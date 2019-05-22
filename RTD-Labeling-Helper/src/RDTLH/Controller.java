package RDTLH;

import RDTLH.data.FrameData;
import RDTLH.util.FileUtil;
import RDTLH.util.UIUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE CONTROLLER
 *      ============================
 *
 *      - Alle FXML-Elmente, die es gibt
 *      - Model model, das alle Daten beinhaltet!
 *
 *      Handler (Buttons):
 *      - loadVideo         =>      laed ein Video(/Frames) und Label ein
 *      - getLastFrame      =>      setzt die ImageView's neu
 *      - getNextFrame      =>      setzt die ImageView's neu
 *      - handleBtnPress    =>      handhabt die Steuerung per Tastendruck      => TODO: fehlt noch
 *
 *      Handler (ImageView):
 *      - handleMouseClick  =>      handhabt das Auswaehlen von einzelnen Labels
 *
 ***********************************************************************************************************************/


public class Controller {
    @FXML private Button loadBtn;
    @FXML private Button backBtn;
    @FXML private Button nextBtn;
    @FXML private Canvas lastFrame;
    @FXML private Canvas currentFrame;
    @FXML private Canvas nextFrame;

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
            /** 1) Dateien auf Richtigkeit ueberpruefen */
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


            /** 2) Labels als FrameData einlesen! */
            try {
                ArrayList<FrameData> frame_info = FileUtil.loadLabelsFromJSON(label);
                this.model.setFrameInfo(frame_info);
            } catch (Exception e) {
                // TODO: das hier muss später noch etwas genauer gemacht werden mit unterschiedlichen Exceptions!
                System.out.println(e);
                return;
            }
            System.out.println("FrameData set: " + this.model.getFrameInfo().size());


            /** 3) Video(-Frames) einlesen */
            // Nur Videos unter 50mb nehmen!
            System.out.println("Videogroesse: " + (video.length() / (1024*1024)) + "mb");
            if ((video.length() / (1024*1024)) > 50) {
                System.out.println("Das angegebene Video ist zu gross!");
                return;
            }

            // Die einzelnen Frames verarbeiten (kann einen Moment dauern!)
            try {
                ArrayList<Image> frames = FileUtil.loadFramesFromVideo(video);
                this.model.setFrames(frames);
            } catch (Exception e) {
                // TODO: das hier muss später noch etwas genauer gemacht werden mit unterschiedlichen Exceptions!
                System.out.println(e);
                return;
            }
            System.out.println("Frames set: " + this.model.getFramesAmount());


            /** 4) Initiale Werte (Frames + Label) setzen */
            this.model.setCurrentFrameId(0);

            FrameData current, next;
            try {
                current = this.model.getFrameDataByFrameNr(0);
            } catch (IndexOutOfBoundsException e) {
                current = null;
            }

            try {
                next = this.model.getFrameDataByFrameNr(1);
            } catch (IndexOutOfBoundsException e) {
                next = null;
            }

            UIUtil.updateCanvas(currentFrame, this.model.getFrameByIndex(0), current);
            UIUtil.updateCanvas(nextFrame, this.model.getFrameByIndex(1), next);
        } else {
            /**
             *  FEHLERBEHANDLUNG: NICHTS AUSGEWAEHLT
             */
        }
    }


    /**
     *  Handler getLastFrames (wenn Button "< Zurück" gedrueckt)
     *  => das vorherige Frame wird im aktuellen Frame angezeigt und das aktuelle im naechsten!
     *  => die Labels werden eingezeichnet, ggf nicht gespeicherte (aber veraenderte) Labels abfangen?
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
            // => ergo ist das "lastFrame" leer!
            return;
        } else if (id == 1) {
            /** Es muss das "lastFrame" noch geloescht werden! */
            UIUtil.clearCanvas(this.lastFrame);

            FrameData current, next;
            try {
                current = this.model.getFrameDataByFrameNr(id-1);
            } catch (IndexOutOfBoundsException e) {
                current = null;
            }

            try {
                next = this.model.getFrameDataByFrameNr(id);
            } catch (IndexOutOfBoundsException e) {
                next = null;
            }

            UIUtil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id-1), current);
            UIUtil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id), next);
        } else {
            FrameData last, current, next;
            try {
                last = this.model.getFrameDataByFrameNr(id-2);
            } catch (IndexOutOfBoundsException e) {
                last = null;
            }

            try {
                current = this.model.getFrameDataByFrameNr(id-1);
            } catch (IndexOutOfBoundsException e) {
                current = null;
            }

            try {
                next = this.model.getFrameDataByFrameNr(id);
            } catch (IndexOutOfBoundsException e) {
                next = null;
            }

            UIUtil.updateCanvas(this.lastFrame, this.model.getFrameByIndex(id-2), last);
            UIUtil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id-1), current);
            UIUtil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id), next);
        }

        this.model.setCurrentFrameId(--id);
        // DEBUG:
        System.out.println("Nachher: " + id);
    }


    /**
     *  Handler getNextFrame (wenn Button "Weiter >" gedrueckt)
     *  => das naechste Frame wird im aktuellen Frame angezeigt und das uebernaechste im naechsten!
     *  => die Labels werden eingezeichnet, ggf nicht gespeicherte (aber veraenderte) Labels abfangen?
     */
    @FXML protected void getNextFrame(ActionEvent event){
        int amount = this.model.getFramesAmount();
        if (amount == 0) {
            return;
        }


        int id = this.model.getCurrentFrameId();
        // DEBUG:
        System.out.println("Vorher: " + id + ", " + amount);
        if (id == amount-1) {
            // Man kann nicht weiter vorwaerts gehen, es ist das allerletzte Frame ausgewaehlt!
            return;
        } else if (id == amount-2) {
            /** Es muss das "nextFrame" noch geloescht werden! */
            UIUtil.clearCanvas(this.nextFrame);

            FrameData last, current;
            try {
                last = this.model.getFrameDataByFrameNr(id);
            } catch (IndexOutOfBoundsException e) {
                last = null;
            }

            try {
                current = this.model.getFrameDataByFrameNr(id+1);
            } catch (IndexOutOfBoundsException e) {
                current = null;
            }

            UIUtil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id), last);
            UIUtil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id+1), current);
        } else {
            FrameData last, current, next;
            try {
                last = this.model.getFrameDataByFrameNr(id);
            } catch (IndexOutOfBoundsException e) {
                last = null;
            }

            try {
                current = this.model.getFrameDataByFrameNr(id+1);
            } catch (IndexOutOfBoundsException e) {
                current = null;
            }

            try {
                next = this.model.getFrameDataByFrameNr(id+2);
            } catch (IndexOutOfBoundsException e) {
                next = null;
            }

            UIUtil.updateCanvas(this.lastFrame, this.model.getFrameByIndex(id), last);
            UIUtil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id+1), current);
            UIUtil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id+2), next);
        }

        this.model.setCurrentFrameId(++id);
        // DEBUG:
        System.out.println("Nachher: " + id);
    }


    /*******************************************************************************************************************
     *
     *      TODO: JEDEM FXML-ELMENT einen gemeinsamen Handler hinzufuegen, wenn irgendein Button gedrueckt wurde!
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
