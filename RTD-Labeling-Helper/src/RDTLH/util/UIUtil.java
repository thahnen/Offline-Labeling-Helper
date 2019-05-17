package RDTLH.util;


import RDTLH.data.Label;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASS UIUITIL
 *      ========================
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
     *  @param labels       Eine Liste aller fuer genau das Frame gefundene Label => null, wenn keine gefunden oder beabsichtigt
     */
    public static void updateCanvas(Canvas view, Image image, ArrayList<Label> labels) {
        final GraphicsContext gc = view.getGraphicsContext2D();

        /** 1) Set the ImageView to the new Image */
        gc.drawImage(image, 0, 0, view.getWidth(), view.getHeight());

        /** 2) Test if data is null (may happen if there is nothing found in the frame) */
        if (labels == null) return;

        /** 3) For each label do */
        for (Label l : labels) {
            double x1 = l.getP1().getX();
            double y1 = l.getP1().getY();
            double w = l.getP3().getX()-x1;
            double h = l.getP3().getY()-y1;

            /** 4) Create the Bounding Box */
            gc.strokeRect(x1, y1, w, h);

            int label_id = l.getLabelId();
            /** 5) Create a Text with the Label-Id in it! */
            // TODO: muss noch kommen!
        }
    }
}
