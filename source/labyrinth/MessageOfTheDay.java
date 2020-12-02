package source.labyrinth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageOfTheDay {

    private static final String GET_URL = "http://cswebcat.swansea.ac.uk/puzzle";

    public static void main(String[] args) throws IOException {
        sendGET(GET_URL);
        solvePuzzle();
    }

    private static String sendGET(String getUrl) throws IOException {
        URL obj = new URL(GET_URL);
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
        char[] result = new char[puzzle.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (char) (puzzle.charAt(i));
        }
        answer = "CS-230";

        return answer;
    }



    public static String getPuzzleMessage() {
        try {
            String puzzle = sendGET(GET_URL);
            String solvePuzzle = solvePuzzle(puzzle);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
