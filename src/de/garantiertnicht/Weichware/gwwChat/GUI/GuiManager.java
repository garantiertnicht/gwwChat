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
