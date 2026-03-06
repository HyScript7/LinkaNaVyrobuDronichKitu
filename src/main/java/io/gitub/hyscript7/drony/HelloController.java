package io.gitub.hyscript7.drony;

import io.gitub.hyscript7.drony.crosscutting.SkladFx;
import io.gitub.hyscript7.drony.crosscutting.SkladFxBuilder;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Material;
import io.gitub.hyscript7.drony.domain.ResourceFactory;
import io.gitub.hyscript7.drony.factory.DroneFactory;
import io.gitub.hyscript7.drony.factory.SkladDirector;
import io.gitub.hyscript7.drony.factory.WorkerDirector;
import io.gitub.hyscript7.drony.workers.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class HelloController {
    private static final Function<ComponentWorkerBuilder, ComponentWorkerBuilder> defaultCustomizer = w -> w;
    private final Log logger;
    private final SkladFx sklad;
    private final DroneFactory droneFactory;
    private final ResourceFactory rf;
    private final ObservableList<Map.Entry<Material, Integer>> tableData = FXCollections.observableArrayList();
    private final List<Material> orderedList;
    @FXML
    private TableView<Map.Entry<Material, Integer>> table;
    @FXML
    private TableColumn<Map.Entry<Material, Integer>, String> materialColumn;
    @FXML
    private TableColumn<Map.Entry<Material, Integer>, Integer> amountColumn;
    @FXML
    private Button pauseButton;
    @FXML
    private TextField productionTargetTextField;
    private Komponenta productionTargetComponent;
    private boolean paused = true;

    public HelloController() {
        this.logger = new Log("USER");
        this.rf = new ResourceFactory();
        this.sklad = createSklad(rf);

        WorkerDirector workerDirector = new WorkerDirector(sklad, rf);
        ComponentWorkerBuilderImpl workerBuilder = new ComponentWorkerBuilderImpl();

        List<WorkerSpecification> desiredWorkers = getWorkerSpecifications(workerDirector);

        List<BaseWorker> workers = new ArrayList<>();

        for (WorkerSpecification spec : desiredWorkers) {
            for (int i = 0; i < spec.instanceCount; i++) {
                workers.add(spec.instantiateOnce(workerBuilder));
            }
        }

        // Skladník
        // Max u supply range je inclusive (vlastní impl., viz work v SupplierWorker)
        workers.add(new SupplierWorker(new Log("SKLADNIK", Log.Level.INFO), sklad, Map.of(
                rf.createHlinik(), new SupplierWorker.SupplyRange(1, 1000, "g"),
                rf.createPlast(), new SupplierWorker.SupplyRange(1, 1000, "g"),
                rf.createChips(), new SupplierWorker.SupplyRange(1, 100, "ks")
        )));

        this.droneFactory = new DroneFactory(workers);

        this.orderedList = List.of(rf.createKit(), rf.createHlinik(), rf.createPlast(), rf.createChips(), rf.createRam(), rf.createSadaVrtuli(), rf.createRidiciDeska());
    }

    private static SkladFx createSklad(ResourceFactory rf) {
        SkladDirector skladDirector = new SkladDirector(rf);
        SkladFxBuilder skladFxBuilder = new SkladFxBuilder();

        skladDirector.createDefaultSklad(skladFxBuilder);
        // Apply customizations here...

        return skladFxBuilder.build();
    }

    private static List<WorkerSpecification> getWorkerSpecifications(WorkerDirector workerDirector) {
        Counter assemblerCounter = new CounterImpl();
        return List.of(
                new WorkerSpecification("VYROBCE-RAM", 1, workerDirector::buildRamWorker, defaultCustomizer),
                new WorkerSpecification("VYROBCE-VRTULE", 1, workerDirector::buildSadaVrtuliWorker, defaultCustomizer),
                // T4 - successDelay je přičítáno k základnímu delay 1 sekundy (v ComponentWorker)
                new WorkerSpecification("VYROBCE-DESKA", 1, workerDirector::buildRidiciDeskaWorker, w -> w.successDelay(Duration.ofSeconds(1))),
                new WorkerSpecification("SESTAVITEL", 2, workerDirector::buildKitWorker, w -> w.pocitadlo(assemblerCounter))
        );
    }

    @FXML
    public void initialize() {
        productionTargetComponent = rf.createKit();

        materialColumn.setSortable(false);

        amountColumn.setSortable(true);

        materialColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getKey().toString()));

        amountColumn.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getValue()));

        table.setItems(tableData);

        sklad.addListener(obs -> Platform.runLater(() -> renderTable(sklad.getStatus())));
        sklad.addListener(obs -> Platform.runLater(() -> {
            if (isProductionTargetReached()) {
                if (!paused) {
                    stop();
                    createAndShowSummaryAlert();
                }
            }
        }));

        renderTable(sklad.getStatus());
        pauseButton.setText("Resume");
    }

    // T3 - Statistiky
    private void createAndShowSummaryAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Production Target Reached");
        alert.setHeaderText("Production Target Reached - Post Production Summary");
        StringBuilder content = new StringBuilder();
        content.append("The factory has been stopped.\nBelow is a summary of each worker's performance:\n");
        int drones = 0;
        var kit = rf.createKit();
        for (BaseWorker worker : droneFactory.getWorkers()) {
            if (worker instanceof ComponentWorker componentWorker) {
                content.append(componentWorker.getLogger().getName()).append(": ").append(componentWorker.getInternalCounterValue()).append("x ").append(componentWorker.getKomponenta().getNazev()).append("\n");
                if (componentWorker.getKomponenta().equals(kit)) {
                    drones += componentWorker.getInternalCounterValue();
                }
            }
        }
        content.append("\n\nCreated ").append(drones).append(" drone kits out of ").append(getProductionTargetValue());
        alert.setContentText(content.toString().strip());
        alert.show();
    }

    private boolean isProductionTargetReached() {
        return sklad.getAmount(productionTargetComponent) >= getProductionTargetValue();
    }

    private int prodTargetCopy = -1;

    private int getProductionTargetValue() {
        if (paused) {
            prodTargetCopy = -1;
        }
        if (prodTargetCopy != -1) {
            return prodTargetCopy;
        }
        try {
            int val = Integer.parseInt(productionTargetTextField.getText().strip());
            if (val <= 0) {
                throw new NumberFormatException("Hodnota musí být > 0");
            }
            prodTargetCopy = val;
            return val;
        } catch (NumberFormatException e) {
            if (!paused) {
                stop();
            }
            return 0;
        }
    }

    @FXML
    protected void onStartButtonClick() {
        if (!paused) {
            return;
        }
        if (getProductionTargetValue() < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You must specify a production target using the slider!");
            alert.setTitle("Command Aborted");
            alert.show();
            return;
        }
        if (isProductionTargetReached()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Production target reached.\nIf you wish to restart production, please increase the target.");
            alert.setTitle("Command Aborted");
            alert.show();
            return;
        }
        paused = false;
        pauseButton.setText("Pause");
        logger.info("START");
        droneFactory.start();
    }

    @FXML
    protected void onStopButtonClick() {
        if (paused) {
            return;
        }
        logger.info("STOP");
        stop();
    }

    private void stop() {
        paused = true;
        pauseButton.setText("Resume");
        droneFactory.stop();
        prodTargetCopy = -1;
    }

    @FXML
    protected void onPauseButtonClick() {
        if (paused) {
            onStartButtonClick();
        } else {
            onStopButtonClick();
        }
    }

    // Na tohle jsem musel použít chata protože wtf
    private void renderTable(Map<Material, Integer> skladContents) {
        List<Map.Entry<Material, Integer>> sorted = skladContents.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> {
                    int index = -1;
                    for (int i = 0; i < orderedList.size(); i++) {
                        if (orderedList.get(i).equals(e.getKey())) {
                            index = i;
                            break;
                        }
                    }
                    return index == -1 ? Integer.MAX_VALUE : index; // unknown materials go to the end
                }))
                .toList();
        tableData.setAll(sorted);
    }

    private static final class WorkerSpecification {
        private final String label;
        private final int instanceCount;
        private final Consumer<ComponentWorkerBuilder> mutator;
        private final Function<ComponentWorkerBuilder, ComponentWorkerBuilder> customizer;

        private int instancesCreated;

        public WorkerSpecification(String label, int instanceCount, Consumer<ComponentWorkerBuilder> mutator, Function<ComponentWorkerBuilder, ComponentWorkerBuilder> customizer) {
            this.label = label;
            this.instanceCount = instanceCount;
            this.mutator = mutator;
            this.customizer = customizer;
            this.instancesCreated = 0;
        }

        BaseWorker instantiateOnce(ComponentWorkerBuilder w) {
            mutator.accept(w);
            String l = label.toUpperCase();
            if (instanceCount > 1) {
                l += "-" + (++instancesCreated);
            }
            w.logger(new Log(l));
            customizer.apply(w);
            return w.getResult();
        }
    }
}