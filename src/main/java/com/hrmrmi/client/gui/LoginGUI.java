package com.hrmrmi.client.gui;

import com.hrmrmi.client.EmployeeClient;
import com.hrmrmi.client.HRClient;
import com.hrmrmi.client.controller.EmployeeController;
import com.hrmrmi.client.controller.HRController;
import com.hrmrmi.common.model.Employee;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginGUI extends Application {

    // Design Constants
    private static final String PRIMARY_COLOR = "#2E7D32";
    private static final String SECONDARY_COLOR = "#1976D2";
    private static final String SUCCESS_COLOR = "#43A047";
    private static final String ERROR_COLOR = "#E53935";
    private static final String BACKGROUND_COLOR = "#F5F5F5";
    private static final String CARD_BG = "#FFFFFF";

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Main Container
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(60));
        mainBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Logo/Title Container
        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 30, 0));

        Label title = new Label("🏢 BHEL Employee System");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setTextFill(javafx.scene.paint.Color.web(PRIMARY_COLOR));

        Label subtitle = new Label("Employee Profile Management Portal");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(javafx.scene.paint.Color.web("#888888"));

        titleBox.getChildren().addAll(title, subtitle);

        // Form Container (Card)
        VBox formBox = new VBox(16);
        formBox.setPadding(new Insets(50));
        formBox.setStyle("-fx-background-color: " + CARD_BG + "; "
                + "-fx-border-color: #E0E0E0; -fx-border-radius: 12; "
                + "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4);");
        formBox.setMaxWidth(380);

        // Email Label
        Label emailLabel = createLabel("Email Address");

        // Email Field
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle(getTextFieldStyle());
        emailField.setPrefHeight(45);

        // Password Label
        Label passLabel = createLabel("Password");

        // Password Field
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setStyle(getTextFieldStyle());
        passField.setPrefHeight(45);

        // Login Button
        Button btnLogin = new Button("🔐 Sign In");
        btnLogin.setStyle(getPrimaryButtonStyle());
        btnLogin.setPrefWidth(250);
        btnLogin.setPrefHeight(45);
        btnLogin.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Error Message Label
        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
        msgLabel.setWrapText(true);

        // Login Logic
        btnLogin.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass = passField.getText();

            if (email.isEmpty()) {
                msgLabel.setText("✗ Email is required");
                msgLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                return;
            }

            if (pass.isEmpty()) {
                msgLabel.setText("✗ Password is required");
                msgLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                return;
            }

            btnLogin.setDisable(true);
            btnLogin.setText("Signing in...");

            try {
                HRController controller = new HRController();
                boolean ok = controller.login(email, pass);

                if (ok) {
                    Employee user = controller.getLoggedIn();

                    if (user != null) {
                        msgLabel.setText("✓ Login successful! Redirecting...");
                        msgLabel.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");

                        if (user.getRole().equals("HR")) {
                            HRClient hrClient = new HRClient(user);
                            hrClient.start(primaryStage);
                        } else {
                            EmployeeClient empClient = new EmployeeClient(user);
                            empClient.start(primaryStage);
                        }

                    } else {
                        msgLabel.setText("✗ User profile not found");
                        btnLogin.setDisable(false);
                        btnLogin.setText("🔐 Sign In");
                    }
                } else {
                    msgLabel.setText("✗ Invalid email or password");
                    passField.clear();
                    btnLogin.setDisable(false);
                    btnLogin.setText("🔐 Sign In");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                msgLabel.setText("✗ Connection Error: " + ex.getMessage());
                btnLogin.setDisable(false);
                btnLogin.setText("🔐 Sign In");
            }
        });

        VBox spacer = new VBox();
        spacer.setPrefHeight(10);

        formBox.getChildren().addAll(
                emailLabel,
                emailField,
                passLabel,
                passField,
                spacer,
                btnLogin,
                msgLabel
        );

        mainBox.getChildren().addAll(titleBox, formBox);

        Scene scene = new Scene(mainBox, 1000, 800);
        primaryStage.setTitle("HR Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // ============ UTILITY METHODS ============

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        label.setTextFill(javafx.scene.paint.Color.web("#333333"));
        return label;
    }

    private String getPrimaryButtonStyle() {
        return "-fx-padding: 12 24 12 24; "
                + "-fx-font-size: 13; "
                + "-fx-font-weight: bold; "
                + "-fx-background-color: " + PRIMARY_COLOR + "; "
                + "-fx-text-fill: white; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);";
    }

    private String getTextFieldStyle() {
        return "-fx-padding: 10 15 10 15; "
                + "-fx-border-color: #E0E0E0; "
                + "-fx-border-width: 1.5; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-font-size: 12; "
                + "-fx-focus-color: " + SECONDARY_COLOR + "; "
                + "-fx-faint-focus-color: rgba(25, 118, 210, 0.1); "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 2, 0, 0, 1);";
    }
}
