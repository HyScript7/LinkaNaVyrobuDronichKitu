package io.gitub.hyscript7.drony;

import io.gitub.hyscript7.drony.crosscutting.SkladFx;
import io.gitub.hyscript7.drony.crosscutting.SkladFxBuilder;
import io.gitub.hyscript7.drony.domain.Material;
import io.gitub.hyscript7.drony.domain.ResourceFactory;
import io.gitub.hyscript7.drony.domain.Sklad;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class HelloController {

    private static final Function<WorkerBuilder, WorkerBuilder> defaultCustomizer = w -> w;

    private static final class WorkerSpecification {
        private final String label;
        private final int instanceCount;
        private final Consumer<WorkerBuilder> mutator;
        private final Function<WorkerBuilder, WorkerBuilder> customizer;

        private int instancesCreated;

        public WorkerSpecification(String label, int instanceCount, Consumer<WorkerBuilder> mutator, Function<WorkerBuilder, WorkerBuilder> customizer) {
            this.label = label;
            this.instanceCount = instanceCount;
            this.mutator = mutator;
            this.customizer = customizer;
            this.instancesCreated = 0;
        }

        BaseWorker instantiateOnce(WorkerBuilder w) {
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

    private final Log logger;
    private final SkladFx sklad;
    private final DroneFactory droneFactory;
    private final ResourceFactory rf;

    public HelloController() {
        this.logger = new Log("USER");
        this.rf = new ResourceFactory();
        this.sklad = createSklad(rf);

        WorkerDirector workerDirector = new WorkerDirector(sklad, rf);
        WorkerBuilderImpl workerBuilder = new WorkerBuilderImpl();

        List<WorkerSpecification> desiredWorkers = getWorkerSpecifications(workerDirector);

        List<BaseWorker> workers = new ArrayList<>();

        for (WorkerSpecification spec : desiredWorkers) {
            for (int i = 0; i < spec.instanceCount; i++) {
                workers.add(spec.instantiateOnce(workerBuilder));
            }
        }

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
                new WorkerSpecification("VYROBCE-DESKA", 1, workerDirector::buildRidiciDeskaWorker, defaultCustomizer),
                new WorkerSpecification("SESTAVITEL", 2, workerDirector::buildKitWorker, w -> w.pocitadlo(assemblerCounter))
        );
    }

    @FXML
    private TableView<Map.Entry<Material, Integer>> table;

    @FXML
    private TableColumn<Map.Entry<Material, Integer>, String> materialColumn;

    @FXML
    private TableColumn<Map.Entry<Material, Integer>, Integer> amountColumn;

    @FXML
    private Button pauseButton;

    private final ObservableList<Map.Entry<Material, Integer>> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        materialColumn.setSortable(false);

        amountColumn.setSortable(true);

        materialColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getKey().toString()));

        amountColumn.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getValue()));

        table.setItems(tableData);

        sklad.addListener(obs -> Platform.runLater(() -> renderTable(sklad.getStatus())));

        renderTable(sklad.getStatus());
        pauseButton.setText("Resume");
    }

    @FXML
    protected void onStartButtonClick() {
        if (!paused) {
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
        paused = true;
        pauseButton.setText("Resume");
        logger.info("STOP");
        droneFactory.stop();
    }

    private boolean paused = true;

    @FXML
    protected void onPauseButtonClick() {
        if (paused) {
            onStartButtonClick();
        } else {
            onStopButtonClick();
        }
    }

    private final List<Material> orderedList;

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
}