package source.labyrinth;

public class Corner extends FloorTile {
    public Corner(int orientation) {
        super(orientation, new Boolean[]{true, true, false, false}, "../resources/img/tile_corner.png");


    }
}
