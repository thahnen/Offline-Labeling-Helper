package RDTLH.data;

import javafx.geometry.Point2D;


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
}
