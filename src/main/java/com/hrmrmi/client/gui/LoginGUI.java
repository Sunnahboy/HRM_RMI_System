package com.hrmrmi.client.gui;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee; // Import the model
import com.hrmrmi.common.util.Config;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.rmi.Naming;

public class LoginGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40px;");

        Label title = new Label("HR Management System");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // DYNAMIC INPUT: User types their email here
        TextField emailField = new TextField();
        emailField.setPromptText("Email (e.g. admin@bhel.com)");

        // DYNAMIC INPUT: User types their password here
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button btnLogin = new Button("Login");

        // LOGIN LOGIC
        btnLogin.setOnAction(e -> {
            String email = emailField.getText();
            String pass = passField.getText();

            try {
                String url = "rmi://" + Config.RMI_HOST + ":" + Config.RMI_PORT + "/" + Config.RMI_NAME;
                HRMService service = (HRMService) Naming.lookup(url);

                // CALL SERVER: Pass the DYNAMIC email/password
                Employee user = service.login(email, pass);

                if (user != null) {
                    // SUCCESS!
                    // user object contains: {id=1, name="Admin", role="admin"...}

                    primaryStage.close(); // Close Login

                    // PASS THE USER TO THE DASHBOARD
                    // We need to update HRGUI to accept this 'user' object!
                    HRGUI dashboard = new HRGUI(user);
                    try {
                        dashboard.start(new Stage());
                    } catch (Exception ex) { ex.printStackTrace(); }

                } else {
                    new Alert(Alert.AlertType.ERROR, "Invalid Email or Password").show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Connection Error: " + ex.getMessage()).show();
            }
        });

        root.getChildren().addAll(title, emailField, passField, btnLogin);
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}