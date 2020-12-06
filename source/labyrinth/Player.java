package source.labyrinth;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Player represents an the actual player on the board, storing it's past positions, whether it has been backtracked
 * and etc. An individual player does not know when it is their turn since they do not need to know.
 * @author Fillip Serov
 */
public class Player implements Serializable {
	private final int associatedProfileID;
	private final int idInGame;

	private int[][] pastPositions;
	private Boolean hasBeenBacktracked;
	private HashMap<ActionTile.ActionType, Float> actions;
	private FloorTile standingOn;

	/**
	 * @param idInGame The id of the player in game (int 0 to 3).
	 * @param profileID The id of the profile this player is assigned to. If no profile, give null.
	 */
	public Player(int idInGame, int profileID) {
		this.idInGame = idInGame;
		this.associatedProfileID = profileID;

		this.pastPositions = new int[3][2];
		this.hasBeenBacktracked = false;
		this.actions = new HashMap<>();
		this.actions.put(ActionTile.ActionType.FIRE, 0.0f);
		this.actions.put(ActionTile.ActionType.ICE, 0.0f);
		this.actions.put(ActionTile.ActionType.BACKTRACK, 0.0f);
		this.actions.put(ActionTile.ActionType.DOUBLEMOVE, 0.0f);
	}

	/**
	 * @param player Player number, 0 to 3
	 * @return JavaFX Color
	 */
	public static Color getPlayerColor(int player) {
		switch (player) {
			case 0:
				return Color.BLUE;
			case 1:
				return Color.RED;
			case 2:
				return Color.GREEN;
			case 3:
				return Color.PURPLE;
			default:
				return Color.GREY;
		}
	}

	/**
	 * @return 0 to 3 int representing which one of the 4 players this player is in-game.
	 */
	public int getIdInGame() {
		return this.idInGame;
	}

	/**
	 * @param actionType ActionType to get
	 * @return Amount of this ActionType this player has.
	 */
	public float getActionAmount(ActionTile.ActionType actionType) {
		return this.actions.get(actionType);
	}

	/**
	 * @param actionType ActionType to change
	 * @param newAmount New amount
	 */
	public void setActionAmount(ActionTile.ActionType actionType, float newAmount) {
		this.actions.replace(actionType, newAmount);
	}

	/**
	 * @return Total amount of action tiles this player has
	 */
	public int getFullActionAmount() {
		int fullAmount = 0;
		for (ActionTile.ActionType at : ActionTile.ActionType.values()) {
			fullAmount += (int) Math.ceil(getActionAmount(at));
		}
		return fullAmount;
	}

	/**
	 * @param standingOn The new FloorTile to hold this player.
	 */
	public void setStandingOn(FloorTile standingOn) {
		if (this.standingOn != null) {
			this.standingOn.setPlayer(null); // Wipe the reference on the previous position
		}
		this.standingOn = standingOn;
		this.standingOn.setPlayer(this);
	}

	/**
	 * @return The profile this player is assigned to. Can be null.
	 */
	public Profile getAssociatedProfile() {
		return ProfileManager.getProfileById(this.associatedProfileID);
	}

	/**
	 * @return Boolean representing if this player has been backtracked.
	 */
	public Boolean getHasBeenBacktracked() {
		return this.hasBeenBacktracked;
	}

	/**
	 * @param toSet Boolean representing if this player has been backtracked.
	 */
	public void setHasBeenBacktracked(Boolean toSet) {
		this.hasBeenBacktracked = toSet;
	}

	/**
	 * Add an (x, y) position to keep track of. Only tracks the last 2 positions given.
	 * @param x X-position
	 * @param y Y-position
	 */
	public void addToPastPositions(int x, int y) {
		// Set the newest position to index 0, move the other one up to index 1
		this.pastPositions[2] = this.pastPositions[1];
		this.pastPositions[1] = this.pastPositions[0];
		this.pastPositions[0] = new int[] {x, y};
	}

	/**
	 * @return Matrix containing the past 3 (x, y) positions of this player
	 */
	public int[][] getPastPositions() {
		return pastPositions;
	}

	/**
	 * Removes 1 use of a specific action from this player.
	 * @param type ActionType to remove one of.
	 */
	public void removeAction(ActionTile.ActionType type) {
		actions.replace(type,actions.get(type)-1);
	}
}
