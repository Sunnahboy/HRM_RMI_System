package com.hrmrmi.client.gui.components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.*;


public class LoginPanel extends VBox {
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label statusLabel;

    public LoginPanel() {
        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        loginButton = new Button("Login");
        loginButton.setPrefWidth(200);

        statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);

        getChildren().addAll(
                titleLabel,
                usernameField,
                passwordField,
                loginButton,
                statusLabel
        );
    }

    // ----------------------
    // Exposed methods
    // ----------------------

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    public void clearStatus() {
        statusLabel.setText("");
    }
}
