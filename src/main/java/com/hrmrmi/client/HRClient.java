package com.hrmrmi.client;

import com.hrmrmi.client.gui.HRGUI;
import com.hrmrmi.client.gui.LoginGUI;
import com.hrmrmi.common.model.Employee;
import javafx.application.Application;
import javafx.stage.Stage;

//public class HRClient {
//    public static void main(String[] args) {
//
//        // This command tells Java: "Start the graphical interface defined in HRGUI"
//        Application.launch(LoginGUI.class, args);
//    }
//}


public class HRClient {

    private Employee employee;
    public HRClient(Employee employee) {
        this.employee = employee;
    }

    public void start(Stage stage) {
        // RMI setup, stubs, security, etc
        HRGUI gui = new HRGUI(employee);
        gui.show(stage);
    }
}
