package source.labyrinth;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ProfileManager deals with profiles: retrieving them from file, saving them, adding / deleting them.
 * @author Fillip Serov
 */
public class ProfileManager {
	private final File profilesFile = new File("source/resources/profiles/profiles.txt");

	private int nextID; // If a new profile is created it will be given this id, which is then incremented
	private ArrayList<Profile> profiles = new ArrayList<Profile>();

	/**
	 * Create a ProfileManager that will automatically load all player profiles and be ready to work with them.
	 */
	public ProfileManager() {
		Scanner in;
		try {
			in = new Scanner(profilesFile);
			buildProfiles(in);
		} catch (FileNotFoundException e) {
			System.out.println("Profiles file wasn't found.");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void writeProfilesToFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(profilesFile));
			bw.write(Integer.toString(this.nextID));
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
	public Boolean createNewProfile(String newName) {
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
	 * Get all profiles as an ArrayList.
	 * @return ArrayList of all profiles.
	 */
	public ArrayList<Profile> getProfiles() {
		return profiles;
	}

	/**
	 * Get a profile by name. Null if no profile found.
	 * @param name Name to search
	 * @return Relevant Profile or null
	 */
	public Profile getProfileByName(String name) {
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
	public Profile getProfileById(int id) {
		for (Profile p : profiles) {
			if (p.getID() == id) {
				return p;
			}
		}
		return null;
	}

	private void buildProfiles(Scanner mainIn) {
		this.nextID = mainIn.nextInt();

		// Prevent crash on empty profile files
		if (mainIn.hasNextLine()) {
			mainIn.nextLine();
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

		System.out.println("Loaded " + profiles.size() + " profiles. nextID is " + this.nextID);
	}
	private ArrayList<Profile> getArrayList(){
	    return  profiles;
    }
}
