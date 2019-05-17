package RDTLH;

import RDTLH.data.FrameData;
import javafx.scene.image.Image;
import java.util.ArrayList;


public class Model {
    private int currentFrameId;
    private ArrayList<Image> frames;
    private ArrayList<FrameData> frame_info;


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
    public ArrayList<FrameData> getFrameInfo() {
        assert frame_info != null;
        return frame_info;
    }

    public void setFrameInfo(ArrayList<FrameData> new_frame_info) {
        this.frame_info = new_frame_info;
    }
}