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

public class LoginCon implements Initializable {
    @FXML private Button ok;
    @FXML private TextField pin;
    @FXML private ProgressIndicator spin;
    @FXML private CheckBox autolog;

    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        UnaryOperator<TextFormatter.Change> filter;
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

        Settings settings = null;

        try {
            settings = new Settings("_login", getDefaultLoginProperties(pin.getText(), autolog.isSelected()));

            if(!settings.properties.getProperty("pin").equalsIgnoreCase("0")) {
                pin.setText(settings.properties.getProperty("pin"));
                login(new ActionEvent());
            }

            if(Main.first)
                autolog.setSelected(true);

        } catch (IOException e) {
            new excHandler().uncaughtException(Thread.currentThread(), e);
            return;
        }
    }

    public void IMPpermsPage(ActionEvent event) {
        HostServicesFactory.getInstance(Main.app).showDocument("https://imperium1871.de/?perms");
    }

    public void pinUpdate(KeyEvent event) {
        if(pin.getText().length() == 6) {
            ok.setDisable(false);
        } else
            ok.setDisable(true);
    }

    public void login(ActionEvent event) throws IOException {
        ok.setDisable(true);
        pin.setDisable(true);
        autolog.setDisable(true);
        spin.setVisible(true);

        Login login = null;

        try {
            login = new Login(pin.getText(), "gwwChat");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Anmeldung fehlgeschlagen");
            alert.setHeaderText("Keine Verbindung zum Anmeldeserver");
            alert.setContentText("Beim Verbinden zum Anmeldeserver ist ein Fehler aufgetreten. Versuche es später erneut.");
            alert.showAndWait();
        } catch (Login.LoginFailedException e) {
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
            ok.setDisable(false);
            pin.setDisable(false);
            autolog.setDisable(false);
            spin.setVisible(false);

            return;
        }

        Main.login = login;

        Settings settings = new Settings("_login", getDefaultLoginProperties(pin.getText(), autolog.isSelected()));

        if(autolog.isSelected() && !settings.properties.get("pin").equals(pin)) {
            settings.properties.setProperty("pin", pin.getText());
            settings.save();
        }

        System.out.println(String.format("Angemeldet als %s mit Token %s, gülltig bis %s.", login.getName(), login.getToken(), login.getExpier()));
        Main.guiManager.changeGui("Main", "gwwChat", true);
    }

    public Properties getDefaultLoginProperties(String pin, boolean auto) {
        Properties def = new Properties();
        def.setProperty("pin", (auto ? pin : "0"));
        def.setProperty("_login", "1");

        return def;
    }
}
