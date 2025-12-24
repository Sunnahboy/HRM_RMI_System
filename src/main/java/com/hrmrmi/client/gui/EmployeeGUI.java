package com.hrmrmi.client.gui;
import com.hrmrmi.client.controller.EmployeeController;
import com.hrmrmi.client.gui.components.LoginPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class EmployeeGUI extends Application {
    private EmployeeController controller;

    @Override
    public void start(Stage stage) {
        controller = new EmployeeController();

        LoginPanel loginPanel = new LoginPanel();

        BorderPane root = new BorderPane();
        root.setCenter(loginPanel);

        // ---- Login button action ----
        loginPanel.getLoginButton().setOnAction(event -> {
            loginPanel.clearStatus();

            String username = loginPanel.getUsername();
            String password = loginPanel.getPassword();

            if (username.isEmpty() || password.isEmpty()) {
                loginPanel.setStatusMessage("Please enter username and password");
                return;
            }

            boolean success = controller.login(username, password);

            if (success) {
                showDashboard(stage);
            } else {
                loginPanel.setStatusMessage("Invalid username or password");
            }
        });

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Employee Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Temporary dashboard placeholder.
     * We will expand this later.
     */
    private void showDashboard(Stage stage) {
        BorderPane dashboard = new BorderPane();
        dashboard.setCenter(new Label("Employee Dashboard (Login Successful)"));

        Scene dashboardScene = new Scene(dashboard, 500, 350);
        stage.setScene(dashboardScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
