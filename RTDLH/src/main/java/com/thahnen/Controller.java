package com.thahnen;

import com.thahnen.data.FrameData;
import com.thahnen.util.FileUtil;
import com.thahnen.util.SysUTIL;
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

    private int currentSelectedLabelId;


    public Controller() {
        this.model = new Model();
        this.currentSelectedLabelId = -1;
    }


    /**
     *  Handler loadVideo (wenn Button "Video laden" gedrueckt)
     *  => liest ein Video ein: es wird abgefragt, in welchem Format das Video vorliegt, dann wird es eingelesen!
     *  => liest die Labels ein: es wird abgefragt, in welchem Format die Label vorliegen, dann wird eingelesen!
     */
    @FXML protected void loadVideo(ActionEvent event) {
        /** 1) nachfragen, in welchem Format das Video vorliegt */
        boolean singleFile = true;
        switch (UIUtil.dialogChooseVideoFormat()) {
            case 0:
                // AVI-Video, ergo FileChooser
                break;
            case 1:
                // PNG-Frames, ergo DirectoryChooser (oder auch FileChooser?)
                singleFile = false;
                break;
            case -1:
                // Abbruch
                // DEBUG:
                System.out.println("Video laden wurde beim 'Video Format wählen' abgebrochen!");
                return;
        }

        /** 2) Video (in angegebenem Format) laden */
        if (singleFile) {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(SysUTIL.getHomeDir()));
            chooser.setTitle("Video-Datei auswählen!");

            File video = chooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (video == null) {
                // Fehlerbehandlung weil nichts ausgewählt!
                return;
            }

            // TODO: nur Videos unter 50mb => nachher anders loesen
            System.out.println("Videogroesse: " + (video.length() / (1024*1024)) + "mb");
            if ((video.length() / (1024*1024)) > 50) {
                // Fehlerbehandlung weil Video zu gross
                System.out.println("Das angegebene Video ist zu gross!");
                return;
            }

            // TODO: einzelne Frames extrahieren und abspeichern => nachher anders loesen
            try {
                ArrayList<Image> frames = FileUtil.loadFramesFromVideo(video);
                this.model.setFrames(frames);
            } catch (Exception e) {
                // TODO: das hier muss später noch etwas genauer gemacht werden mit unterschiedlichen Exceptions!
                System.out.println(e);
                return;
            }
            System.out.println("Frames set: " + this.model.getFramesAmount());
        } else {
            // Noch nicht implementiert
            // TODO: kleines Dialogfenster machen, dafür, dass Feature noch nicht implementiert ist!
            System.out.println("Laden per Frames noch nicht implementiert!");
            return;
        }

        /** 3) nachfragen, in welchem Format die Label vorliegen */
        byte format = UIUtil.dialogChooseLabelFormat();
        if (format == -1) {
            // Abbruch
            System.out.println("Video laden wurde beim 'Label Format wählen' abgebrochen!");
            return;
        }

        /** 4) Label (in angegebenem Format) laden */
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(SysUTIL.getHomeDir()));
        chooser.setTitle("Label-Datei auswählen!");

        File label = chooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (label == null) {
            // Fehlerbehandlung weil nichts ausgewählt!
            return;
        }

        switch (format) {
            case 0:
                // JSON
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

                break;
            case 1:
                // Darkflow-CSV
                // Noch nicht implementiert
                // TODO: kleines Dialogfenster machen, dafür, dass Feature noch nicht implementiert ist!
                System.out.println("Darkflow-CSV noch nicht implementiert!");
                return;
        }

        /** 5) Initiale Werte setzen */
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

        UIUtil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(0), current);
        UIUtil.updateCanvas(this.nextFrame, this.model.getFrameByIndex(1), next);

        /** 6) Alle UI-Elemente entsprechend aktivieren/ deaktivieren */
        this.saveBtn.setDisable(false);
        this.loadBtn.setDisable(true);          // bis anständig geregelt ist, was passiert, wenn man neuläd!
        this.currentFrame.setDisable(false);
        this.backBtn.setDisable(false);
        this.nextBtn.setDisable(false);
    }


    /**
     *
     *  @param event
     */
    @FXML protected void saveLabels(ActionEvent event) {
        // TODO: ggf noch eine Extension vorgeben, je nachdem in welchem Format geladen!
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(SysUTIL.getHomeDir()));
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

            UIUtil.updateCanvas(this.lastFrame, this.model.getFrameByIndex(id), last);
            UIUtil.updateCanvas(this.currentFrame, this.model.getFrameByIndex(id+1), current);
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
            label_id = UIUtil.getClickedLabelId(this.currentFrame, x, y, this.model.getFrameDataByFrameNr(this.model.getCurrentFrameId()));

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
        byte result = UIUtil.dialogSaveLabel();
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
