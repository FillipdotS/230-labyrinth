package source.labyrinth;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import source.labyrinth.controllers.LevelController;

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
	private int isFrozenUntil;
	private Player player;

	public FloorTile(int orientation, TileType tileType) {
		this.orientation = orientation;
		this.tileType = tileType;
		this.isFrozenUntil=-1;
		this.isOnFireUntil=-1;

		// Shift the array to the right depending on orientation
		Boolean[] tmpMask = this.tileType.defaultMoveMask.clone();
		for (int i = 0; i < orientation; i++) {
			Boolean tmp = tmpMask[3];
			System.arraycopy(tmpMask, 0, tmpMask, 1, 3);
			tmpMask[0] = tmp;
		}
		this.moveMask = tmpMask;
	}

	/**
	 * freezes tile for a loop
	 */
	public void freeze() {
		isFrozenUntil = LevelController.getCurrentTime() + LevelController.getTimeForFullLoop();
	}

	/**
	 * sets tile on fire for 2 loops
	 */
	public void setOnFire() {
		isOnFireUntil = LevelController.getCurrentTime() + 2 * LevelController.getTimeForFullLoop();
	}

	/**
	 * @return Player standing on this FloorTile
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * This method exists only for the Player to set it to null when they moves. Should NOT be used otherwise.
	 * @param player Player that now stands on this FloorTile
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public Boolean[] getMoveMask() {
		return (LevelController.getCurrentTime() >= isOnFireUntil) ? this.moveMask : new Boolean[] {false,false,false,false};
	}

	public String getImageURL() {
		return this.tileType.imageURL;
	}

	public Boolean getFixed() {
		return this.isFixed;
	}

	/**
	 * is tile fixer now
	 * @return Boolean show if tile is currently fixed
	 */
	public Boolean isCurrentlyFixed() {
		return isFixed || LevelController.getCurrentTime() < isFrozenUntil;
	}

	public int getIsFrozenUntil() {
		return isFrozenUntil;
	}

	public int getIsOnFireUntil() {
		return isOnFireUntil;
	}

	public void setIsOnFireUntil(int isOnFireUntil) {
		this.isOnFireUntil = isOnFireUntil;
	}

	public void setIsFrozenUntil(int isFrozenUntil) {
		this.isFrozenUntil = isFrozenUntil;
	}

	public void setFixed(Boolean fixed) {
		this.isFixed = fixed;
	}

	/**
	 * @param renderSize Size that the StackPane should return as.
	 * @return StackPane representing the FloorTile.
	 */
	public StackPane renderTile(int renderSize) {
		Image img = new Image(String.valueOf(getClass().getResource(this.getImageURL())), renderSize, renderSize, false, false);

		ImageView iv = new ImageView(img);
		iv.setRotate(90 * this.getOrientation());

		StackPane stack = new StackPane(iv);

		if (isFixed) {
			Image fixedImage = new Image(String.valueOf(getClass().getResource("../resources/img/fixed_tile.png")), renderSize, renderSize, false, false);
			ImageView fixedImageView = new ImageView(fixedImage);
			stack.getChildren().addAll(fixedImageView);
		}

		if (isFrozenUntil > LevelController.getCurrentTime()) {
			Image fixedImage = new Image(String.valueOf(getClass().getResource("../resources/img/frozen_tile.png")), renderSize, renderSize, false, false);
			ImageView fixedImageView = new ImageView(fixedImage);
			fixedImageView.setOpacity(0.5);
			stack.getChildren().addAll(fixedImageView);
		}

		if (isOnFireUntil > LevelController.getCurrentTime()) {
			Image fixedImage = new Image(String.valueOf(getClass().getResource("../resources/img/fire_tile.png")), renderSize, renderSize, false, false);
			ImageView fixedImageView = new ImageView(fixedImage);
			fixedImageView.setOpacity(0.5);
			stack.getChildren().addAll(fixedImageView);
		}

		return stack;
	}

	/**
	 * Get a string that represents this tile and it's state completely.
	 * Example: "0,STRAIGHT,true,11,15"
	 * In order: orientation, tile type, is permanently fixed, is on fire until, is frozen until
	 * @return Single line string representing this FloorTile.
	 */
	public String exportSelf() {
		String self = Integer.toString(this.orientation);
		self += "," + this.tileType.name();
		self += "," + this.isFixed;
		self += "," + this.isOnFireUntil;
		self += "," + this.isFrozenUntil;
		return self;
	}
}
