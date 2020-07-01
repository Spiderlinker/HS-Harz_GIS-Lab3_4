package de.hsharz.gis.ui;

import java.io.File;

import de.hsharz.gis.DataReader;
import de.hsharz.gis.IDW;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainWindow {

    private Stage      stage;

    private BorderPane root;
    private Button     btnLoadValues;
    private TabPane    tabPane;

    public MainWindow(final Stage stage) {
        this.stage = stage;

        this.createWidgets();
        this.setupInteractions();
        this.addWidgets();
    }

    private void createWidgets() {
        this.root = new BorderPane();
        this.root.setPadding(new Insets(10));

        this.btnLoadValues = new Button("Neue Messwerte laden...");

        this.tabPane = new TabPane();
    }

    private void setupInteractions() {
        this.btnLoadValues.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Datei mit Messwerten auswählen");
            fileChooser.getExtensionFilters().addAll( // Eigene ExtensionFilters hinzufügen
                    new ExtensionFilter("Alle Dateien", "*.*") // Alle Dateien
            );
            File selectedFile = fileChooser.showOpenDialog(this.stage);
            // Wurde eine Datei ausgewählt?
            if (selectedFile != null) {
                // Neuen Tab erstellen mit gewählter Datei als Messwertedatei
                this.createNewTab(selectedFile);
            }
        });
    }

    private void createNewTab(final File file) {
        try {
            IDW idw = new IDW(DataReader.readValues(file));

            Tab tab = new Tab(file.getName());
            tab.setContent(new IDWView(idw).getPane());
            this.tabPane.getTabs().add(tab);
            this.tabPane.getSelectionModel().select(tab);

        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorLoadingFile(e.getMessage());
        }
    }

    private void showErrorLoadingFile(final String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Fehler beim Laden der Datei!");
        alert.setGraphic(new Label("Fehler beim Lesen der angegebenen Datei: " + error));
        alert.show();
    }

    private void addWidgets() {
        this.root.setTop(this.btnLoadValues);
        this.root.setCenter(this.tabPane);
    }

    public Pane getPane() {
        return this.root;
    }

}
