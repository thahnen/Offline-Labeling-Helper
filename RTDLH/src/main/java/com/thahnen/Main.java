package com.thahnen;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene scene = new Scene((Pane) FXMLLoader.load(getClass().getClassLoader().getResource("UI.fxml")));
        // braucht man nicht, wenn man CSS ueber FXML einbindet! => brauchen wir aber ggf wenn man neue Elemente hinzufuegt
        //scene.getStylesheets().add(getClass().getResource("res/UI.css").toExternalForm());
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


    public static void main(String[] args) {
        // TODO: hier die Pfade für Windows / *nix anpassen: OpenCV 3.4.2 soll erstmal nur im Home-Verzeichnis installiert sein!
        // ueber System.getenv("HOME") gemacht, damit es Konten-/BS-unabhaengig ist!
        System.load(System.getenv("HOME") + "/opencv-3.4.2/build/lib/libopencv_java342.so");
        launch(args);
    }
}
