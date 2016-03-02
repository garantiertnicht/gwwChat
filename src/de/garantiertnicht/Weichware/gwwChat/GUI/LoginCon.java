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

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import de.garantiertnicht.Weichware.gwwChat.Main;
import de.garantiertnicht.Weichware.gwwChat.api.Login;
import de.garantiertnicht.Weichware.gwwChat.api.Settings;
import de.garantiertnicht.Weichware.gwwChat.excHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.util.Properties;
import java.util.function.UnaryOperator;

/**
 * This class Manages the Login GUI.
 * @author garantiertnicht
 */
public class LoginCon implements Initializable {
    @FXML private Button ok;
    @FXML private TextField pin;
    @FXML private ProgressIndicator spin;
    @FXML private CheckBox autolog;

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> filter;

        //Filter, wish only allow "valid" Pins to be entered.
        filter = change -> {
            String text = change.getText();

            if(change.isAdded() && change.getControlNewText().length() > 6)
                return null;

            for (int i = 0; i < text.length(); i++)
                if (!Character.isDigit(text.charAt(i)))
                    return null;
            return change;
        };

        pin.setTextFormatter(new TextFormatter<String>(filter));


        //If this is the first start, we sugesst to remember the code.
        //If the User don't wants this, we will not turn on the Checkbox.
        if(Main.first)
            autolog.setSelected(true);
    }

    /**
     * Makes the Link to the Imperium Permission Page clickable.
     * @param event Advanced JavaFX stuff
     */
    public void IMPpermsPage(ActionEvent event) {
        HostServicesFactory.getInstance(Main.app).showDocument("https://imperium1871.de/?perms");
    }

    /**
     * Only let the user click ok if the pin can be valid.
     * @param event Advanced JavaFX stuff
     */
    public void pinUpdate(KeyEvent event) {
        if(pin.getText().length() == 6)
            ok.setDisable(false);
        else
            ok.setDisable(true);
    }

    /**
     * Does the Login.
     * @param event Advanced JavaFX stuff
     * @throws IOException
     */
    public void login(ActionEvent event) throws IOException {
        ok.setDisable(true);
        pin.setDisable(true);
        autolog.setDisable(true);
        spin.setVisible(true);

        Login login = null;

        try {
            login = new Login(pin.getText(), "gwwChat");
        } catch (IOException e) {
            //We could not connect.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Anmeldung fehlgeschlagen");
            alert.setHeaderText("Keine Verbindung zum Anmeldeserver");
            alert.setContentText("Beim Verbinden zum Anmeldeserver ist ein Fehler aufgetreten. Versuche es später erneut.");
            alert.showAndWait();
        } catch (Login.LoginFailedException e) {
            //The server had reasons to reject our Pin.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Anmeldung fehlgeschlagen");
            alert.setHeaderText("Anmeldedaten zurrückgewiesen");
            alert.setContentText("Der Angegebene PIN ist entweder Falsch oder bereits abgelaufen.");
            alert.showAndWait();
        } catch (Exception e) {
            new excHandler().uncaughtException(Thread.currentThread(), e);
            return;
        }

        if(login == null) {
            //Login failed. Try again, user!
            ok.setDisable(false);
            pin.setDisable(false);
            autolog.setDisable(false);
            spin.setVisible(false);

            return;
        }

        //Login successfull! Remember it.
        Main.login = login;

        //Save Autologin
        Settings settings = new Settings("_login", getDefaultLoginProperties(pin.getText(), autolog.isSelected()));

        if(autolog.isSelected() && !settings.properties.get("pin").equals(pin)) {
            settings.properties.setProperty("pin", pin.getText());
            settings.save();
        }

        System.out.println(String.format("Angemeldet als %s mit Token %s, gülltig bis %s.", login.getName(), login.getToken(), login.getExpier()));
        Main.guiManager.changeGui("Main", "gwwChat", true);
    }

    /**
     * Gets default properties for autologin.
     * @param pin Pin for default
     * @param auto Do Autologin
     * @return Default properties
     */
    public Properties getDefaultLoginProperties(String pin, boolean auto) {
        Properties def = new Properties();
        def.setProperty("pin", (auto ? pin : "0"));
        def.setProperty("_login", "1");

        return def;
    }
}
