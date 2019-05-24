package com.thahnen.RDTLH;

import com.thahnen.RDTLH.data.FrameData;
import com.thahnen.RDTLH.data.Label;
import javafx.scene.image.Image;
import java.util.ArrayList;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE Model
 *      =======================
 *
 *      => das Datenmodell der Applikation, handhabt die Ver-/Bearbeitung der einzelnen Daten
 *
 *      - int currentFrameId        die Id des Frames, das gegenwärtig im currentFrame-Canvas angezeigt wird
 *      - Image[] frames            alle eingelesenen Frames des Videos
 *                                  TODO: irgendwie Lücken vorbeugen, sonst sind die Frames + FrameData ggf. nicht mehr synchron!
 *      - FrameData[] frame_info    alle Frame-Daten (Labels etc.), es muss nicht zu jedem Frame Daten vorhanden sein!
 *                                  TODO: ggf irgendwie die Frames und die -Daten zusammenpacken?
 *
 *      Funktionen:
 *      - changeLabelIdByFrameNr    =>      wird aufgerufen, wenn ein Label auf einem speziellen Frame verändert werden soll!
 *
 ***********************************************************************************************************************/


public class Model {
    private int currentFrameId;
    private ArrayList<Image> frames;
    private ArrayList<FrameData> frame_info;    // ich weiss nicht, ob die frame_nr bei 0 oder 1 anfaengt -.-


    public Model() {
        this.currentFrameId = 0;
        this.frames = null;
        this.frame_info = null;
    }


    /**
     *  (Standard-)Funktionen rund um die aktuelle Frame-Id
     */
    public int getCurrentFrameId() {
        return currentFrameId;
    }
    public void setCurrentFrameId(int new_id) {
        this.currentFrameId = new_id;
    }


    /**
     *  (Standard-)Funktionen rund um die Liste der Frames
     */
    public ArrayList<Image> getFrames() {
        assert frames != null;
        return frames;
    }

    public Image getFrameByIndex(int index) throws IndexOutOfBoundsException {
        assert frames != null;
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        throw new IndexOutOfBoundsException("The id requesting a frame is outside of [0, frames.size()] !");
    }

    public int getFramesAmount() {
        return frames != null ? frames.size() : 0;
    }

    public void setFrames(ArrayList<Image> new_frames) {
        this.frames = new_frames;
    }


    /**
     *  Funktionen rund um die Liste der Frame-Daten
     */
    public FrameData getFrameDataByFrameNr(int frame_nr) throws IndexOutOfBoundsException {
        // TODO: die mitgegebene "frame_nr" sollte um eins erhoeht werden, da Array der Frames bei 0 anfaengt und die Frame-Nr bei 1?
        for (FrameData data : frame_info) {
            if (data.getFrameNr() == frame_nr) {
                return data;
            }
        }
        throw new IndexOutOfBoundsException("The frame_nr requested was not found in the dataset!");
    }

    public ArrayList<FrameData> getFrameInfo(){
        assert frame_info != null;
        return frame_info;
    }

    public void setFrameInfo(ArrayList<FrameData> new_frame_info) {
        // TODO: jedes mal die Informationen sortieren von niedrigster Frame Id zur hoechsten!
        this.frame_info = new_frame_info;
    }

    // TODO: da hier alle Label mit genau der Id verändert werden, kann es dazu kommen, dass ungewollt eine ander Id geändert wird!
    // TODO: vlt die Funktion so umschreiben, dass sie die X/Y-Werte, wo geklickt wurde, mit verarbeitet?
    public void changeLabelIdByFrameNr(int frame_nr, int old_id, int new_id) throws IndexOutOfBoundsException {
        for (FrameData info : this.frame_info) {
            if (info.getFrameNr() == frame_nr) {
                for (Label l : info.getLabels()) {
                    if (l.getLabelId() == old_id) {
                        l.setLabelId(new_id);
                    }
                }
                return;
            }
        }
        throw new IndexOutOfBoundsException("The Label in the FrameData with the given frame_nr cannot be changed!");
    }
}