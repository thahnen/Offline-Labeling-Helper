package RDTLH;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.*;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASS CONTROLLER
 *      ===========================
 *
 *      Alle FXML-Elmente, die es gibt
 *
 *      Model model, das alle Daten beinhaltet!
 *
 ***********************************************************************************************************************/


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


    /**
     *  Handler loadVideo (wenn Button "Video laden" gedrueckt)
     *  => liest ein Video ein: in der Version Frame fuer Frame, was lange dauert und nicht gross sein darf!
     *  => liest die Labels ein: in der Version ueber JSON
     *
     *  TODO: ggf anstatt eines FileChooser's für ein Video + Label-Datei, DirectoryChooser nehmen für Frames + Label-Datei
     *  TODO: => dann müssen nicht ALLE Frames des Videos eingelesen werden sondern kann dynamischer gemacht werden!
     *
     *  TODO: mehr Formate unterstuetzen fuer die Label und fuer das Video/ die Frames!
     *
     *  TODO: bei Fehlern ein PopUp oeffnen mit entsprechendem Text!
     */
    @FXML protected void loadVideo(ActionEvent event) {
        FileChooser chooser = new FileChooser();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nix") || os.contains("nux")) {
            // Linux
            chooser.setInitialDirectory(new File(System.getenv("HOME")));
        } else {
            // Windows oder etwas anderes!
        }

        chooser.setTitle("Video und Label-Datei auswaehlen!");

        List<File> dateien = chooser.showOpenMultipleDialog(((Node) event.getSource()).getScene().getWindow());
        if (dateien != null) {
            // 1) Es muessen genau 2 Dateien sein!
            // 2) Eine der Dateien muss ein Video sein, das andere eine JSON-Datei
            if (dateien.size() != 2) {
                /**
                 *  FEHLERBEHANDLUNG: ANZAHL DATEIEN != 2
                 */
                System.out.println("Es wurden nicht genau zwei Dateien ausgewaehlt!");
                return;
            } else if (!(dateien.get(0).getName().endsWith(".json") || dateien.get(1).getName().endsWith(".json"))) {
                /**
                 *  FEHLERBEHANDLUNG: KEINE JSON-DATEI AUSGEWAEHLT
                 */
                System.out.println("Es wurde keine JSON-Datei mit ausgewaehlt!");
                return;
            } else if (!(dateien.get(0).getName().endsWith(".avi") || dateien.get(1).getName().endsWith(".avi"))) {
                /**
                 *  FEHLERBEHANDLUNG: KEINE AVI-VIDEO-DATEI AUSGEWAEHLT
                 */
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


            // 1) JSON-Datei einlesen
            JSONArray array;
            try {
                array = (JSONArray) new JSONParser().parse(new FileReader(label.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                // sollte eigentlich vorkommen koennen!
                System.out.println("Irgendwelche FileNotFound-Fehler bei JSON");
                return;
            } catch (IOException e) {
                /**
                 *  FEHLERBEHANDLUNG: IRGENDEIN FEHLER BEIM EINLESEN
                 */
                System.out.println("Irgendwelche IO Fehler bei JSON");
                return;
            } catch (ParseException e) {
                /**
                 *  FEHLERBEHANDLUNG: IRGENDWAS STIMMT MIT DEM JSON NICHT
                 */
                System.out.println("Irgendwelche JSON-Parser Fehler bei JSON");
                return;
            }

            ArrayList<FrameData> frame_info = new ArrayList<>();
            for (Object o : array) {
                /**
                 *  JSON sollte wie folgt aussehen (ist ein Array):
                 *
                 *  {
                 *      "frame_nr" : Int,
                 *      "image_url" : String    => ist egal!
                 *      "external_id" : String  => ist egal!
                 *      "prediction_label" {
                 *          "object" : [
                 *              {
                 *                  "label_id" : Int
                 *                  "geometry" : [
                 *                      { "x" : Int, "y" : Int },   => P1       P1->--P2
                 *                      { "x" : Int, "y" : Int },   => P2       |      |
                 *                      { "x" : Int, "y" : Int },   => P3       |      |
                 *                      { "x" : Int, "y" : Int }    => P4       P4--<-P3
                 *                  ]
                 *              },
                 *              [...]
                 *          ]
                 *      }
                 *  }
                 */
                JSONObject info = (JSONObject) o;
                ArrayList<Label> found_labels = new ArrayList<>();

                // TODO: das hier hinter eigentlich noch in einen Try-Catch-Block einbauen, ich nehme an, dass das so vorliegt!!!
                int frame_nr = (int) ((long) info.get("frame_nr"));

                for (Object obj : (JSONArray)((JSONObject)info.get("prediction_label")).get("object")) {
                    JSONObject label_obj = (JSONObject) obj;

                    // TODO: hier genauer gucken, vlt muss das zum casten etwas auseinander genommen werden!
                    JSONObject[] geometry = (JSONObject[])((JSONArray) label_obj.get("geometry")).toArray();

                    int label_id = (int) label_obj.get("label_id");
                    Point2D p1 = new Point2D(
                            (int) geometry[0].get("x"),
                            (int) geometry[0].get("y")
                    );
                    Point2D p2 = new Point2D(
                            (int) geometry[1].get("x"),
                            (int) geometry[1].get("y")
                    );
                    Point2D p3 = new Point2D(
                            (int) geometry[2].get("x"),
                            (int) geometry[2].get("y")
                    );
                    Point2D p4 = new Point2D(
                            (int) geometry[3].get("x"),
                            (int) geometry[3].get("y")
                    );

                    found_labels.add(new Label(
                            label_id, p1, p2, p3, p4
                    ));
                }

                frame_info.add(new FrameData(
                        frame_nr, found_labels
                ));
            }
            this.model.setFrameInfo(frame_info);


            // 2) Video(-Frames) einlesen
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
        } else {
            /**
             *  FEHLERBEHANDLUNG: NICHTS AUSGEWAEHLT
             */
        }
    }


    /**
     *  Handler getLastFrames (wenn Button "< Zurück" gedrueckt)
     *  => das vorherige Frame wird im aktuellen Frame angezeigt und das aktuelle im naechsten!
     */
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
     *  Handler getNextFrame (wenn Button "Weiter >" gedrueckt)
     *  => das naechste Frame wird im aktuellen Frame angezeigt und das uebernaechste im naechsten!
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


    /*******************************************************************************************************************
     *
     *      TODO: JEDEM FXML-ELMENT einen Handler hinzufuegen, wenn irgendein Button gedrueckt wurde!
     *
     *******************************************************************************************************************/
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
