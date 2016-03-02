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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Adds a super-fancy Exception dialog.
 * I know, this isn't best practise!
 *
 * @author garantiertnicht
 */
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