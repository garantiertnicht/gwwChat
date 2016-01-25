package de.garantiertnicht.Weichware.gwwChat.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
