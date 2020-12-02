package source.labyrinth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageOfTheDay {

    private static final String GET_URL = "http://cswebcat.swansea.ac.uk/puzzle";

    public static void main(String[] args) throws IOException {
        sendGET();
    }

    private static void sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        //This way we know if the request was processed successfully or there was any HTTP error message thrown.
        int responseCode = con.getResponseCode();
		System.out.println("GET Response Code : " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer puzzle = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            puzzle.append(inputLine);
        }
        in.close();

        // print result
        System.out.println(puzzle.toString());
        } else {
        System.out.println("GET request not worked");
		}

    }

}
