package RDTLH;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Controller {
    @FXML private Button loadBtn;
    @FXML private Button backBtn;
    @FXML private Button nextBtn;
    @FXML private ImageView currentFrame;
    @FXML private ImageView nextFrame;

    private Model model;


    public Controller() {
        this.model = new Model();
    }

    @FXML protected void loadVideo(ActionEvent event) {
        FileChooser chooser = new FileChooser();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            // Linux
            chooser.setInitialDirectory(new File(System.getenv("HOME")));
        } else {
            // Windows
        }

        chooser.setTitle("Video und Label-Datei auswaehlen!");

        List<File> dateien = chooser.showOpenMultipleDialog(((Node) event.getSource()).getScene().getWindow());
        if (dateien != null) {
            // 1. Datei muss ein Video sein
            // 2. Datei muss eine JSON-Datei sein!
            if (dateien.size() != 2) {
                // Fehlerbehandlung, es muessen genau zwei Datein ausgewaehlt werden!
                System.out.println("Es wurden nicht genau zwei Dateien ausgewaehlt!");
                return;
            } else if (!(dateien.get(0).getName().endsWith(".json") || dateien.get(1).getName().endsWith(".json"))) {
                // Fehlerbehandlung, es muss eine JSON-Datei dabei sein die die Label enthaelt!
                System.out.println("Es wurde keine JSON-Datei mit ausgewaehlt!");
                return;
            } else if (!(dateien.get(0).getName().endsWith(".avi") || dateien.get(1).getName().endsWith(".avi"))) {
                // Fehlerbehandlung, es muss eine AVI-Datei dabei sein, die das Video enthaelt!
                System.out.println("Es wurde keien AVI-Datei mit ausgewaehlt!");
                return;
            }

            File video, label;
            if (dateien.get(0).getName().endsWith(".json")) {
                label = dateien.get(0);
                video = dateien.get(1);
            } else {
                label = dateien.get(1);
                video = dateien.get(0);
            }

            // Nur Videos unter 50mb nehmen!
            System.out.println("Videogroesse: " + (video.length() / (1024*1024)) + "mb");
            if ((video.length() / (1024*1024)) > 50) {
                System.out.println("Das angegebene Video ist zu gross!");
                return;
            }

            // Die einzelnen Frames verarbeiten (kann einen Moment dauern!)
            VideoCapture cap = new VideoCapture(video.getAbsolutePath());
            ArrayList<Image> frames = new ArrayList<>();
            while (true) {
                Mat frame = new Mat();
                if (cap.read(frame)) {
                    // Convert the frame back to Grayscale because it got converted!
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

                    // Convert into an image for the ImageView!
                    MatOfByte buffer = new MatOfByte();
                    Imgcodecs.imencode(".png", frame, buffer);
                    frames.add(new Image(new ByteArrayInputStream(buffer.toArray())));
                } else {
                    break;
                }
            }

            // Vorher noch ueberpruefen, ob die Liste nicht vielleicht sogar leer ist!
            this.model.setFrames(frames);

            try {
                this.currentFrame.setImage(model.getFrameByIndex(0));
                this.nextFrame.setImage(model.getFrameByIndex(1));
            } catch (Exception e) {
                System.out.println(e);
                return;
            }

            this.model.setCurrentFrameId(0);
        }
    }

    @FXML protected void getLastFrame(ActionEvent event){
        if (this.model.getFramesAmount() == 0) {
            return;
        }

        int id = this.model.getCurrentFrameId();
        // DEBUG:
        System.out.println("Vorher: " + id);
        if (id == 0) {
            // Man kann nicht weiter zurueck gehen, es ist das allererste Frame ausgewaehlt!
            return;
        }

        try {
            this.nextFrame.setImage(model.getFrameByIndex(id));
            this.currentFrame.setImage(model.getFrameByIndex(--id));
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        this.model.setCurrentFrameId(id);
        // DEBUG:
        System.out.println("Nachher: " + id);
    }


    /**
     *  Handler getNextFrame (triggered wenn man auf den Button "Weiter >" drueckt)
     *  => das naechste Frame wird zum aktuellen Frame gesetzt und das naechste Frame auf das uebernaechste (wenn vorhanden)
     */
    @FXML protected void getNextFrame(ActionEvent event){
        int amount = this.model.getFramesAmount();
        if (amount == 0) {
            return;
        }

        int id = this.model.getCurrentFrameId();
        // DEBUG:
        System.out.println("Vorher: " + id);
        if (id == amount-1) {
            // Man kann nicht weiter vorwaerts gehen, es ist das allerletzte Frame ausgewaehlt!
            return;
        } else if (id == amount-2) {
            this.nextFrame.setImage(null);
        } else {
            try {
                this.nextFrame.setImage(model.getFrameByIndex(id+2));
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
        }

        try {
            this.currentFrame.setImage(model.getFrameByIndex(++id));
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        this.model.setCurrentFrameId(id);
        // DEBUG:
        System.out.println("Nachher: " + id);
    }


    @FXML protected void backBtnHandleKeyPressed(KeyEvent event) {
        // Hier ueberpruefen, wo der Fokus liegt und ob Enter gedrueckt wurde!
        // => dann muss der Fokus ggf umgesetzt werden und bei Enter soll doppelte Aktion verhindert werden!
        System.out.println(event.getCode());
        this.getLastFrame(null);
    }


    @FXML protected void nextBtnHandleKeyPressed(KeyEvent event) {
        // Hier ueberpruefen, wo der Fokus liegt und ob Enter gedrueckt wurde!
        // => dann muss der Fokus ggf umgesetzt werden und bei Enter soll doppelte Aktion verhindert werden!
        System.out.println(event.getCode());
        this.getNextFrame(null);
    }

    // TODO: noch einen EventHandler fur den "Video laden" Button!
}
