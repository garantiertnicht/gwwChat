package de.garantiertnicht.Weichware.gwwChat.GUI;

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
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;

public class MainCon implements Initializable {
    @FXML private ListView<String> plyList;
    @FXML private ListView<TextFlow> chat;
    @FXML private TextField txt;
    @FXML private Button send;

    ObservableList<TextFlow> chatMsgs = null;
    ObservableList<String> names = null;

    String lastId = "";

    int delay = 3000;

    Timeline refresh = new Timeline(new KeyFrame(Duration.millis(delay), new EventHandler<ActionEvent>() {


        @Override
        public void handle(ActionEvent e) {
            boolean plyRefreshed = false;

            try {
                JsonObject newMsgs = ImpCall.call(String.format("chat/fetchChatAdvanced/%s", lastId));

                JsonArray msgLi = newMsgs.getJsonArray("messages");

                if (msgLi == null)
                    return;

                for (int i = 0; i < msgLi.size(); i++) {
                    TextFlow tf = new TextFlow();
                    JsonObject msg = msgLi.getJsonObject(i);

                    if (msg.getString("source").equalsIgnoreCase("join")) {
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
                    } else if (msg.getString("source").equalsIgnoreCase("quit")) {
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

                        send.setDisable(true);
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
                    } else {
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

                lastId = msgLi.getJsonObject(msgLi.size() - 1).getString("uuid");
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }));


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            Properties prop = new Properties();
            prop.setProperty("delay", "3000");

            Settings settings = new Settings("_chatDelay", prop);
            delay = Integer.parseInt(settings.properties.getProperty("delay"));

            JsonObject plys = ImpCall.call("getServer/query/89.163.145.197/25565");

            refreshPlyList();
            plyList.setEditable(false);


            JsonObject msgs = ImpCall.call("chat/fetchChat");
            JsonArray msgLi = msgs.getJsonArray("messages");

            chatMsgs = FXCollections.observableArrayList();

            chat.setEditable(false);

            lastId = msgLi.getJsonObject(0).getString("uuid");

            refresh.setCycleCount(Timeline.INDEFINITE);
            refresh.play();

            send.setDefaultButton(true);
            chat.scrollTo(chat.getItems().size() - 1);
        } catch (Exception e) {
            new excHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

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
}
