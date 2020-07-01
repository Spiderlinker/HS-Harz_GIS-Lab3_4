package de.hsharz.gis;

import java.io.FileNotFoundException;

import de.hsharz.gis.ui.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Labor extends Application {

    public static void main(final String[] args) throws FileNotFoundException {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {

        BorderPane root = new BorderPane(new MainWindow(stage).getPane());
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

}
