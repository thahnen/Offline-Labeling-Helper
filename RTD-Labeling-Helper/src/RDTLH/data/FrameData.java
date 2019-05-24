package RDTLH.data;

import java.util.ArrayList;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE FrameData
 *      ===========================
 *
 *      => dient nur als Datenspeicher und bietet (soweit) keine komplexen Funktionen an (quasi wie ein struct in C)
 *
 *      - int frame_nr              die eingelesene, Frame-Nr, die angibt, zu welchem Frame (des Videos) die Label gehören
 *      - Label[] found_labels      die gefundenen Labels in diesem Frame, können nur einzeln verändert werden!
 *                                  TODO: irgendwann soll man neue hinzufügen, alte verändern oder löschen können!
 *
 ***********************************************************************************************************************/


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
