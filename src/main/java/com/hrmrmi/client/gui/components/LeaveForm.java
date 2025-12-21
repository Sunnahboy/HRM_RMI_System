package com.hrmrmi.client.gui.components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class LeaveForm extends VBox {
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final TextArea reasonArea;
    private final Button submitButton;
    private final Label statusLabel;

    public LeaveForm() {
        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Apply Leave");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        reasonArea = new TextArea();
        reasonArea.setPromptText("Reason");
        reasonArea.setPrefRowCount(3);

        submitButton = new Button("Submit Leave");
        submitButton.setPrefWidth(200);

        statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);

        getChildren().addAll(
                title,
                new Label("Start Date"),
                startDatePicker,
                new Label("End Date"),
                endDatePicker,
                new Label("Reason"),
                reasonArea,
                submitButton,
                statusLabel
        );
    }

    // ----------------------
    // Exposed getters
    // ----------------------

    public LocalDate getStartDate() {
        return startDatePicker.getValue();
    }

    public LocalDate getEndDate() {
        return endDatePicker.getValue();
    }

    public String getReason() {
        return reasonArea.getText().trim();
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    public void clearStatus() {
        statusLabel.setText("");
    }

    public void clearForm() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reasonArea.clear();
        clearStatus();
    }
}
