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

package de.garantiertnicht.Weichware.gwwChat.GUI;

import de.garantiertnicht.Weichware.gwwChat.FilterList;
import de.garantiertnicht.Weichware.gwwChat.Main;
import de.garantiertnicht.Weichware.gwwChat.api.ImpCall;
import de.garantiertnicht.Weichware.gwwChat.api.Settings;
import de.garantiertnicht.Weichware.gwwChat.excHandler;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import javax.json.JsonArray;
import javax.json.JsonObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainCon implements Initializable {
    @FXML private ListView<String> plyList;
    @FXML private ListView<TextFlow> chat;
    @FXML private TextField txt;
    @FXML private Button send;

    //Content for Lists
    ObservableList<TextFlow> chatMsgs = null;
    ObservableList<String> names = null;

    private boolean reply = false;

    //Last Id
    String lastId = "";

    private static int unreadMsgs = 0;

    //Defauld delay between refreshes
    int delay = 3000;

    Timeline refresh = new Timeline(new KeyFrame(Duration.millis(delay), new EventHandler<ActionEvent>() {

        /**
         * This is our Refresh-Job for fetching new Messages!
         * @param e Advanced stuff
         */
        @Override
        public void handle(ActionEvent e) {
            boolean plyRefreshed = false;

            try {
                JsonObject newMsgs = ImpCall.call(String.format("chat/fetchChatAdvanced/%s", lastId));

                JsonArray msgLi = newMsgs.getJsonArray("messages");

                if (msgLi == null) //Something gone Wrong. I dont care! I <3 it!
                    return;

                //Read all new messages
                for (int i = 0; i < msgLi.size(); i++) {
                    //Nice Formatting stuff
                    TextFlow tf = new TextFlow();

                    /* We could get:
                     * uuid    Message UUID (for fetching next)
                     * ply     Player Name
                     * source  Type of Message
                     * time    Unix Timestamp of message
                     * msg     Hmm, I really dont know
                     */
                    JsonObject msg = msgLi.getJsonObject(i);

                    //Now the hard part: Formating different messages differently!

                    //Player joined
                    if (msg.getString("source").equalsIgnoreCase("join")) {
                        if(!msg.getString("ply").equalsIgnoreCase(Main.login.getName()) && reply)
                            unreadMsgs++;
                        Text t1 = new Text(msg.getString("ply"));
                        t1.setFill(Color.BLUE);
                        Text t2 = new Text(" hat den Server betreten.");
                        t2.setFill(Color.DARKBLUE);

                        tf.getChildren().addAll(t1, t2);
                        chatMsgs.add(tf);

                        if(!plyRefreshed) {
                            plyRefreshed = true;
                            refreshPlyList();
                        }

                    //Player left
                    } else if (msg.getString("source").equalsIgnoreCase("quit")) {
                        if(!msg.getString("ply").equalsIgnoreCase(Main.login.getName()) && reply)
                            unreadMsgs++;
                        Text t1 = new Text(msg.getString("ply"));
                        t1.setFill(Color.BLUE);
                        Text t2 = new Text(" hat den Server verlassen.");
                        t2.setFill(Color.DARKBLUE);

                        tf.getChildren().addAll(t1, t2);
                        chatMsgs.add(tf);

                        if(!plyRefreshed) {
                            plyRefreshed = true;
                            refreshPlyList();
                        }

                    //Proxy is shutting down
                    } else if (msg.getString("source").equalsIgnoreCase("shutdown")) {
                        Text t1 = new Text("~{");
                        t1.setFill(Color.DARKBLUE);
                        Text t2 = new Text("Imperium 1871");
                        t2.setFill(Color.RED);
                        Text t3 = new Text("}~ ");
                        t3.setFill(Color.DARKBLUE);
                        Text t4 = new Text("Der Server startet neu! Bitte habe einen Moment geduld.");
                        t4.setFill(Color.DARKRED);

                        tf.getChildren().addAll(t1, t2, t3, t4);
                        chatMsgs.add(tf);

                        //I dont want to find out what happens if we try to write messages now.
                        //Sure is sure!
                        send.setDisable(true);

                    //Proxy is starting again
                    } else if (msg.getString("source").equalsIgnoreCase("boot")) {
                        Text t1 = new Text("~{");
                        t1.setFill(Color.DARKBLUE);
                        Text t2 = new Text("Imperium 1871");
                        t2.setFill(Color.RED);
                        Text t3 = new Text("}~ ");
                        t3.setFill(Color.DARKBLUE);
                        Text t4 = new Text("Der Server startet wieder. Du solltest gleich wieder Chatten können.");
                        t4.setFill(Color.DARKRED);

                        tf.getChildren().addAll(t1, t2, t3, t4);
                        chatMsgs.add(tf);

                        send.setDisable(false);

                    //Ignore gwwBot-MSGs
                    } else if (msg.getString("source").equalsIgnoreCase("gwwBot")) {

                    //Most likly chat, or stuff that even I don't know.
                    } else {
                        if(!FilterList.isValidMsg(msg.getString("ply") + " " + msg.getString("msg"))) {
                            if(reply) {
                                //TODO included later again, after PMs possible
                               /* JsonObject stat = ImpCall.call("chat/sendMessage", String.format("SOURCE=%s&TOKEN=%s&MESSAGE=%s&MODEL=%s",
                                        URLEncoder.encode("gwwBot"), URLEncoder.encode(Main.login.getToken()),
                                        URLEncoder.encode("Die Nachicht \"" + msg.getString("msg") + "\" wurde von gwwChat " + "gefiltert."),
                                        URLEncoder.encode(Main.login.getDevice()))); */
                            }
                            continue;
                        }


                        if(!msg.getString("ply").equalsIgnoreCase(Main.login.getName()) && reply)
                            unreadMsgs++;
                        Text t1 = new Text("~{");
                        t1.setFill(Color.DARKBLUE);
                        Text t2 = new Text(msg.getString("ply"));
                        t2.setFill(Color.BLUE);
                        Text t3 = new Text("}~ ");
                        t3.setFill(Color.DARKBLUE);
                        Text t4 = new Text(msg.getString("msg"));
                        t4.setFill(Color.BLACK);

                        tf.getChildren().addAll(t1, t2, t3, t4);
                        chatMsgs.add(tf);
                    }
                }

                chat.setItems(chatMsgs);
                reply = true;
                lastId = msgLi.getJsonObject(msgLi.size() - 1).getString("uuid");
                updateNotify();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }));


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            //We may want some faster refresh rate. So let the user costumize it.
            Properties prop = new Properties();
            prop.setProperty("delay", "3000");

            Settings settings = new Settings("_chatDelay", prop);
            delay = Integer.parseInt(settings.properties.getProperty("delay"));

            //Inital call of the player list
            refreshPlyList();
            plyList.setEditable(false);

            //Get last Message UUID
            JsonObject msgs = ImpCall.call("chat/fetchChat");
            JsonArray msgLi = msgs.getJsonArray("messages");

            chatMsgs = FXCollections.observableArrayList();

            chat.setEditable(false);

            lastId = msgLi.getJsonObject(0).getString("uuid");

            //Start fetching messages
            refresh.setCycleCount(Timeline.INDEFINITE);
            refresh.play();

            msgRead();

            send.setDefaultButton(true);
            chat.scrollTo(chat.getItems().size() - 1);
        } catch (Exception e) {
            new excHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    /**
     * Calles when we send our Message
     */
    public void sendMsg() {
        try {
            JsonObject stat = ImpCall.call("chat/sendMessage", String.format("SOURCE=%s&TOKEN=%s&MESSAGE=%s&MODEL=%s",
                    URLEncoder.encode("gwwChat"), URLEncoder.encode(Main.login.getToken()), URLEncoder.encode(txt.getText()), URLEncoder.encode(Main.login.getDevice())));
            if(!stat.getString("status").equalsIgnoreCase("success")) {
                TextFlow tf = new TextFlow();
                Text t1 = new Text("[!] ");
                t1.setFill(Color.RED);
                Text t2 = new Text(stat.getString("message"));
                t2.setFill(Color.DARKBLUE);

                tf.getChildren().addAll(t1, t2);
                chatMsgs.add(tf);
            } else {
                txt.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh our players
     * @throws IOException If connection to API fails
     */
    public void refreshPlyList() throws IOException {
        JsonObject plys = ImpCall.call("getServer/query/89.163.145.197/25565");

        if(plys.getInt("serverPLAYERS") != 0) {

            JsonArray plyLi = plys.getJsonArray("serverPLAYERLIST");

            names = FXCollections.observableArrayList();

            for (int i = 0; i < plyLi.size(); i++) {
                names.add(plyLi.getString(i));
            }
        }

        plyList.setItems(names);
    }

    /**
     * Does actualy call the Badge update Method.
     * @param i Set the Badge to this.
     */
    public void setNotify(String i) {
        try {
            Class c = Class.forName("com.apple.eawt.Application");
            Object retrn = c.getMethod("getApplication").invoke(null);
            c.getMethod("setDockIconBadge", java.lang.String.class).invoke(retrn, i);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {e.printStackTrace();}
    }


    /**
     * Calls a Badge update.
     */
    public void updateNotify() {
        int i = 0;
        i += unreadMsgs;
        setNotify(i == 0 ? null : Integer.toString(i));
    }

    /**
     * Marks all Chat messages as readen.
     */
    public void msgRead() {
        unreadMsgs = 0;
        updateNotify();
    }
}
