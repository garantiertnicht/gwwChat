package de.garantiertnicht.Weichware.gwwChat.api;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class Login {
    public final class LoginFailedException extends Exception {}

    private String name;
    private String token;
    private String pin;
    private String device;
    private int expier;

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

    public int getExpier() {
        return expier;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public String getDevice() {
        return device;
    }
}