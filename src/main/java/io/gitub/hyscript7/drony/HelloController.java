package io.gitub.hyscript7.drony;

import io.gitub.hyscript7.drony.crosscutting.SkladFx;
import io.gitub.hyscript7.drony.crosscutting.SkladFxBuilder;
import io.gitub.hyscript7.drony.domain.Material;
import io.gitub.hyscript7.drony.domain.ResourceFactory;
import io.gitub.hyscript7.drony.domain.Sklad;
import io.gitub.hyscript7.drony.domain.SkladDirector;
import io.gitub.hyscript7.drony.workers.WorkerBuilderImpl;
import io.gitub.hyscript7.drony.workers.WorkerDirector;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HelloController {
    private final Log logger;
    private final SkladFx sklad;
    private final DroneFactory droneFactory;
    private final ResourceFactory rf;

    public HelloController() {
        this.logger = new Log("USER");
        this.rf = new ResourceFactory();
        SkladDirector skladDirector = new SkladDirector(rf);
        SkladFxBuilder skladFxBuilder = new SkladFxBuilder();
        skladDirector.createDefaultSklad(skladFxBuilder);
        this.sklad = skladFxBuilder.build();
        this.droneFactory = new DroneFactory(new WorkerDirector(sklad, rf), new WorkerBuilderImpl(), 2);
        this.orderedList = List.of(rf.createHlinik(), rf.createPlast(), rf.createChips(), rf.createRam(), rf.createSadaVrtuli(), rf.createRidiciDeska(), rf.createKit());
    }

    @FXML
    private TableView<Map.Entry<Material, Integer>> table;

    @FXML
    private TableColumn<Map.Entry<Material, Integer>, String> materialColumn;

    @FXML
    private TableColumn<Map.Entry<Material, Integer>, Integer> amountColumn;

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
    }

    @FXML
    protected void onStartButtonClick() {
        logger.info("start");
        droneFactory.start();
    }

    @FXML
    protected void onStopButtonClick() {
        logger.info("stop");
        droneFactory.stop();
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