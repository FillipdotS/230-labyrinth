package source.labyrinth;

import java.io.Serializable;

/**
 * Action Tile class sets up the action tiles images and gets their action type(fire,ice,etc..).
 * @author Ian Lavin Rady
 * @author Erik Miller
 * @version 1.0
 */
public class ActionTile extends Tile implements Serializable {

	/**
	 * Represents the different types of action tiles available in the game.
	 */
	public enum ActionType {
		FIRE("source/resources/img/action_tile_fire.png"),
		ICE("source/resources/img/action_tile_ice.png"),
		DOUBLEMOVE("source/resources/img/action_tile_double_move.png"),
		BACKTRACK("source/resources/img/action_tile_back_track.png");

		public final String imageURL;

		/**
		 * Constructor for imageURL.
		 * @param imageURL the imageURL for the action type.
		 */
		ActionType(String imageURL) {
			this.imageURL = imageURL;
		}
	}

	private final ActionType ACTIONTYPE;

	/**
	 * Constructor for the action type.
	 * @param actionType the action type(fire, ice, backtrack, etc..)
	 */
	public ActionTile(ActionType actionType) {
		this.ACTIONTYPE = actionType;
	}

	/**
	 * Gets the action type.
	 * @return the action type.
	 */
	public ActionType getType() {
		return this.ACTIONTYPE;
	}
}
