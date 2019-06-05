package com.thahnen;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = new Scene((Pane) FXMLLoader.load(getClass().getClassLoader().getResource("UI.fxml")));
        primaryStage.setScene(scene);

        // Titel setzen
        primaryStage.setTitle("RTD Labeling Helper");

        // Einen EventHandler dafuer setzen, wenn Fenster geschlossen werden soll
        primaryStage.setOnCloseRequest((new EventHandler<>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                // do it, just do it!
                System.out.println("Wird geschlossen!");
            }
        }));
        primaryStage.show();
    }


    // TODO: FXML-Dateien erstellen für die (beiden) mögichen Fehlerfälle!
    public static void main(String[] args) {
        /** OpenCV 3.4.2 soll erstmal nur im Home-Verzeichnis unterstützt werden! */

        String os = System.getProperty("os.name").toLowerCase();
        String folder;
        if (os.contains("nix") || os.contains("nux")) {
            // Linux + "Unix"
            folder = "/opencv-3.4.2/build/lib/libopencv_java342.so";
        } else if (os.contains("os x")) {
            // macOS
            folder = "/opencv-3.4.2/build/lib/libopencv_java342.dylib";
        } else if (os.contains("win")) {
            // Windows
            folder = "\\opencv-3.4.2\\build\\lib\\libopencv_java342.dll";
        } else {
            // TODO: Pop-Up mit Warnhinweis, dann schliessen! Muss ein eigenes JavaFX-FXML sein! Alert funktioniert nicht!
            System.out.println("OS noch nicht unterstützt, kommt noch: Issue auf GitHub verfassen!");
            System.exit(1);
            return;
        }

        String library_path = System.getenv("HOME") + folder;
        if (!new File(library_path).exists()) {
            // TODO: Pop-Up mit Fehlerhinweis, dann schliessen! Muss ein eigenes JavaFX-FXML sein! Alert funktioniert nicht!
            System.out.println("OpenCV 3.4.2 konnte nicht gefunden werden!");
            System.exit(1);
            return;

        }

        try {
            // ueber System.getenv("HOME") gemacht, damit es Konten-/BS-unabhaengig ist!
            System.load(library_path);
        } catch (Exception e) {
            // TODO: Pop-Up mit Fehlerhinweis, dann schliessen! Muss ein eigenes JavaFX-FXML sein! Alert funktioniert nicht!
            System.out.println("OpenCV 3.4.2 konnte nicht geladen werden!");
            System.exit(1);
            return;
        }

        launch(args);
    }
}
