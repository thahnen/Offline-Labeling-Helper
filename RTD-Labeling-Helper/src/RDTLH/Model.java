package RDTLH;

import javafx.scene.image.Image;

import java.util.ArrayList;


public class Model {
    private ArrayList<Image> frames;


    public Model() {
        this.frames = null;
    }


    /**
     *  Funktionen rund um die Liste der Frames
     */
    public ArrayList<Image> getFrames() { return this.frames; }
    public Image getFrameByIndex(int index) throws NotFoundException {
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        throw new NotFoundException();
    }
    public void setFrames(ArrayList<Image> new_frames) { this.frames = new_frames; }
}


final class NotFoundException extends Exception {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
