package RDTLH;

import javafx.scene.image.Image;
import java.util.ArrayList;


public class Model {
    private int currentFrameId;
    private ArrayList<Image> frames;


    public Model() {
        this.currentFrameId = 0;
        this.frames = null;
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
    public ArrayList<Image> getFrames() throws Exception {
        if (frames != null) {
            return frames;
        }
        throw new NotFoundException("List of frames is null!");
    }

    public Image getFrameByIndex(int index) throws Exception {
        assert frames != null;
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        throw new NotFoundException("Requested index is out of bounds!");
    }

    public int getFramesAmount() {
        return frames != null ? frames.size() : 0;
    }

    public void setFrames(ArrayList<Image> new_frames) {
        this.frames = new_frames;
    }
}


final class NotFoundException extends Exception {
    public NotFoundException() {
        super();
    }
    public NotFoundException(String msg) {
        super(msg);
    }
}
