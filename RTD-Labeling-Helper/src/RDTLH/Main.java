package RDTLH;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader((getClass().getResource("res/UI.fxml")));
        Controller controller = loader.getController();
        Scene scene = new Scene((Pane) loader.load());
        primaryStage.setScene(scene);

        // Titel setzen
        primaryStage.setTitle("RTD Labeling Helper");

        // Einen EventHandler dafuer setzen, wenn Fenster geschlossen werden soll
        primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                // do it, just do it!
            }
        }));
        primaryStage.show();
    }


    public static void main(String[] args) {
        // Loading is necessary!
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
