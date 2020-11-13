package source.labyrinth;

public class Straight extends FloorTile {
	public Straight(int orientation) {
		super(orientation, new Boolean[]{true, false, true, false}, "../resources/img/tile_straight.png");
	}
}
