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