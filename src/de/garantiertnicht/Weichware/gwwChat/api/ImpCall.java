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

public class ImpCall {

    static  {
        System.setProperty("http.agent", "gwwChat/0.1");
    }

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
