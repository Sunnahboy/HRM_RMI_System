package com.hrmrmi.client.gui;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.util.Config;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.rmi.Naming;


public class HRGUI extends Application {
    private HRMService service;

    @Override
    public void start(Stage primaryStage) {
        //server connection
        try {
            // look on 'localhost' at the port defined in Config
            String url = "rmi://localhost:" + Config.RMI_PORT + "/" + Config.RMI_NAME;
            service = (HRMService) Naming.lookup(url);
        } catch (Exception e) {
            // show an error if fail
            new Alert(Alert.AlertType.ERROR, "Server Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }


        TabPane tabPane = new TabPane();

        // Register
        VBox registerBox = new VBox(10);
        registerBox.setStyle("-fx-padding: 20px;");

        TextField fName = new TextField(); fName.setPromptText("First Name");
        TextField lName = new TextField(); lName.setPromptText("Last Name");
        TextField icNum = new TextField(); icNum.setPromptText("Passport Number");
        TextField deptField = new TextField(); deptField.setPromptText("Department (e.g. HR, IT)");
        TextField posField = new TextField(); posField.setPromptText("Position (e.g. Manager)");

        Button btnRegister = new Button("Register Employee");

        btnRegister.setOnAction(e -> {
            try {
                // Call the Server (which calls Repository -> Database)
                boolean success = service.registerEmployees(fName.getText(), lName.getText(), icNum.getText(), deptField.getText(), posField.getText());

                if(success) {
                    new Alert(Alert.AlertType.INFORMATION, "Success! Employee Registered.").show();

                    fName.clear(); lName.clear(); icNum.clear();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to register.").show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "System Error: " + ex.getMessage()).show();
            }
        });

        registerBox.getChildren().addAll(new Label("New Employee Registration"), fName, lName, icNum, deptField, posField, btnRegister);

        Tab tab1 = new Tab("Registration", registerBox);
        tab1.setClosable(false);

        tabPane.getTabs().add(tab1);


        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setTitle("HR Staff Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}