package com.thahnen;

import com.thahnen.data.FrameData;
import com.thahnen.util.FileUtil;
import com.thahnen.util.UIUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE CONTROLLER
 *      ============================
 *
 *      => der Controller, der an die GUI gebunden ist und alle Aktionen verarbeitet!
 *
 *      - Alle FXML-Elmente, die es gibt
 *      - Model model, das alle Daten beinhaltet!
 *      - UIUtil uiutil, das Veränderungen in der UI handhabt!
 *
 *      Handler (Buttons):
 *      - loadVideo             =>      laed ein Video(/Frames) und Label ein
 *      - saveLabels            =>      speichert die (veränderten) Label ab
 *      - getLastFrame          =>      setzt die ImageView's neu
 *      - getNextFrame          =>      setzt die ImageView's neu
 *      - handleBtnPress        =>      handhabt die Steuerung per Tastendruck
 *
 *      Handler (ImageView):
 *      - handleMouseClick      =>      handhabt das Auswaehlen von einzelnen Labels
 *
 ***********************************************************************************************************************/


public class Controller {
    @FXML private Button loadBtn;
    @FXML private Button saveBtn;
    @FXML private Button saveLabelBtn;
    @FXML private Button backBtn;
    @FXML private Button nextBtn;
    @FXML private Canvas lastFrame;
    @FXML private Canvas currentFrame;
    @FXML private Canvas nextFrame;
    @FXML private TextField txtLabelId;

    private Model model;
    private UIUtil uiutil;

    private int currentSelectedLabelId;


    public Controller() {
        this.model = new Model();
        this.uiutil = new UIUtil();
        this.currentSelectedLabelId = -1;
    }


    /**
     *  Handler loadVideo (wenn Button "Video laden" gedrueckt)
     *  => liest ein Video ein: in der Version Frame fuer Frame, was lange dauert und nicht gross sein darf!
     *  => liest die Labels ein: in der Version ueber JSON
     *
     *  TODO: ggf anstatt eines FileChooser's für ein Video + Label-Datei, DirectoryChooser nehmen für Frames + Label-Datei
     *  TODO: => dann müssen nicht ALLE Frames des Videos eingelesen werden sondern kann dynamischer gemacht werden!
     */
    @FXML protected void loadVideo(ActionEvent event) {
        /** 1) nachfragen, in welchem Format das Video vorliegt */
        /*
        switch (UIUtil.dialogChooseVideoFormat()) {
            case 0:
                // AVI-Video, ergo FileChooser
                break;
            case 1:
                // PNG-Frames, ergo DirectoryChooser (oder auch FileChooser?)
                break;
            case -1:
                // Abbruch
                break;
        }
         */

        /** 2) Video (in angegebenem Format) laden */

        /** 3) nachfragen, in welchem Format die Label vorliegen */
        /*
        switch (UIUtil.dialogChooseLabelFormat()) {
            case 0:
                // JSON-Datei, ergo FileChooser
                break;
            case 1:
                // CSV-Datei, ergo FileChooser
                break;
            case -1:
                // Abbruch
                break;
        }
         */

        /** 4) Label (in angegebenem Format) laden */


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
            // 1) Dateien auf Richtigkeit ueberpruefen
            if (dateien.size() != 2) {
                /*
                 *  FEHLERBEHANDLUNG: ANZAHL DATEIEN != 2
                 */
                System.out.println("Es wurden nicht genau zwei Dateien ausgewaehlt!");
                return;
            } else if (!(dateien.get(0).getName().endsWith(".json") || dateien.get(1).getName().endsWith(".json"))) {
                /*
                 *  FEHLERBEHANDLUNG: KEINE JSON-DATEI AUSGEWAEHLT
                 */
                System.out.println("Es wurde keine JSON-Datei mit ausgewaehlt!");
                return;
            } else if (!(dateien.get(0).getName().endsWith(".avi") || dateien.get(1).getName().endsWith(".avi"))) {
                /*
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


            // 2) Labels als FrameData einlesen!
            try {
                ArrayList<FrameData> frame_info = FileUtil.loadLabelsFromJSON(label);
                this.model.setFrameInfo(frame_info);
            } catch (Exception e) {
                // TODO: das hier muss später noch etwas genauer gemacht werden mit unterschiedlichen Exceptions!
                System.out.println(e);
                return;
            }
            System.out.println("FrameData set: " + this.model.getFrameInfo().size());


            // 3) Video(-Frames) einlesen
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


            // 4) Initiale Werte (Frames + Label) setzen
            this.model.setCurrentFrameId(0);

            FrameData current, next;
            try {
                current = this.model.getFrameDataByFrameNr(0);
            } catch (IndexOutOfBoundsException e) {
                current = null;
                System.out.println("Init: current is null!");
            }

            try {
                next = this.model.getFrameDataByFrameNr(1);
            } catch (IndexOutOfBoundsException e) {
                next = null;
                System.out.println("Init: next is null!");
            }

            this.uiutil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(0), current);
            this.uiutil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(1), next);


            // 5) Alle UI-Elemente aktivieren, die deaktiviert waren
            this.saveBtn.setDisable(false);
            this.loadBtn.setDisable(true);          // bis anständig geregelt ist, was passiert, wenn man neuläd!
            this.currentFrame.setDisable(false);
            this.backBtn.setDisable(false);
            this.nextBtn.setDisable(false);
        } else {
            /*
             *  FEHLERBEHANDLUNG: NICHTS AUSGEWAEHLT
             */
        }
    }


