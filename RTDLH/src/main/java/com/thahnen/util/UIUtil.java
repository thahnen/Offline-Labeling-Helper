package com.thahnen.util;


import com.thahnen.data.FrameData;
import com.thahnen.data.Label;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE UIUITIL
 *      =========================
 *
 *      => beinhaltet (übersichtshalber) alle Funktionen zum Ver-/Bearbeiten der UI-Elemente
 *
 *      Funtktionen:
 *      - updateCanvas          =>      updatet ein bestimmtes Canvas mit den jeweils angegebenen Daten (Bild + Frame-Daten)
 *      - clearCanvas           =>      loescht den Inhalt des angegebenen Canvas
 *      - getClickedLabelId     =>      gibt die Label-Id von dem Label zurück, auf das im Canvas geklickt wurde
 *                                      TODO: ggf keine Fehler zurückgeben oder so
 *
 *      TODO: ggf doch alle Funktionen statisch machen (ausser die Klasse muss irgendwelche Infos speichern)
 *
 ***********************************************************************************************************************/


public final class UIUtil {

    public UIUtil() {}


    /**
     *  Updatet das jeweils angegebene Canvas mit einem Bild und den Labeln!
     *
     *  @param view         Das zu ändernde Canvas (muss mit angegeben werden, kann nicht an die Klasse weitergegeben werden!)
     *  @param image        Das anzuzeigende Frame
     *  @param data         Die Frame-Daten zum jeweiligen Frame, daraus werden die Labels extrahiert!
     */
    public void updateCanvas(Canvas view, Image image, FrameData data) {
        final GraphicsContext gc = view.getGraphicsContext2D();

        /** 1) Set the ImageView to the new Image */
        gc.drawImage(image, 0, 0, view.getWidth(), view.getHeight());

        /** 2) Test if data is null (may happen if there is nothing found in the frame) */
        if (data == null) return;

        /** => and test if the labels are null! shall not happen! */
        ArrayList<Label> labels = data.getLabels();
        if (labels == null) return;

        double scaleX = view.getWidth()/image.getWidth();
        double scaleY = view.getHeight()/image.getHeight();

        /** 3) For each label do */
        for (Label l : labels) {
            double x1 = (l.getP1().getX())*scaleX;
            double y1 = (l.getP1().getY())*scaleY;
            double w = (l.getP3().getX())*scaleX-x1;
            double h = (l.getP3().getY())*scaleY-y1;

            /** 4) Create the Bounding Box */
            gc.strokeRect(x1, y1, w, h);

            int label_id = l.getLabelId();
            /** 5) Create a Text with the Label-Id in it! */
            // TODO: muss noch kommen!
            gc.fillText(l.getLabelId()+"", x1, y1);
        }
    }


    /**
     *  Loescht den Inhalt des angegebenen Canvas
     *
     *  @param view         Das Canvas, dessen Inhalt geloescht werden soll (muss mit angegeben werden, kann nicht an die Klasse weitergegeben werden!)
     *
     *  TODO: kann zu einem Einzeiler gemacht werden!
     */
    public void clearCanvas(Canvas view) {
        final GraphicsContext gc = view.getGraphicsContext2D();
        gc.clearRect(0, 0, view.getWidth(), view.getHeight());
    }


    /**
     *  Gibt die Label-Id des Labels zurück, in das geklickt wurde. Falls in gar keins geklickt wurde gibt es eine Exception!
     *
     *  @param view         Das Canvas, welches aus dem das Label extrahiert werden soll (muss mit angegeben werden, kann nicht an die Klasse weitergegeben werden!)
     *  @param x            Die X-Koordinate des Klick-Events
     *  @param y            Die Y-Koordinate des Klick-Events
     *  @param data         Die Frame-Daten zum jeweiligen Frame, um daraus alle vorhandenen Labels abzufragen!
     *  @throws NoSuchFieldException
     */
    public int getClickedLabelId(Canvas view, double x, double y, FrameData data) throws NoSuchFieldException {
        double scaleX = view.getWidth()/768;
        double scaleY = view.getHeight()/640;

        // TODO: gibt allerdings nur das allererste Label aus, auf das geklickt wurde! es können sich ja auch zwei ueberlappen!
        for (Label l : data.getLabels()) {
            if (x >= (l.getP1().getX())*scaleX && x <= (l.getP3().getX())*scaleX        // x in [P1.x, P3.x]
                    && y >= (l.getP1().getY())*scaleY && y <= (l.getP3().getY())*scaleY) {  // y in [P1.y, P3.y]
                return l.getLabelId();
            }
        }
        throw new NoSuchFieldException("No Label was clicked!");
    }


    /*******************************************************************************************************************
     *
     *      DIALOGE ALLER ART (ungefaehr nach dem Sequenzdiagramm sortiert)
     *
     *******************************************************************************************************************/


    /**
     *  Zeigt den Dialog an, in dem man auswaehlen kann, in welchem Format das Video vorliegt (Video-Datei oder Frames)
     *
     *  @return         eine, der jeweiligen Auswahlmoeglichkeit zugeordneten, Zahl, die dann ueberprueft werden muss!
     *
     *  TODO: was auch immer es mit diesem Raw-Type auf sich hat!
     */
    public static short dialogChooseVideoFormat() {
        final ArrayList<String> options = new ArrayList<>(Arrays.asList(
                "AVI-Video",
                "PNG-Frames"
        ));

        ChoiceDialog dialog = new ChoiceDialog(options.get(0), options);
        dialog.setTitle("Video laden");
        dialog.setHeaderText("Video-Format waehlen!");

        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            return (short) options.indexOf(res);
        }
        return -1;
    }


    /**
     *  Zeigt den Dialog an, in dem man auswaehlen kann, in welchem Format die Label vorliegen (JSON, CSV, ...)
     *
     *  @return         eine, der jeweiligen Auswahlmoeglichkeit zugeordneten, Zahl, die dann ueberprueft werden muss!
     *
     *  TODO: was auch immer es mit diesem Raw-Type auf sich hat!
     */
    public static short dialogChooseLabelFormat() {
        final ArrayList<String> options = new ArrayList<>(Arrays.asList(
                "JSON",
                "CSV (Darkflow)"
        ));

        ChoiceDialog dialog = new ChoiceDialog(options.get(0), options);
        dialog.setTitle("Label laden");
        dialog.setHeaderText("Label-Format waehlen!");

        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            return (short) options.indexOf(res);
        }
        return -1;
    }


    /**
     *  Zeigt den Dialog zum speichern eines ausgewaehlten Labels an (ob alle oder nur eins gespeichert werden soll)
     *
     *  @return             eine, dem jeweiligen Button zugeordnete, Zahl, die dann nur überprueft werden muss.
     *
     *  TODO: wir wollen ja eigentlich keinen Speicherplatz verschwenden, deshalb später Rückgabewert in "short" ändern!
     */
    public static int dialogSaveLabel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dieses Frame nur oder alle?");
        alert.setHeaderText("Soll das Label nur in diesem Frame oder in allen geändert werden?");

        ButtonType eins = new ButtonType("Nur hier");
        ButtonType alle = new ButtonType("Überall");
        ButtonType cancel = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(eins, alle, cancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == eins) {
                return 1;
            } else if (result.get() == alle) {
                return 2;
            }
        }

        return -1;
    }
}
