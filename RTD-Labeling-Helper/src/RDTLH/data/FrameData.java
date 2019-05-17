package RDTLH.data;

import java.util.ArrayList;


public class FrameData {
    int frame_nr;
    ArrayList<Label> found_labels;

    public FrameData(int n_nr, ArrayList<Label> n_label) {
        this.frame_nr = n_nr;
        this.found_labels = n_label;
    }
}
