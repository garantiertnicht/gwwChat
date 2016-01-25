package de.garantiertnicht.Weichware.gwwChat;

import de.garantiertnicht.Weichware.gwwChat.GUI.GuiManager;
import de.garantiertnicht.Weichware.gwwChat.api.Login;
import de.garantiertnicht.Weichware.gwwChat.api.Settings;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * If you need explination what this does, you should go away.
 * @author garantiertnicht
 */
public class Main extends Application {
    public static Application app;
    public static Login login = null;
    public static boolean first = false;
    public static GuiManager guiManager = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            app = this;
            boolean needsLogin = true;

            //Try to use Autologin, if the user said so
            try {
                Settings settings = new Settings("_login");
                if(!settings.properties.getProperty("pin").equalsIgnoreCase("0")) {
                    login = new Login(settings.properties.getProperty("pin"), "gwwChat");
                    needsLogin = false;
                }
            } catch (IOException e) {
                new excHandler().uncaughtException(Thread.currentThread(), e);
                return;
            } catch (Login.LoginFailedException | Settings.NoDefaultsException e) {
                first = true;
            }

            guiManager = new GuiManager(primaryStage);

            //Sets the GUI to Login if a Login is needed, otherwise start to main.
            if(needsLogin)
                guiManager.changeGui("Login", "Anmelden", false);
            else
                guiManager.changeGui("Main", "gwwChat", true);
        } catch (Exception throwable) {
            primaryStage.close();
            new excHandler().uncaughtException(Thread.currentThread(), throwable);
        }
    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }
}