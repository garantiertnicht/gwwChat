package de.garantiertnicht.Weichware.gwwChat;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class excHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable exc) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Es ist ein (un)erwarteter Fehler aufgetreten.");
            alert.setHeaderText(exc.getClass().getCanonicalName() + " in " + t);
            alert.setContentText("Bitte senden Sie den folgenden Fehlerbericht den Entwickler:");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exc.printStackTrace(pw);
            String exceptionText = sw.toString();


            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);
            alert.getButtonTypes().setAll(new ButtonType("Beenden", ButtonBar.ButtonData.FINISH));
            alert.showAndWait();

            System.exit(1);
        } catch (Exception throwable) {
            throwable.printStackTrace();
        }
    }
}