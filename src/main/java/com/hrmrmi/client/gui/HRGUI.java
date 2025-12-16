package com.hrmrmi.client.gui;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee; // Import the model
import com.hrmrmi.common.util.Config;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.rmi.Naming;

public class HRGUI extends Application {
    private HRMService service;
    private Employee currentUser; // Store the logged-in user

    // DEFAULT CONSTRUCTOR (Required by JavaFX, but we won't use it directly)
    public HRGUI() {
        this.currentUser = null;
    }

    // --- CUSTOM CONSTRUCTOR ---
    // This is the one LoginGUI calls!
    public HRGUI(Employee user) {
        this.currentUser = user;
    }

    @Override
    public void start(Stage primaryStage) {
        // 1. CONNECT TO SERVER
        try {
            String url = "rmi://" + Config.RMI_HOST + ":" + Config.RMI_PORT + "/" + Config.RMI_NAME;
            service = (HRMService) Naming.lookup(url);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Server Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }

        // 2. SETUP LAYOUT
        TabPane tabPane = new TabPane();

        // --- TAB A: PROFILE (Everyone sees this) ---
        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome, " + currentUser.getFirstName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label detailsLabel = new Label(
                "ID: " + currentUser.getId() + "\n" +
                        "Role: " + currentUser.getRole() + "\n" +
                        "Department: " + currentUser.getDepartment() + "\n" +
                        "Email: " + currentUser.getEmail()
        );

        profileBox.getChildren().addAll(welcomeLabel, detailsLabel);
        Tab profileTab = new Tab("My Profile", profileBox);
        profileTab.setClosable(false);
        tabPane.getTabs().add(profileTab);


        // --- TAB B: REGISTRATION (Only for ADMIN or HR) ---
        // We check the role string (ignoring case so "Admin" and "admin" both work)
        if (currentUser.getRole().equalsIgnoreCase("admin") ||
                currentUser.getDepartment().equalsIgnoreCase("HR")) {

            VBox registerBox = new VBox(10);
            registerBox.setPadding(new Insets(20));

            TextField fName = new TextField(); fName.setPromptText("First Name");
            TextField lName = new TextField(); lName.setPromptText("Last Name");
            TextField icNum = new TextField(); icNum.setPromptText("Passport Number");
            TextField deptField = new TextField(); deptField.setPromptText("Department");
            TextField posField = new TextField(); posField.setPromptText("Position");

            Button btnRegister = new Button("Register New Employee");

            btnRegister.setOnAction(e -> {
                try {
                    boolean success = service.registerEmployees(
                            fName.getText(), lName.getText(), icNum.getText(),
                            deptField.getText(), posField.getText()
                    );
                    if(success) {
                        new Alert(Alert.AlertType.INFORMATION, "Success! Employee Registered.").show();
                        fName.clear(); lName.clear(); icNum.clear();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to register.").show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
                }
            });

            registerBox.getChildren().addAll(
                    new Label("Register New Employee"),
                    fName, lName, icNum, deptField, posField, btnRegister
            );

            Tab adminTab = new Tab("Registration (Admin)", registerBox);
            adminTab.setClosable(false);
            tabPane.getTabs().add(adminTab);
        }

        // 3. SHOW SCENE
        Scene scene = new Scene(tabPane, 600, 450);
        primaryStage.setTitle("HRM System - " + currentUser.getFirstName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}