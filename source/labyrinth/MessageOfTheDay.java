package source.labyrinth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageOfTheDay {

    private static final String GET_URL = "http://cswebcat.swansea.ac.uk/puzzle";
    private static final String GET_MSG_OF_THE_DAY = "http://cswebcat.swansea.ac.uk/message?solution=";
    private static final char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static void main(String[] args) throws IOException {
        System.out.println(getPuzzleMessage());
    }

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

    private static int alphabetPos(char l) {
        for (int x = 0; x < alphabet.length; x++) {
            if (alphabet[x] == l) {
                return x;
            }
        }
        return -1;
    }

    private static String solvePuzzle(String puzzle) {
        String answer = "";

        for (int i = 0; i < puzzle.length(); i++) {
            char currentChar = puzzle.charAt(i);
            int currentCharPos = alphabetPos(currentChar);
            int shiftAmount = i + 1;
            boolean shiftingBackwards = i % 2 == 0;
            int resultingLetterPosition;

            if (shiftingBackwards) {
                resultingLetterPosition = (currentCharPos - shiftAmount) % alphabet.length;
                if (resultingLetterPosition > -1) {
                    answer += alphabet[resultingLetterPosition];
                } else {
                    answer += alphabet[alphabet.length + resultingLetterPosition];
                }
            } else {
                resultingLetterPosition = (currentCharPos + shiftAmount) % alphabet.length;
                answer += alphabet[resultingLetterPosition];
            }
        }
        answer = "CS-230" + answer;
        answer += answer.length();
        return answer;
    }

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

    public static String getMessageOfTheDay() {
        return MessageOfTheDay.getPuzzleMessage();
    }
}
