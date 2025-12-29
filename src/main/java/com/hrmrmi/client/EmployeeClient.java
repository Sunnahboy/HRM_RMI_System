
package com.hrmrmi.client;

import com.hrmrmi.client.gui.EmployeeGUI;
import javafx.application.Application;
/**
 * EmployeeClient serves as the entry point for the HRM employee client application.
 * It launches the JavaFX-based user interface that allows employees to interact
 * with the HRM system through secure RMI communication.
 */

public class EmployeeClient {

    public static void main(String[] args) {

        // Launch Employee JavaFX application
        Application.launch(EmployeeGUI.class, args);
    }
}

