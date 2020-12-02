package source.labyrinth;

import sun.plugin2.message.GetAppletMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageOfTheDay {

    private static final String GET_URL = "http://cswebcat.swansea.ac.uk/puzzle";

    public static void main(String[] args) throws IOException {
        sendGET(GET_URL);
        System.out.println(solvePuzzle("XMKVXXRKMYPNMLFF"));
    }

    private static String sendGET(String getURL) throws IOException {
        URL obj = new URL(getURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

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
            System.out.println(buffer.toString());
        } else {
            System.out.println("GET request not worked");
        }
        return "";
    }

    private static String solvePuzzle(String puzzle) {
        String answer = "";
        int len = puzzle.length();
        int x = 0;

        if (puzzle.charAt(x) % 2 == 0) {
            for (x = 0; x < len; x++) {
                int shift = puzzle.charAt(x);
                char c = (char) (puzzle.charAt(x) + shift);
                if(c > 'z') {
                    answer += (char)(puzzle.charAt(x) - (26-shift));
                } else {
                    answer += (char)(puzzle.charAt(x) + shift);
                }
            }
        } else {
            for (x = 0; x < len; x--) {
                int shift = puzzle.charAt(x);
                char c = (char) (puzzle.charAt(x) + shift);
                if(c > 'z') {
                    answer += (char)(puzzle.charAt(x) - (26-shift));
                } else {
                    answer += (char)(puzzle.charAt(x) + shift);
                }
            }
        }
        answer = "CS-230" + answer;

        return answer;
    }



    public static String getPuzzleMessage() {
        //String key = "";
        try {
            String puzzle = sendGET(GET_URL);
            String solvePuzzle = solvePuzzle(puzzle);
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return getPuzzleMessage();
    }

}
