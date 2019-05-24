package com.thahnen.RDTLH.data;

import javafx.geometry.Point2D;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE Label
 *      =======================
 *
 *      => dient nur als Datenspeicher und bietet (soweit) keine komplexen Funktionen an (quasi wie ein struct in C)
 *
 *      - int label_id              die eingelesene, vergebene Label-Id, kann verändert werden
 *      - Point p1, p2, p3, p4      die Eckepunkte der Bounding-Box
 *                                  TODO: sollen auch irgendwann veränderbar sein!
 *
 ***********************************************************************************************************************/


public class Label {
    private int label_id;
    private Point2D p1, p2, p3, p4;

    public Label(int n_id, Point2D n_p1, Point2D n_p2, Point2D n_p3, Point2D n_p4) {
        this.label_id = n_id;
        this.p1 = n_p1;
        this.p2 = n_p2;
        this.p3 = n_p3;
        this.p4 = n_p4;
    }

    public int getLabelId() {
        return label_id;
    }
    public void setLabelId(int n_label_id) {
        this.label_id = n_label_id;
    }

    public Point2D getP1() {
        return p1;
    }

    public Point2D getP2() {
        return p2;
    }

    public Point2D getP3() {
        return p3;
    }

    public Point2D getP4() {
        return p4;
    }
}
