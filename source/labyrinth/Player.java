package source.labyrinth;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Player represents an the actual player on the board, storing it's past positions, whether it has been backtracked
 * and etc. An individual player does not know when it is their turn since they do not need to know.
 * @author Fillip Serov
 */
public class Player {
	private final Profile associatedProfile;

	private int[][] pastPositions;
	private Boolean hasBeenBacktracked;
	private ArrayList<ActionTile> availableActions;

	/**
	 * @param profile The profile this player is assigned to. If no profile, give null.
	 */
	public Player(Profile profile) {
		this.associatedProfile = profile;

		this.pastPositions = new int[2][2];
		this.hasBeenBacktracked = false;
		this.availableActions = new ArrayList<>();
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
}
