package RDTLH.util;


import RDTLH.data.FrameData;
import RDTLH.data.Label;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE UIUITIL
 *      =========================
 *
 *      - Alle FXML-Elemente, die bearbeitet werden können
 *
 *      Mehtoden (statisch):
 *      - updateCanvas          =>      updatet ein bestimmtes Canvas mit den jeweils angegebenen Daten
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
}
