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
        //sendGET(GET_URL);
        //sendGET(GET_MSG_OF_THE_DAY);
        //System.out.println(solvePuzzle("AZZ", 2));
        //System.out.println(solvePuzzleTwo("CAB"));
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
            System.out.println("Got this from request: " + finalResponse);
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

    private static String solvePuzzle(String puzzle, int shift) {
        String answer = "";
        for (int x = 1; x <= puzzle.length(); x++) {
            char c = (puzzle.charAt(x - 1));
            if (x % 2 == 0) {
                System.out.println("Works?");
                if (c > 'Z') {
                    answer += (char) (puzzle.charAt(x - 1) - (23 - shift));
                } else {
                    answer += (char) (puzzle.charAt(x - 1) + shift);
                }
            } else {
                if (c < 'A') {
                    System.out.println("works2");
                    answer += (char) (puzzle.charAt(x - 1) + (23 + shift));
                } else {
                    System.out.println("works3");
                    answer += (char) (puzzle.charAt(x - 1) - shift);
                }
            }
        }
        return answer;
    }

    private static String solvePuzzleTwo(String puzzle) {
        String answer = "";
        System.out.println("Starting a solve, got given: " + puzzle);

        for (int i = 0; i < puzzle.length(); i++) {
            char currentChar = puzzle.charAt(i);
            int currentCharPos = alphabetPos(currentChar);
            int shiftAmount = i + 1;
            boolean shiftingBackwards = i % 2 == 0;

            System.out.println("Letter: " + currentChar + " with value " + currentCharPos + " will be shifted by " + shiftAmount + " and shiftingBackwards is " + shiftingBackwards);

            int resultingLetterPosition;

            if (shiftingBackwards) {
                System.out.println("Shifting backwards");
                resultingLetterPosition = (currentCharPos - shiftAmount) % alphabet.length;
                if (resultingLetterPosition > -1) {
                    answer += alphabet[resultingLetterPosition];
                } else {
                    answer += alphabet[alphabet.length + resultingLetterPosition];
                }
            } else {
                System.out.println("Shifting forwards");
                resultingLetterPosition = (currentCharPos + shiftAmount) % alphabet.length;
                answer += alphabet[resultingLetterPosition];
            }

            System.out.println("Shifted " + currentChar + " to " + answer.charAt(i));
        }

        System.out.println(answer);
        answer = "CS-230" + answer;
        answer += answer.length();
        System.out.println(answer);
        return answer;
    }

    public static String getPuzzleMessage() {
        String endResult = "";
        try {
            String givenPuzzle = sendGET(GET_URL);
            String solvedPuzzle = solvePuzzleTwo(givenPuzzle);
            endResult = sendGET(GET_MSG_OF_THE_DAY + solvedPuzzle);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return endResult;
    }
}
