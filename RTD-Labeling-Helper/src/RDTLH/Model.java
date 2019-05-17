package RDTLH;

import RDTLH.data.FrameData;
import javafx.scene.image.Image;
import java.util.ArrayList;


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
     *  Funktionen rund um die aktuelle Frame-Id
     */
    public int getCurrentFrameId() {
        return currentFrameId;
    }
    public void setCurrentFrameId(int new_id) {
        this.currentFrameId = new_id;
    }


    /**
     *  Funktionen rund um die Liste der Frames
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

    public ArrayList<FrameData> getFrameInfo() {
        assert frame_info != null;
        return frame_info;
    }

    public void setFrameInfo(ArrayList<FrameData> new_frame_info) {
        // TODO: jedes mal die Informationen sortieren von niedrigster Frame Id zur hoechsten!
        this.frame_info = new_frame_info;
    }
}