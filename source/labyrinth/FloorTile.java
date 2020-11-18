package source.labyrinth;

public class FloorTile extends Tile {
	/**
	 * The different types of floor tile that an instance of FloorTile can be.
	 */
	public enum TileType {
		STRAIGHT(new Boolean[]{true, false, true, false}, "../resources/img/tile_straight.png"),
		CORNER(new Boolean[]{true, true, false, false}, "../resources/img/tile_corner.png"),
		TSHAPE(new Boolean[]{false, true, true, true},"../resources/img/tile_tshape.png"),
		GOAL(new Boolean[]{true, true, true, true},"../resources/img/tile_goal.png");

		// Each tile type has their default move mask, and a string to their image.
		private final Boolean[] defaultMoveMask;
		private final String imageURL;

		TileType(Boolean[] defaultMoveMask, String imageURL) {
			this.defaultMoveMask = defaultMoveMask;
			this.imageURL = imageURL;
		}
	}

	private final int orientation;
	private final Boolean[] moveMask; // Specifically THIS tiles move mask, which has been changed by orientation
	private final TileType tileType;


	private Boolean isFixed = false;
	private int isOnFireUntil;
	private int isFrozeneUntil;

	public FloorTile(int orientation, TileType tileType) {
		this.orientation = orientation;
		this.tileType = tileType;
		this.isFrozeneUntil=-1;
		this.isOnFireUntil=-1;

		// Shift the array to the right depending on orientation
		Boolean[] tmpMask = this.tileType.defaultMoveMask.clone();
		for (int i = 0; i < orientation; i++) {
			Boolean tmp = tmpMask[3];
			for (int j = 3; j > 0; j--) {
				tmpMask[j] = tmpMask[j - 1];
			}
			tmpMask[0] = tmp;
		}
		this.moveMask = tmpMask;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public Boolean[] getMoveMask() {
		return this.moveMask;
	}

	public String getImageURL() {
		return this.tileType.imageURL;
	}

	public Boolean getFixed() {
		return this.isFixed;
	}

	public int getIsFrozeneUntil() {
		return isFrozeneUntil;
	}

	public int getIsOnFireUntil() {
		return isOnFireUntil;
	}

	public void setIsOnFireUntil(int isOnFireUntil) {
		this.isOnFireUntil = isOnFireUntil;
	}

	public void setIsFrozeneUntil(int isFrozeneUntil) {
		this.isFrozeneUntil = isFrozeneUntil;
	}

	public void setFixed(Boolean fixed) {
		this.isFixed = fixed;
	}
}