    @FXML protected void saveLabels(ActionEvent event) {
        FileChooser chooser = new FileChooser();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nix") || os.contains("nux")) {
            // Linux
            chooser.setInitialDirectory(new File(System.getenv("HOME")));
        } else {
            // Windows oder etwas anderes!
        }

        chooser.setTitle("JSON-Datei zum abspeichern auswählen!");

        File datei = chooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (datei != null) {
            try {
                FileUtil.saveLabelsToJSON(datei, this.model.getFrameInfo());
            } catch (Exception e) {
                /*
                    FEHLERBEHANDLUNG: KONNTE NICHT ABGESPEICHERT WERDEN
                 */
                System.out.println(e);
            }
        } else {
            /*
                FEHLERBEHANDLUNG: ZU VIEL/ NICHTS AUSGEWÄHLT
             */
            System.out.println("Zu viel oder nichts ausgewählt!");
        }
    }


    /**
     *  Handler getLastFrames (wenn Button "< Zurück" gedrueckt)
     *  => das vorherige Frame wird im aktuellen Frame angezeigt und das aktuelle im naechsten!
     *  => die Labels werden eingezeichnet, ggf nicht gespeicherte (aber veraenderte) Labels abfangen?
     *  TODO: bei den ersten (beiden) Frames kommt es zu irgendwelchen Dopplungen! -> manchmal!
     */
    @FXML protected void getLastFrame(ActionEvent event){
        if (this.model.getFramesAmount() == 0) {
            return;
        }


        // TODO: überprüfen, ob ein Label ausgewählt ist und fragen oder aber Inhalt löschen und mit Button disabeln!


        int id = this.model.getCurrentFrameId();
        // DEBUG:
        //System.out.println("Vorher: " + id);
        if (id == 0) {
            // Man kann nicht weiter zurueck gehen, es ist das allererste Frame ausgewaehlt!
            // => ergo ist das "lastFrame" leer!
            return;
        } else if (id == 1) {
            // Es muss das "lastFrame" noch geloescht werden!
            this.uiutil.clearCanvas(this.lastFrame);

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

            this.uiutil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id-1), current);
            this.uiutil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id), next);
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

            this.uiutil.updateCanvas(this.lastFrame, this.model.getFrameByIndex(id-2), last);
            this.uiutil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id-1), current);
            this.uiutil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id), next);
        }

        this.model.setCurrentFrameId(--id);
        // DEBUG:
        //System.out.println("Nachher: " + id);
    }


    /**
     *  Handler getNextFrame (wenn Button "Weiter >" gedrueckt)
     *  => das naechste Frame wird im aktuellen Frame angezeigt und das uebernaechste im naechsten!
     *  => die Labels werden eingezeichnet, ggf nicht gespeicherte (aber veraenderte) Labels abfangen?
     *  TODO: bei den letzten (beiden) Frames kommt es zu irgendwelchen Dopplungen! -> manchmal!
     */
    @FXML protected void getNextFrame(ActionEvent event){
        int amount = this.model.getFramesAmount();
        if (amount == 0) {
            return;
        }


        // TODO: überprüfen, ob ein Label ausgewählt ist und fragen oder aber Inhalt löschen und mit Button disabeln!


        int id = this.model.getCurrentFrameId();
        // DEBUG:
        //System.out.println("Vorher: " + id + ", " + amount);
        if (id == amount-1) {
            // Man kann nicht weiter vorwaerts gehen, es ist das allerletzte Frame ausgewaehlt!
            return;
        } else if (id == amount-2) {
            // Es muss das "nextFrame" noch geloescht werden!
            this.uiutil.clearCanvas(this.nextFrame);

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

            this.uiutil.updateCanvas(this.lastFrame, this.model.getFrameByIndex(id), last);
            this.uiutil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id+1), current);
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

            this.uiutil.updateCanvas(this.lastFrame, this.model.getFrameByIndex(id), last);
            this.uiutil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id+1), current);
            this.uiutil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(id+2), next);
        }

        this.model.setCurrentFrameId(++id);
        // DEBUG:
        //System.out.println("Nachher: " + id);
    }


    /**
     *  EventHandler, der ausgeführt wird, wenn, während der der "Zurück"-Button fokussiert, eine Taste gedrückt wurde
     *
     *  @param event        Das Event, aus dem der KeyCode extrahiert wird!
     */
    @FXML protected void backBtnHandleKeyPressed(KeyEvent event) {
        // Auswerten, welcher Knopf gedrueckt wurde, darauf reagieren!
        KeyCode pressed = event.getCode();
        if (pressed != KeyCode.ENTER) {
            this.handleKeyPressed(pressed);
        }
    }


    /**
     *  EventHandler, der ausgeführt wird, wenn, während der der "Weiter"-Button fokussiert, eine Taste gedrückt wurde
     *
     *  @param event        Das Event, aus dem der KeyCode extrahiert wird!
     */
    @FXML protected void nextBtnHandleKeyPressed(KeyEvent event) {
        // Auswerten, welcher Knopf gedrueckt wurde, darauf reagieren!
        KeyCode pressed = event.getCode();
        if (pressed != KeyCode.ENTER) {
            this.handleKeyPressed(pressed);
        }
    }


    /**
     *  EventHandler, der ausgeführt wird, wenn, während der der "Video laden"-Button fokussiert, eine Taste gedrückt wurde
     *
     *  @param event        Das Event, aus dem der KeyCode extrahiert wird!
     */
    @FXML protected void loadBtnHandleKeyPressed(KeyEvent event) {
        // Auswerten, welcher Knopf gedrueckt wurde, darauf reagieren!
        KeyCode pressed = event.getCode();
        if (pressed != KeyCode.ENTER) {
            this.handleKeyPressed(pressed);
        }
    }


    /**
     *  EventHandler, der ausgeführt wird, wenn, während der der "Label speichern"-Button fokussiert, eine Taste gedrückt wurde
     *
     *  @param event        Das Event, aus dem der KeyCode extrahiert wird!
     */
    @FXML protected void saveBtnHandleKeyPressed(KeyEvent event) {
        // Auswerten, welcher Knopf gedrueckt wurde, darauf reagieren!
        KeyCode pressed = event.getCode();
        if (pressed != KeyCode.ENTER) {
            this.handleKeyPressed(pressed);
        }
    }


    /**
     *  EventHandler, der ausgeführt wird, wenn, während der der "Label Speichern"-Button fokussiert, eine Taste gedrückt wurde
     *
     *  @param event        Das Event, aus dem der KeyCode extrahiert wird!
     */
    @FXML protected void saveLabelBtnHandleKeyPressed(KeyEvent event) {
        // Auswerten, welcher Knopf gedrueckt wurde, darauf reagieren!
        KeyCode pressed = event.getCode();
        if (pressed != KeyCode.ENTER) {
            this.handleKeyPressed(pressed);
        }
    }


    /**
     *  Verarbeitet den KeyCode, da die Abarbeitung für (fast) alle Tasten gleich sein soll!
     *
     *  @param code         Der KeyCode des Events, wird weiterverarbeitet!
     */
    private void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.RIGHT && !this.nextBtn.isDisabled()) {
            if (!this.nextBtn.isFocused()) this.nextBtn.requestFocus();
            this.getNextFrame(null);
        } else if (code == KeyCode.LEFT && !this.backBtn.isDisabled()) {
            if (!this.backBtn.isFocused()) this.backBtn.requestFocus();
            this.getLastFrame(null);
        } /* else if (...) ... */
    }


    /**
     *  Verarbeitet den Klick in das currentFrame-Canvas
     *
     *  @param event        Wird eigentlich nicht verwendet!
     */
    @FXML protected void currentFrameHandleMouseClicked(MouseEvent event) {
        if (this.currentFrame.isDisabled()) return;

        double x = event.getX();
        double y = event.getY();

        int label_id;
        try {
            label_id = this.uiutil.getClickedLabelId(this.currentFrame, x, y, this.model.getFrameDataByFrameNr(this.model.getCurrentFrameId()));

            // Das TextField und den Button freigeben!
            this.txtLabelId.setDisable(false);
            this.saveLabelBtn.setDisable(false);

            this.txtLabelId.setText(label_id+"");
            this.currentSelectedLabelId = label_id;
        } catch (Exception e) {
            // Das TextField und den Button sperren!
            this.txtLabelId.setDisable(true);
            this.saveLabelBtn.setDisable(true);

            this.txtLabelId.setText("");
            this.currentSelectedLabelId = -1;
        }
    }


    /**
     *  EventHandler, der ausgeführt wird, wenn der Button "Label speichern" gedrückt wurde!
     *
     *  @param event
     *
     *  TODO: abfragen, ob nur in diesem Frame das Label geändert werden soll oder in allen!
     *  TODO: wenn in allen geändert werden soll, kann es vorkommen, dass mehrere Label die gleiche Id haben!
     */
    @FXML protected void saveLabel(ActionEvent event) {
        if (this.currentSelectedLabelId == -1) return;

        int n_label_id;
        try {
            n_label_id = Integer.parseInt(this.txtLabelId.getText());
        } catch (Exception e) {
            // Fehlerbehandlung, weil der Inhalt keine Zahl ist!
            System.out.println("Es wurde keine Zahl als neue Label-Id eingegeben!");
            return;
        }


        // Den Dialog aufrufen
        int result = UIUtil.dialogSaveLabel();
        if (result > 0) {
            int currentFrameId = this.model.getCurrentFrameId();

            if (result == 2) {
                for (int i = 0; i < this.model.getFramesAmount(); i++) {
                    if (i == this.model.getCurrentFrameId()) continue;

                    try {
                        this.model.changeLabelIdByFrameNr(i, this.currentSelectedLabelId, n_label_id);
                    } catch (IndexOutOfBoundsException e) {
                        // Ich mach einfach gar nichts?
                    }
                }
            }

            this.model.changeLabelIdByFrameNr(currentFrameId, this.currentSelectedLabelId, n_label_id);
            System.out.println("Changed Label: " + this.currentSelectedLabelId + " to " + n_label_id + " on " + currentFrameId);

            // TODO: Hier wird etwas getrickst, um nicht nochmal darauf zu achten, was neugezeichnet wird und was nicht!
            this.model.setCurrentFrameId(this.model.getCurrentFrameId() - 1);
            this.getNextFrame(null);

            // Alles zurücksetzen!
            this.txtLabelId.setDisable(true);
            this.saveLabelBtn.setDisable(true);

            this.txtLabelId.setText("");
            this.currentSelectedLabelId = -1;
        }
    }
}
