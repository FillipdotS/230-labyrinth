package source.labyrinth;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ProfileManager deals with profiles: retrieving them from file, saving them, adding / deleting them.
 * It is entirely static so there is no need to create an instance of it.
 * @author Fillip Serov
 */
public final class ProfileManager {
	private static final File PROFILESFILE = new File("source/resources/profiles/profiles.txt");
	private static int nextID; // A new profile will be given this id, which is then incremented
	private static ArrayList<Profile> profiles;

	/**
	 * performSetup will read the profile data from file. Since the entire class is static, this only has to
	 * be done once when the game is launched.
	 */
	public static void performSetup() {
		// If this is the first time we call this
		if (profiles == null) {
			profiles = new ArrayList<>();

			Scanner in;
			try {
				in = new Scanner(PROFILESFILE);
				buildProfiles(in);
			} catch (FileNotFoundException e) {
				System.out.println("Profiles file wasn't found, making one now.");
				// e.printStackTrace();
				makeProfileFile();
				performSetup();
			}
		}
	}

	/**
	 * Writes the profiles to a file and saves them.
	 */
	public static void writeProfilesToFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(PROFILESFILE));
			bw.write(Integer.toString(nextID));
			bw.newLine();

			for (Profile p : profiles) {
				bw.write(p.getName());
				bw.newLine();

				String stats = p.getID() + "," + p.getTotalPlayed() + "," + p.getWins() + "," + p.getLosses();
				bw.write(stats);
				bw.newLine();
			}
			bw.close();

			System.out.println("Saved current profiles to file.");
		} catch (IOException e) {
			System.out.println("Could not write profiles to file.");
			e.printStackTrace();
		}
	}

	/**
	 * Create a new profile which will permanently saved. The profile name must be unique.
	 * @param newName Name for the new profile (must be unique).
	 * @return true if the given name is unique and the profile was created, false otherwise.
	 */
	public static Boolean createNewProfile(String newName) {
		if (getProfileByName(newName) != null) {
			return false;
		}

		// Add the new profile to the beginning of the ArrayList ONLY because it will then appear at the
		// top of the table in the profile menu when a user creates a new one.
		profiles.add(0, new Profile(newName, nextID));
		nextID++;

		writeProfilesToFile();
		return true;
	}

	/**
	 * Deletes a profile via reference if it exists.
	 * @param toDelete Profile to delete.
	 */
	public static void deleteProfile(Profile toDelete) {
		System.out.println("Deleting profile " + toDelete.getName());
		profiles.remove(toDelete);
		writeProfilesToFile();
	}

	/**
	 * Get all profiles as an ArrayList.
	 * @return ArrayList of all profiles.
	 */
	public static ArrayList<Profile> getProfiles() {
		return profiles;
	}

	/**
	 * Get a profile by name. Null if no profile found.
	 * @param name Name to search
	 * @return Relevant Profile or null
	 */
	public static Profile getProfileByName(String name) {
		for (Profile p : profiles) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Get a profile by id. Null if no profile found.
	 * @param id ID to search
	 * @return Relevant Profile or null
	 */
	public static Profile getProfileById(int id) {
		for (Profile p : profiles) {
			if (p.getID() == id) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Will create a profile file to be used for saving profiles.
	 */
	private static void makeProfileFile() {
		try {
			PROFILESFILE.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not create new profiles file, exiting...");
			System.exit(0);
		}
	}

	/**
	 * Sets up the profiles
	 * @param mainIn main method scanner
	 */
	private static void buildProfiles(Scanner mainIn) {
		// Prevent crash on empty profile files
		if (mainIn.hasNextLine()) {
			nextID = mainIn.nextInt();
			mainIn.nextLine();
		} else {
			nextID = 0; // Fresh profiles file that was just created
		}

		while (mainIn.hasNextLine()) {
			String profileName = mainIn.nextLine();

			Scanner lineIn = new Scanner(mainIn.nextLine());
			lineIn.useDelimiter(",");
			int id = lineIn.nextInt();
			int totalPlayed = lineIn.nextInt();
			int totalWins = lineIn.nextInt();
			int totalLosses = lineIn.nextInt();

			profiles.add(new Profile(profileName, id, totalPlayed, totalWins, totalLosses));
		}

		System.out.println("Loaded " + profiles.size() + " profiles. nextID is " + nextID);
	}
}
