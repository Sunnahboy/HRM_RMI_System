package com.hrmrmi.client;

import com.hrmrmi.client.gui.HRGUI;
import javafx.application.Application;

public class HRClient {
    public static void main(String[] args) {
        // This command tells Java: "Start the graphical interface defined in HRGUI"
        Application.launch(HRGUI.class, args);
    }
}