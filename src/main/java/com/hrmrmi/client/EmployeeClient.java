
package com.hrmrmi.client;

import com.hrmrmi.client.gui.EmployeeGUI;
import com.hrmrmi.common.model.Employee;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * EmployeeClient serves as the entry point for the HRM employee client application.
 * It launches the JavaFX-based user interface that allows employees to interact
 * with the HRM system through secure RMI communication.
 */

//public class EmployeeClient {
//
//    public static void main(String[] args) {
//
//        // Launch Employee JavaFX application
//        Application.launch(EmployeeGUI.class, args);
//    }
//}


public class EmployeeClient {
    private Employee employee;

    public EmployeeClient(Employee employee) {
        this.employee = employee;
    }

    public void start(Stage stage) {
        // RMI setup, lookup registry, etc
        EmployeeGUI gui = new EmployeeGUI(employee);
        gui.show(stage);
    }
}


