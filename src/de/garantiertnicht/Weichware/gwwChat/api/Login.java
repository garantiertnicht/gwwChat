package de.garantiertnicht.Weichware.gwwChat.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;


/**
 * Class used to loin.
 * @author garantiertnicht
 */
public class Login {

    /**
     * Thrown when the Login failed.
     */
    public final class LoginFailedException extends Exception {}

    private String name;
    private String token;
    private String pin;
    private String device;
    private int expier;

    /**
     * Logs in with the given Pin.
     * @param pin A User-Pin
     * @param app The App, used here to generate an unused device.
     * @throws IOException If the connection fails
     * @throws LoginFailedException If the server refuses the login. Check "message" for more details.
     */
    public Login(String pin, String app) throws IOException, LoginFailedException {
        System.setProperty("http.agent", "gwwChat/0.1");
        JsonObject json = ImpCall.call("token/validatePin", String.format("PIN=%s&MODEL=gwwGeneric_%s", URLEncoder.encode(pin), URLEncoder.encode(app)));

        if(!json.getString("status").equalsIgnoreCase("success"))
            throw new LoginFailedException();

        name = json.getString("plyName");
        token = json.getString("token");
        expier = json.getInt("expires");
        this.pin = pin;
        this.device = String.format("gwwGeneric_%s", app);
    }

    /**
     * Gets the Timestamp, when the TOKEN gets Invalid.<br/>
     * As a token is valif 2 weeks, It remains unchecked here.
     * @return The date when the token Expiers, as Unix Timestamp (Secounds science 01.01.1970)
     */
    public int getExpier() {
        return expier;
    }

    /**
     * Get a token to use.
     * @return The Token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Get the Playername of the player, wish was so stupid to use this app.
     * @return A playername
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the pin, in case you want to renew the Login because of {@link Login#getExpier()}.
     * @return The Pin, as String.
     */
    public String getPin() {
        return pin;
    }

    /**
     * Gets the Device string used to login.
     * @return The Device String.
     */
    public String getDevice() {
        return device;
    }
}