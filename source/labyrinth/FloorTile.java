package source.labyrinth;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import source.labyrinth.controllers.LevelController;

import java.io.Serializable;

public class FloorTile extends Tile implements Serializable {
	/**
	 * The different types of floor tile that an instance of FloorTile can be.
	 */
	public enum TileType {
		STRAIGHT(new Boolean[]{true, false, true, false}, "source/resources/img/tile_straight.png"),
		CORNER(new Boolean[]{true, true, false, false}, "source/resources/img/tile_corner.png"),
		TSHAPE(new Boolean[]{false, true, true, true},"source/resources/img/tile_tshape.png"),
		GOAL(new Boolean[]{true, true, true, true}, "source/resources/img/tile_goal.png");

		public final String imageURL;

		// Each tile type has their default move mask, and a string to their image.
		private final Boolean[] defaultMoveMask;

		TileType(Boolean[] defaultMoveMask, String imageURL) {
			this.defaultMoveMask = defaultMoveMask;
			this.imageURL = imageURL;
		}
	}

	private final double playerToTileScaling = 0.6f;
	private final TileType tileType;

	private Boolean[] moveMask; // Specifically THIS tiles move mask, which has been changed by orientation
	private int orientation;
	private Boolean isFixed = false;
	private int isOnFireUntil;
	private int isFrozenUntil;
	private Player player;

	public FloorTile(int orientation, TileType tileType) {
		this.orientation = orientation;
		this.tileType = tileType;
		this.isFrozenUntil=-1;
		this.isOnFireUntil=-1;

		this.moveMask = calculateMoveMask();
	}

	/**
	 * Rotate this tile, updating it's move mask as well.
	 * @param rotation Either 1 or (-1)
	 */
	public void rotateBy(int rotation) {
		if (this.orientation + rotation > 3) {
			// Rotating from 3 upwards returns us to 0
			this.orientation = 0;
		} else if (this.orientation + rotation < 0) {
			// Rotating from 0 downwards returns us to 3
			this.orientation = 3;
		} else {
			this.orientation += rotation;
		}
		this.moveMask = calculateMoveMask();
	}

	public Boolean isItGoal() {
		return (this.tileType == TileType.GOAL);
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
		return (LevelController.getCurrentTime() >= isOnFireUntil)? this.moveMask : new Boolean[] {false,false,false,false};
	}

	public Boolean[] getNeighbourMoveMask() {
		return (player == null)? this.getMoveMask() : new Boolean[] {false,false,false,false};
	}

	public Boolean canMoveTo(){
		return (LevelController.getCurrentTime() >= isOnFireUntil) && (player == null);
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
		String url;
		if (isFixed) {
			url = this.tileType.imageURL.substring(0,this.tileType.imageURL.length()-4) + "_fixed.png";
			//System.out.println(this.tileType.imageURL.substring(0,this.tileType.imageURL.length()-4) + "_fixed.png");
			//url = this.tileType.imageURL;
		} else {
			url = this.tileType.imageURL;
		}

		Image img = new Image(url, renderSize, renderSize, false, false);

		ImageView iv = new ImageView(img);
		iv.setRotate(90 * this.getOrientation());

		StackPane stack = new StackPane(iv);
		stack.setStyle("-fx-border-width: 1px; -fx-border-color: darkgrey");

//		if (isFixed) {
//			Image fixedImage = new Image("source/resources/img/fixed_tile.png", renderSize, renderSize, false, false);
//			ImageView fixedImageView = new ImageView(fixedImage);
//			stack.getChildren().addAll(fixedImageView);
//		}

		if (isFrozenUntil > LevelController.getCurrentTime()) {
			Image fixedImage = new Image("source/resources/img/frozen_tile.png", renderSize, renderSize, false, false);
			ImageView fixedImageView = new ImageView(fixedImage);
			fixedImageView.setOpacity(0.5);
			stack.getChildren().addAll(fixedImageView);
		}

		if (isOnFireUntil > LevelController.getCurrentTime()) {
			Image fixedImage = new Image("source/resources/img/fire_tile.png", renderSize, renderSize, false, false);
			ImageView fixedImageView = new ImageView(fixedImage);
			fixedImageView.setOpacity(0.5);
			stack.getChildren().addAll(fixedImageView);
		}

		if (this.getPlayer() != null) {
			String playerImageURL = "source/resources/img/player_" + this.getPlayer().getIdInGame() + ".png";
			Image playerImage = new Image(playerImageURL, renderSize * playerToTileScaling, renderSize * playerToTileScaling, false, false);
			ImageView playerImageView = new ImageView(playerImage);
			stack.getChildren().add(playerImageView);
		}

		return stack;
	}

	/**
	 * Using the floor tiles orientation and default move mask of it's TileType, get an accurate move mask.
	 * @return This FloorTiles move mask with orientation accounted for
	 */
	private Boolean[] calculateMoveMask() {
		// Shift the array to the right depending on orientation
		Boolean[] toReturn = this.tileType.defaultMoveMask.clone();
		for (int i = 0; i < orientation; i++) {
			Boolean tmp = toReturn[3];
			System.arraycopy(toReturn, 0, toReturn, 1, 3);
			toReturn[0] = tmp;
		}
		return toReturn;
	}
}
