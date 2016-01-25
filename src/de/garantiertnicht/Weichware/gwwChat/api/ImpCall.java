package de.garantiertnicht.Weichware.gwwChat.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Contains some static Methods for connecting to the API.
 * @author garantiertnicht
 */
public class ImpCall {

    static  {
        //If you dont do this, CloudFlare will refuse to connect!
        System.setProperty("http.agent", "gwwChat/0.1");
    }

    /**
     * Calls the Specified IMP-v3 Page.
     * @param apiPage The API-Page to add
     * @param postData Some fancy Post data, needed for Login and Chat
     * @return A JSON-Object of the returned data. "Status" Should be checked (success or error)
     * @throws IOException If the connection fails
     */
    public static JsonObject call(String apiPage, String postData) throws IOException {
        URL url = new URL(String.format("https://api-v3.imperium1871.de/%s", apiPage));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Content-Length", String.valueOf(postData.length()));

        Writer writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(postData);
        writer.flush();

        String response = "";
        Scanner scan = new Scanner(con.getInputStream());
        while(scan.hasNextLine())
            response += scan.nextLine();

        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject obj = reader.readObject();
        reader.close();

        return obj;
    }

    /**
     * Calls the Specified IMP-v3 Page.
     * @param apiPage The API-Page to add
     * @return A JSON-Object of the returned data. "Status" Should be checked (success or error)
     * @throws IOException If the connection fails
     */
    public static JsonObject call(String apiPage) throws IOException {
        URL url = new URL(String.format("https://api-v3.imperium1871.de/%s", apiPage));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setDoInput(true);
        con.setDoOutput(false);
        con.setUseCaches(false);

        String response = "";
        Scanner scan = new Scanner(con.getInputStream());
        while(scan.hasNextLine())
            response += scan.nextLine();

        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject obj = reader.readObject();
        reader.close();

        return obj;
    }
}
