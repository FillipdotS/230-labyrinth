package source.labyrinth;

import java.io.Serializable;

/**
 * Profile is used to store the profiles that have been loaded in. The only class that should create new
 * instances of Profile is ProfileManager, since ProfileManager will ensure new / updated profiles are saved
 * to file.
 * @author Fillip Serov
 */
public class Profile implements Serializable {
	private final String name;
	private final int id;
	private int totalPlayed;
	private int wins;
	private int losses;

	/**
	 * Constructor for absolutely new profiles where all stats are 0. Should only be used by
	 * ProfileManager as otherwise any updates to this profile won't be saved permanently.
	 * @param name Name for profile
	 * @param id ID for profile
	 */
	public Profile(String name, int id) {
		this(name, id, 0, 0, 0);
	}

	/**
	 * Constructor for Profiles that are loaded from file. Should only be used by ProfileManager as otherwise
	 * any updates to this profile won't be saved permanently.
	 * @param name Name of profile
	 * @param id ID for profile
	 * @param totalPlayed Total games played
	 * @param wins Total wins
	 * @param losses Total losses
	 */
	public Profile(String name, int id, int totalPlayed, int wins, int losses) {
		this.name = name;
		this.id = id;
		this.totalPlayed = totalPlayed;
		this.wins = wins;
		this.losses = losses;
	}

	/**
	 * @return the name of the Profile
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the ID of the Profile
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the number of total games played
	 */
	public int getTotalPlayed() {
		return totalPlayed;
	}

	/**
	 * Increase the number of total games playes
	 */
	public void addTotalPlayed() {
		this.totalPlayed += 1;
	}

	/**
	 * @return the number of win games
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * Increase the number of win games
	 */
	public void addWin() {
		this.wins += 1;
	}

	/**
	 * @return the number of losses
	 */
	public int getLosses() {
		return losses;
	}

	/**
	 * Increase the number of losses
	 */
	public void addLoss() {
		this.losses += 1;
	}
}
