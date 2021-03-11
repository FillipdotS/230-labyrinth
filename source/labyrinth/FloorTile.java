package source.labyrinth;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import source.labyrinth.controllers.LevelController;

import java.io.Serializable;

/**
 * FloorTile represents a physical tile on the game board on which players can walk on. FloorTiles can be set on
 * fire, frozen, or both at once.
 * @author Fillip Serov
 */
public class FloorTile extends Tile implements Serializable {
	/**
	 * The different types of floor tile that an instance of FloorTile can be. Changes the default move mask and
	 * the image used for the tile.
	 */
	public enum FloorType {
		/**
		 * By default can move north (0), south (2).
		 */
		STRAIGHT(new Boolean[]{true, false, true, false}, "source/resources/img/tile_straight.png"),
		/**
		 * By default can move north (0), east (1).
		 */
		CORNER(new Boolean[]{true, true, false, false}, "source/resources/img/tile_corner.png"),
		/**
		 * By default can move east (1), south (2), west (3)
		 */
		TSHAPE(new Boolean[]{false, true, true, true},"source/resources/img/tile_tshape.png"),
		/**
		 * By default can move north (0), east (1), south (2), west (3)
		 */
		GOAL(new Boolean[]{true, true, true, true}, "source/resources/img/tile_goal.png");

		public final String imageURL;

		// Each tile type has their default move mask, and a string to their image.
		private final Boolean[] defaultMoveMask;

		FloorType(Boolean[] defaultMoveMask, String imageURL) {
			this.defaultMoveMask = defaultMoveMask;
			this.imageURL = imageURL;
		}
	}

	private final double playerToTileScaling = 0.6f;
	private final FloorType floorType;

	private Boolean[] moveMask; // Specifically THIS tiles move mask, which has been changed by orientation
	private int orientation;
	private Boolean isFixed;
	private int isOnFireUntil;
	private int isFrozenUntil;
	private Player player;

	/**
	 * Create a new FloorTile with a certain orientation and FloorType
	 * @param orientation Orientation of this tile, 0 to 3
	 * @param floorType FloorType of this tile
	 */
	public FloorTile(int orientation, FloorType floorType) {
		this(orientation,floorType,false);
	}

	/**
	 * Create a new FloorTile with a certain orientation, FloorType and isFixed value
	 * @param orientation Orientation of this tile, 0 to 3
	 * @param floorType FloorType of this tile
	 * @param isFixed boolean for whether this floortile is fixed or not
	 */
	public FloorTile(int orientation, FloorType floorType, boolean isFixed) {
		this.orientation = orientation;
		this.floorType = floorType;
		this.isFrozenUntil=-1;
		this.isOnFireUntil=-1;
		this.isFixed = isFixed;

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
		return (this.floorType == FloorType.GOAL);
	}
	/**
	 * Freezes this for 1 turn
	 */
	public void freeze() {
		isFrozenUntil = LevelController.getCurrentTime() + LevelController.getTimeForFullLoop();
	}

	/**
	 * Sets tile on fire for 2 turns
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

	/**
	 * @return int 0 to 3 representing this FloorTile's orientation
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * @return Boolean array representing this tile's move mask
	 */
	public Boolean[] getMoveMask() {
		return (LevelController.getCurrentTime() >= isOnFireUntil)? this.moveMask : new Boolean[] {false, false, false, false};
	}

	/**
	 * @return Get move mask as if it is a FloorTile next to a player
	 */
	public Boolean[] getNeighbourMoveMask() {
		return (player == null) ? this.getMoveMask() : new Boolean[] {false, false, false, false};
	}

	/**
	 * Get FloorType enum value for this floor tile.
	 * @return FloorType of this floor tile.
	 */
	public FloorType getFloorType() {
		return this.floorType;
	}

	/**
	 * @return Boolean showing if a player could stand on this tile.
	 */
	public Boolean canMoveTo(){
		return (LevelController.getCurrentTime() >= isOnFireUntil) && (player == null);
	}

	/**
	 * @return Boolean representing whether this FloorTile is permanently fixed.
	 */
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

	/**
	 * @param isOnFireUntil How many individual player turns should this be on fire for
	 */
	public void setIsOnFireUntil(int isOnFireUntil) {
		this.isOnFireUntil = isOnFireUntil;
	}

	/**
	 * @param isFrozenUntil How many individual player turns should this be frozen for
	 */
	public void setIsFrozenUntil(int isFrozenUntil) {
		this.isFrozenUntil = isFrozenUntil;
	}

	/**
	 * @param fixed Set this tile to be permanently fixed
	 */
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
			url = this.floorType.imageURL.substring(0, this.floorType.imageURL.length() - 4) + "_fixed.png";
		} else {
			url = this.floorType.imageURL;
		}

		Image img = new Image(url, renderSize, renderSize, false, false);

		ImageView iv = new ImageView(img);
		iv.setRotate(90 * this.getOrientation());

		StackPane stack = new StackPane(iv);
		stack.setStyle("-fx-border-width: 1px; -fx-border-color: darkgrey");

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
		Boolean[] toReturn = this.floorType.defaultMoveMask.clone();
		for (int i = 0; i < orientation; i++) {
			Boolean tmp = toReturn[3];
			System.arraycopy(toReturn, 0, toReturn, 1, 3);
			toReturn[0] = tmp;
		}
		return toReturn;
	}
}
