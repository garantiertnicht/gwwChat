package de.garantiertnicht.Weichware.gwwChat.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class is used to manage the GUI-Stuff one line easier.<br/>
 * I tend to do a over complicated solution, if I can remove one line of code…
 * @author garantiertnicht
 */
public class GuiManager {
    public Stage stage = null;

    public GuiManager(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
    }

    public void changeGui(String newGui, String title, boolean resizable) throws IOException {
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource(String.format("/de/garantiertnicht/Weichware/gwwChat/GUI/%s.fxml", newGui)));

        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.setResizable(resizable);
        stage.show();
    }
}
