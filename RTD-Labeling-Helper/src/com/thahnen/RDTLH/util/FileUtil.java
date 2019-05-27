package com.thahnen.RDTLH.util;

import com.thahnen.RDTLH.data.FrameData;
import com.thahnen.RDTLH.data.Label;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE FILEUTIL
 *      ==========================
 *
 *      Methoden (statisch!):
 *      - loadLabelsFromJSON    =>      laed die Label in Frame-Info-Objekte fuer das Model
 *      - loadFranesFromVideo   =>      laed das Video in einzelnen Frames fuer das Model
 *      - saveLabelsToJSON      =>      speichert die Label im JSON-Format zurück
 *
 *      TODO: ggf noch zusätzlich Daten direkt aus Darkflow-CSV einlesen/ speichern
 *
 ***********************************************************************************************************************/


public final class FileUtil {
    /**
     *  Liest die Label aus einer JSON-Datei ein nach dem Schema, was auch fuer Labelbox verwendet wurde!
     *
     *  @param file         JSON-Datei mit den Label
     *  @return             Liste mit allen Frame-Infos
     *  @throws Exception   Mehrere unterschiedliche Fehler
     *
     *  TODO: ggf die "external_id" mitabspeichern als String?
     */
    public static ArrayList<FrameData> loadLabelsFromJSON(File file) throws Exception {
        ArrayList<FrameData> frame_info = new ArrayList<>();

        /** 1) JSON einlesen, sollte wie folgt aussehen (ist ein Array):
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
        JSONArray array = (JSONArray) new JSONParser().parse(new FileReader(file.getAbsolutePath()));

        /** 2) Fuer jedes Array-Element: */
        for (Object o1 : array) {
            JSONObject elem = (JSONObject) o1;

            /** 3) Frame-Nr extrahieren */
            int frame_nr = (int) ((long) elem.get("frame_nr"));

            ArrayList<Label> labels = new ArrayList<>();
            /** 4) Fuer jedes gefundene Label */
            for (Object o2 : (JSONArray) ((JSONObject) elem.get("prediction_label")).get("object")) {
                JSONObject label = (JSONObject) o2;

                /** 5) Label-Id extrahieren */
                int label_id = (int) ((long) label.get("label_id"));

                JSONArray geometry = (JSONArray) label.get("geometry");
                /** 6) Punkte P1-P4 extrahieren */
                JSONObject point = (JSONObject) geometry.get(0);
                Point2D p1 = new Point2D(
                        (int) ((long) point.get("x")),
                        (int) ((long) point.get("y"))
                );
                point = (JSONObject) geometry.get(1);
                Point2D p2 = new Point2D(
                        (int) ((long) point.get("x")),
                        (int) ((long) point.get("y"))
                );
                point = (JSONObject) geometry.get(2);
                Point2D p3 = new Point2D(
                        (int) ((long) point.get("x")),
                        (int) ((long) point.get("y"))
                );
                point = (JSONObject) geometry.get(3);
                Point2D p4 = new Point2D(
                        (int) ((long) point.get("x")),
                        (int) ((long) point.get("y"))
                );

                /** 7) An Label-Liste anghaengen */
                labels.add(new Label(
                        label_id, p1, p2, p3, p4
                ));
            }

            /** 8) An Frame-Info-Liste anhaengen */
            frame_info.add(new FrameData(
                    frame_nr, labels
            ));
        }

        return frame_info;
    }


    /**
     *  Liste die Frames aus einem Video ein (ohne auf Eigenschaften zu pruefen)
     *
     *  @param file         Video-Datei
     *  @return             Liste mit allen Frames als JavaFX-Image
     *  @throws Exception   Mehrere unterschiedliche Fehler
     */
    public static ArrayList<Image> loadFramesFromVideo(File file) throws Exception {
        ArrayList<Image> frames = new ArrayList<>();

        /** 1) Video einlesen */
        VideoCapture cap = new VideoCapture(file.getAbsolutePath());

        /** 2) Fuer jeden einzelnen Frame */
        while (true) {
            Mat frame = new Mat();

            /** 3) Test kein Frame mehr da? */
            if (!cap.read(frame)) break;

            /** 4) Konvertierung von Farbe in Graustufen, da Video */
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

            /** 5) Konvertierung in ein JavaFX-Bild fuer ImageView */
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".png", frame, buffer);

            /** 6) An Frame-Liste anhaengen */
            frames.add(new Image(new ByteArrayInputStream(buffer.toArray())));
        }

        return frames;
    }


    /**
     *  Speichert die Label in einer JSON-Datei ab in demselben Format, wie es eingelesen wurde!
     *
     *  @param file         JSON-Datei für die Label
     *  @param frameData    Liste mit allen Frame-Infos
     *  @throws Exception   Mehrere unterschiedliche Fehler
     */
    public static void saveLabelsToJSON(File file, ArrayList<FrameData> frameData) throws Exception {
        /** JSON abspeichern, sollte wie folgt aussehen (ist ein Array):
         *
         *  {
         *      "frame_nr" : Int,
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

        // 1) Jedes FrameData-Objekt als ein JSON-Objekt verpacken!
        JSONArray array = new JSONArray();
        for (FrameData data : frameData) {
            JSONObject save_elem = new JSONObject();
            save_elem.put("frame_nr", data.getFrameNr());

            // 2) Jedes Label-Objekt als ein JSON-Objekt verpacken!
            JSONArray object = new JSONArray();
            for (Label label : data.getLabels()) {
                JSONObject l = new JSONObject();
                l.put("label_id", label.getLabelId());

                JSONArray points = new JSONArray();
                JSONObject p1 = new JSONObject();
                p1.put("x", label.getP1().getX());
                p1.put("y", label.getP1().getY());
                points.add(p1);

                JSONObject p2 = new JSONObject();
                p2.put("x", label.getP2().getX());
                p2.put("y", label.getP2().getY());
                points.add(p2);

                JSONObject p3 = new JSONObject();
                p3.put("x", label.getP3().getX());
                p3.put("y", label.getP3().getY());
                points.add(p3);

                JSONObject p4 = new JSONObject();
                p4.put("x", label.getP4().getX());
                p4.put("y", label.getP4().getY());
                points.add(p4);

                l.put("geometry", points);

                object.add(l);
            }

            JSONObject prediction_label = new JSONObject();
            prediction_label.put("object", object);
            save_elem.put("prediction_label", prediction_label);

            array.add(save_elem);
        }

        // 3) Label-Array abspeichern
        FileWriter writer = new FileWriter(file);
        writer.write(array.toJSONString());
    }
}
