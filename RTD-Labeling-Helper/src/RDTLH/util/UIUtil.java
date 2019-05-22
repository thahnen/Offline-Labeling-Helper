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
 *      Mehtoden (statisch):
 *      - updateCanvas          =>      updatet ein bestimmtes Canvas mit den jeweils angegebenen Daten
 *
 ***********************************************************************************************************************/


public final class UIUtil {
    /**
     *  Updatet das jeweils angegebene Canvas mit einem Bild und den Labeln!
     *
     *  @param view         Das Canvas, welches geupdatet werden soll
     *  @param image        Das anzuzeigende Frame
     *  @param data         Die Frame-Daten zum jeweiligen Frame, daraus werden die Labels extrahiert!
     */
    public static void updateCanvas(Canvas view, Image image, FrameData data) {
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
     *  @param view         Das Canvas, dessen Inhalt geloescht werden soll!
     *
     *  TODO: kann zu einem Einzeiler gemacht werden!
     */
    public static void clearCanvas(Canvas view) {
        final GraphicsContext gc = view.getGraphicsContext2D();
        gc.clearRect(0, 0, view.getWidth(), view.getHeight());
    }
}
