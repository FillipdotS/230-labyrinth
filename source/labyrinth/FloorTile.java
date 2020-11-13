package source.labyrinth;

import javafx.scene.image.Image;

public abstract class FloorTile extends Tile {
    protected int orientation;
    protected Boolean[] moveMask;
    protected String imageURL;

    public FloorTile(int orientation, Boolean[] moveMask, String imageURL) {
        this.orientation = orientation;
        this.imageURL = imageURL;

        // Each mask is given for the
        for (int i = 0; i < orientation; i++) {
            Boolean tmp = moveMask[3];
            for (int j = 3; j > 0; j--) {
                moveMask[j] = moveMask[j - 1];
            }
            moveMask[0] = tmp;
        }
        this.moveMask = moveMask;
    }

    public Boolean[] getMoveMask() {
        return this.moveMask;
    }
}
