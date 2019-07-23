package com.thahnen;

import com.thahnen.util.SysUTIL;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;


public class Main extends Application {

    private static int error_nr;

    /**
     *  Runs after main!
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml;
        String title;

        switch (error_nr) {
            case 1:
                fxml = "OSNotSupportedUI.fxml";
                title = "OS not supported :(";
                break;
            case 2:
                fxml = "OpenCVNotFoundUI.fxml";
                title = "OpenCV 3.4.2 not found!";
                break;
            case 3:
                fxml = "OpenCVNotLoadableUI.fxml";
                title = "OpenCV 3.4.2 not loadable!";
                break;
            default:
                // TODO: die Monitorgroesse ueberpruefen, ggf unterschiedliche FXML laden, -> bei der Standard-UI!
                fxml = "UI.fxml";
                title = "RTD Labeling Helper";
                break;
        }

        // FXML laden
        Scene scene = new Scene((Pane) FXMLLoader.load(getClass().getClassLoader().getResource(fxml)));
        primaryStage.setScene(scene);

        // Titel setzen
        primaryStage.setTitle(title);

        // Handhaben, was bei der normalen UI passieren soll!
        if (error_nr == 0) {
            primaryStage.setOnCloseRequest((new EventHandler<>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    // TODO: Dialog zum speichern etc!
                    System.out.println("Wird geschlossen!");
                }
            }));

            // Einen EventHandler dafuer setzen, wenn Fenstergroesse veraendert wird!
            // TODO: kommt noch, sollte eigentlich nur die FXML-Elemente anpassen!
            primaryStage.widthProperty().addListener((observer, oldValue, newValue) -> {
                System.out.println("Fensterbreite veraendert von " + oldValue + " zu " + newValue);
            });
            primaryStage.heightProperty().addListener((observer, oldValue, newValue) -> {
                System.out.println("Fensterhiehe veraendert von " + oldValue + " zu " + newValue);
            });
        }

        // UI anzeigen
        primaryStage.show();
    }


    /**
     *  Runs on start
     */
    public static void main(String[] args) {
        error_nr = 0;

        /** OpenCV 3.4.2 soll erstmal nur im Home-Verzeichnis unterstützt werden! */
        String folder = "";
        switch (SysUTIL.getOS()) {
            case WINDOWS:
                folder = "\\opencv-3.4.2\\build\\lib\\libopencv_java342.dll";
                break;
            case MACOSX:
                folder = "/opencv-3.4.2/build/lib/libopencv_java342.dylib";
                break;
            case LINUX:
                folder = "/opencv-3.4.2/build/lib/libopencv_java342.so";
                break;
            default:
                // OS not supported
                error_nr = 1;
        }

        if (error_nr == 0) {
            String library_path = SysUTIL.getHomeDir() + folder;
            if (!new File(library_path).exists()) {
                // OpenCV not found
                error_nr = 2;
            } else {
                try {
                    System.load(library_path);
                } catch (Exception e) {
                    // OpenCV not loadable
                    error_nr = 3;
                }
            }
        }

        launch(args);
    }
}
