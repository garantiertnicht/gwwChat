/*
 * Copyright (c) 2016, garantiertnicht
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by garantiertnicht Weichware.
 * 4. Neither the name of garantiertnicht Weichware nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GARANTIERTNICHT WEICHWARE ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GARANTIERTNICHT WEICHWARE BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.garantiertnicht.Weichware.gwwChat.api;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


/**
 * A class wich manages the settings.
 * I may rethink this logic!
 *
 * @deprecated I dont think this class is well desinged. It will change in near Futute!
 * @author garantiertnicht
 */

@Deprecated
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

        //I wish we could have a System.getProperty for an defauld Junk-Folder
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
