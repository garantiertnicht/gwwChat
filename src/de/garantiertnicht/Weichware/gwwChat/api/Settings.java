package de.garantiertnicht.Weichware.gwwChat.api;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Settings {

    public Properties properties;
    private String app;
    private String uri;

    public final class NoDefaultsException extends Exception {}

    /**
     * Initalize the Config file.
     * @param app The app to use.
     * @throws IOException If the File reading fails.
     * @throws NoDefaultsException If the App has no stored data. You should add a propertie with the apps name
     */
    public Settings(String app) throws IOException, NoDefaultsException {
        this.app = app;

        String uri = System.getProperty("user.home");
        String seperator = File.separator;

        String osName = System.getProperty("os.name");

        if(osName.contains("OS X")) {
            uri += seperator + "Library" + seperator + "Application Support";
        } else if(osName.contains("Windows")) {
            uri = System.getenv("APPDATA");
        }

        uri += seperator + ".gwwChat";

        Path path = FileSystems.getDefault().getPath(uri);
        if(!Files.exists(path))
            Files.createDirectory(path);

        uri += seperator + "settings.properties";

        this.uri = uri;

        path = FileSystems.getDefault().getPath(uri);

        if(!Files.exists(path)) {
            Files.createFile(path);
            throw new NoDefaultsException();
        }

        properties = new Properties();
        FileInputStream in = new FileInputStream(uri);
        properties.load(in);
        in.close();

        if(!properties.containsKey(app))
            throw new NoDefaultsException();
    }

    /**
     * Initalize the Config file.
     * @param app The app to use.
     * @param def Default settings
     * @throws IOException If the File reading fails.
     */
    public Settings(String app, Properties def) throws IOException {
        this.app = app;

        String uri = System.getProperty("user.home");
        String seperator = File.separator;

        String osName = System.getProperty("os.name");

        if(osName.contains("OS X")) {
            uri += seperator + "Library" + seperator + "Application Support";
        } else if(osName.contains("Windows")) {
            uri = System.getenv("APPDATA");
        }

        uri += seperator + ".gwwChat";

        Path path = FileSystems.getDefault().getPath(uri);
        if(!Files.exists(path))
            Files.createDirectory(path);

        uri += seperator + "settings.properties";

        this.uri = uri;

        path = FileSystems.getDefault().getPath(uri);

        if(!Files.exists(path)) {
            Files.createFile(path);
        }

        properties = new Properties();
        FileInputStream in = new FileInputStream(uri);
        properties.load(in);
        in.close();

        if(!properties.containsKey(app)) {
            addProperties(def);
            save();
        }
    }

    /**
     * Adds some default properties.
     * @param def The properzies to add
     */
    public void addProperties(Properties def) {
        properties.putAll(def);
        if(!properties.containsKey(app))
            properties.setProperty(app, "0");
    }

    /**
     * Saves the Properties back to disk. Better call twice!
     * @throws IOException
     */
    public void save() throws IOException {
        FileOutputStream out = new FileOutputStream(uri);
        properties.store(out, "");
        out.close();
    }


}
