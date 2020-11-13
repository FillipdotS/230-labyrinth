package source.labyrinth;

public class TShape extends FloorTile {
	public TShape(int orientation) {
		super(orientation, new Boolean[]{false, true, true, true},"../resources/img/tile_tshape.png");
	}
}
