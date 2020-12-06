package source.labyrinth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Message of the Day class gets the encoded message,
 * decodes it and gets the decoded message of the day.
 * @author Ian Lavin Rady, Fillip Serov
 */
public class MessageOfTheDay {

	private static final String GET_URL = "http://cswebcat.swansea.ac.uk/puzzle";
	private static final String GET_MSG_OF_THE_DAY = "http://cswebcat.swansea.ac.uk/message?solution=";
	private static final char[] ALPHABET = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	/**
	 * Method to call message of the day and place it in main menu.
	 * @return method to get Message of the Day.
	 */
	public static String getMessageOfTheDay() {
		return MessageOfTheDay.getPuzzleMessage();
	}

	/**
	 * Puts the decoded puzzle together and sends a final get request to a website to get mssg of the day.
	 * @return the message of the day in it's full glory.
	 */
	public static String getPuzzleMessage() {
		String endResult = "";
		try {
			String givenPuzzle = sendGET(GET_URL);
			String solvedPuzzle = solvePuzzle(givenPuzzle);
			endResult = sendGET(GET_MSG_OF_THE_DAY + solvedPuzzle);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return endResult;
	}

	/**
	 * sends the get request to desired website and establishes a connection,
	 * finally it gets the response code(checks if request was processed) and
	 * message from website.
	 * @param getURL gets the URL.
	 * @return final response(answer, response code).
	 * @throws IOException handles exceptions thrown with buffer reader.
	 */
	private static String sendGET(String getURL) throws IOException {
		URL obj = new URL(getURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		String finalResponse = "";

		//This way we know if the request was processed successfully or there was any HTTP error message thrown.
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code : " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer buffer = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				buffer.append(inputLine);
			}
			in.close();

			// print result
			finalResponse = buffer.toString();
		} else {
			System.out.println("GET request not worked");
		}
		return finalResponse;
	}

	/**
	 * It locates the position of a letter in the ALPHABET.
	 * @param l letter in ALPHABET.
	 * @return the letter if its in range else returns -1 if there is an error.
	 */
	private static int alphabetPos(char l) {
		for (int x = 0; x < ALPHABET.length; x++) {
			if (ALPHABET[x] == l) {
				return x;
			}
		}
		return -1;
	}

	/**
	 * It solves puzzle and shifts the chars the right amount depending on index.
	 * @param puzzle the String to be decoded.
	 * @return the String decoded along with the requirements e.g CS-230 and word count.
	 */
	private static String solvePuzzle(String puzzle) {
		String answer = "";

		for (int i = 0; i < puzzle.length(); i++) {
			char currentChar = puzzle.charAt(i);
			int currentCharPos = alphabetPos(currentChar);
			int shiftAmount = i + 1;
			boolean shiftingBackwards = i % 2 == 0;
			int resultingLetterPosition;

			if (shiftingBackwards) {
				resultingLetterPosition = (currentCharPos - shiftAmount) % ALPHABET.length;
				if (resultingLetterPosition > -1) {
					answer += ALPHABET[resultingLetterPosition];
				} else {
					answer += ALPHABET[ALPHABET.length + resultingLetterPosition];
				}
			} else {
				resultingLetterPosition = (currentCharPos + shiftAmount) % ALPHABET.length;
				answer += ALPHABET[resultingLetterPosition];
			}
		}
		answer = "CS-230" + answer;
		answer += answer.length();
		return answer;
	}
}
