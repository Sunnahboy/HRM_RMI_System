package com.hrmrmi.client;

import com.hrmrmi.client.gui.HRGUI;
import com.hrmrmi.client.gui.LoginGUI;
import javafx.application.Application;

public class HRClient {
    public static void main(String[] args) {
        // This command tells Java: "Start the graphical interface defined in HRGUI"
        Application.launch(LoginGUI.class, args);
    }
}