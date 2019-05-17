package RDTLH.data;

import java.util.ArrayList;


public class FrameData {
    int frame_nr;
    ArrayList<Label> found_labels;

    public FrameData(int n_nr, ArrayList<Label> n_label) {
        this.frame_nr = n_nr;
        this.found_labels = n_label;
    }

    public int getFrameNr() {
        return frame_nr;
    }

    public ArrayList<Label> getLabels() {
        return found_labels;
    }
}
