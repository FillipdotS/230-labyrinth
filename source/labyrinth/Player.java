package source.labyrinth;

import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * Player represents an the actual player on the board, storing it's past positions, whether it has been backtracked
 * and etc. An individual player does not know when it is their turn since they do not need to know.
 * @author Fillip Serov
 */
public class Player {
	private final Profile associatedProfile;
	private final int idInGame;

	private int[][] pastPositions;
	private Boolean hasBeenBacktracked;
	private HashMap<ActionTile.ActionType, Float> actions;
	private FloorTile standingOn;

	/**
	 * @param profile The profile this player is assigned to. If no profile, give null.
	 */
	public Player(int idInGame, Profile profile) {
		this.idInGame = idInGame;
		this.associatedProfile = profile;

		this.pastPositions = new int[2][2];
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

	public int getIdInGame() {
		return this.idInGame;
	}

	public float getActionAmount(ActionTile.ActionType at) {
		return this.actions.get(at);
	}

	public void setActionAmount(ActionTile.ActionType at, float newAmount) {
		this.actions.replace(at, newAmount);
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
	 * @return The FloorTile this player is standing on
	 */
	public FloorTile getStandingOn() {
		return standingOn;
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
		return associatedProfile;
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
		int[] temp = this.pastPositions[0];
		this.pastPositions[0] = new int[] {x, y};
		this.pastPositions[1] = temp;
	}

	public void removeAction(ActionTile.ActionType type) {
		actions.replace(type,actions.get(type)-1);
	}
}
