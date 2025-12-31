package com.hrmrmi.client.gui;
import com.hrmrmi.client.controller.EmployeeController;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.Leave;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * EmployeeGUI provides the JavaFX-based graphical user interface for employee users.
 * It handles authentication, profile management, leave applications, leave status
 * tracking, and emergency contact management by interacting with the
 * EmployeeController and remote HRM services.
 */


@SuppressWarnings({"unused","CallToPrintStackTrace"})
public class EmployeeGUI {//extends Application {

    private EmployeeController controller;
    private Employee loggedIn;

    // Instance variables for header updates
    private Label headerName;
    private Label headerBalance;

    // Instance variable for table refresh
    private TableView<Leave> leaveTable;

    // Design constants
    private static final String PRIMARY_COLOR = "#2E7D32";
    private static final String SECONDARY_COLOR = "#1976D2";
    private static final String SUCCESS_COLOR = "#43A047";
    private static final String ERROR_COLOR = "#E53935";
    private static final String WARNING_COLOR = "#FB8C00";
    private static final String BACKGROUND_COLOR = "#F5F5F5";
    private static final String CARD_BG = "#FFFFFF";

    public EmployeeGUI() { this.loggedIn = null; }
    public EmployeeGUI(Employee user) { this.loggedIn = user; }

    public void show(Stage stage) {
        try {
            controller = new EmployeeController();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }

        // SAFETY CHECK: If no user passed (e.g. run directly), go to Login
        if (this.loggedIn == null) {
            try {
                new LoginGUI().start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // Initialize Controller with User and Show Dashboard
        controller.setLoggedInEmployee(this.loggedIn);
        showDashboard(stage);
    }

    private void showDashboard(Stage stage) {
        TabPane tabs = new TabPane(
                profileTab(),
                applyLeaveTab(),
                leaveStatusTab(),
                emergencyContactTab()
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-font-size: 12; -fx-padding: 15;");

        BorderPane root = new BorderPane();
        root.setTop(header());
        root.setCenter(tabs);
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("Employee Dashboard");
        stage.centerOnScreen();
    }

    private HBox header() {
        headerName = new Label("Welcome, " + loggedIn.getFirstName() + " " + loggedIn.getLastName());
        headerName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        headerName.setTextFill(javafx.scene.paint.Color.web(TEXT_COLOR()));

        headerBalance = new Label("📊 Leave Balance: " + loggedIn.getLeaveBalance() + " days");
        headerBalance.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        headerBalance.setTextFill(javafx.scene.paint.Color.web(PRIMARY_COLOR));

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle(getOutlineButtonStyle());

        // LOGOUT LOGIC: Close this window -> Open LoginGUI
        logoutBtn.setOnAction(e -> {
            // Close the current Employee Dashboard
            ((Stage) logoutBtn.getScene().getWindow()).close();

            // Open the Login Screen
            try {
                new LoginGUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox box = new HBox(25, headerName, headerBalance, spacer, logoutBtn);
        box.setPadding(new Insets(20, 30, 20, 30));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: " + CARD_BG + "; "
                + "-fx-border-color: #E0E0E0; -fx-border-width: 0 0 2 0; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 2);");
        return box;
    }

    // Update header with fresh data
    private void updateHeader() {
        if (headerName != null && headerBalance != null) {
            headerName.setText("Welcome, " + loggedIn.getFirstName() + " " + loggedIn.getLastName());
            headerBalance.setText("📊 Leave Balance: " + loggedIn.getLeaveBalance() + " days");
        }
    }

    /* ================= PROFILE ================= */

    private Tab profileTab() {
        TextField firstName = new TextField(loggedIn.getFirstName());
        firstName.setStyle(getTextFieldStyle());
        firstName.setPrefHeight(40);

        TextField lastName = new TextField(loggedIn.getLastName());
        lastName.setStyle(getTextFieldStyle());
        lastName.setPrefHeight(40);

        String currentPhone = loggedIn.getPhoneNumber();
        TextField phone = new TextField(loggedIn.getPhoneNumber());
        phone.setStyle(getTextFieldStyle());
        phone.setPrefHeight(40);

        TextField email = new TextField(loggedIn.getEmail());
        email.setStyle(getTextFieldStyle());
        email.setPrefHeight(40);
        email.setDisable(true);

        TextField dept = new TextField(loggedIn.getDepartment());
        dept.setStyle(getTextFieldStyle());
        dept.setPrefHeight(40);
        dept.setDisable(true);

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
        msg.setWrapText(true);

        Button refresh = new Button("🔄 Refresh");
        refresh.setStyle(getSecondaryButtonStyle());
        refresh.setPrefWidth(140);
        refresh.setPrefHeight(40);

        Button save = new Button("💾 Update Profile");
        save.setStyle(getPrimaryButtonStyle());
        save.setPrefWidth(160);
        save.setPrefHeight(40);

        refresh.setOnAction(e -> {
            refresh.setDisable(true);
            refresh.setText("Refreshing...");

            Employee fresh = controller.viewProfile();
            if (fresh != null) {
                loggedIn = fresh;
                firstName.setText(fresh.getFirstName());
                lastName.setText(fresh.getLastName());
                phone.setText(fresh.getPhoneNumber());
                email.setText(fresh.getEmail());
                dept.setText(fresh.getDepartment());

                updateHeader();
                msg.setText("✓ Profile refreshed successfully");
                msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
            } else {
                msg.setText("✗ Failed to refresh profile");
                msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            }
            refresh.setDisable(false);
            refresh.setText("🔄 Refresh");
        });

        save.setOnAction(e -> {
            if (validateProfileInputs(firstName, lastName, msg)) {
                save.setDisable(true);
                save.setText("Updating...");

                loggedIn.setFirstName(firstName.getText().trim());
                loggedIn.setLastName(lastName.getText().trim());
                loggedIn.setPhoneNumber(phone.getText());

                boolean ok = controller.updateProfile(loggedIn);

                if (ok) {
                    Employee fresh = controller.viewProfile();
                    if (fresh != null) {
                        loggedIn = fresh;
                        firstName.setText(fresh.getFirstName());
                        lastName.setText(fresh.getLastName());
                        phone.setText(fresh.getPhoneNumber());
                        email.setText(fresh.getEmail());
                        dept.setText(fresh.getDepartment());

                        updateHeader();
                        msg.setText("✓ Profile updated successfully");
                        msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
                    }
                } else {
                    msg.setText("✗ Update failed");
                    msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                }
                save.setDisable(false);
                save.setText("💾 Update Profile");
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(16);
        grid.setHgap(16);
        grid.setPadding(new Insets(30));
        grid.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                + "-fx-border-radius: 12; -fx-background-radius: 12;");
        grid.setMaxWidth(600);

        grid.addRow(0, createLabel("First Name"), firstName);
        grid.addRow(1, createLabel("Last Name"), lastName);
        grid.addRow(2, createLabel("Phone Number"), phone);
        grid.addRow(3, createLabel("Email Address"), email);
        grid.addRow(4, createLabel("Department"), dept);

        HBox buttonBox = new HBox(12, save, refresh);
        grid.add(buttonBox, 1, 4);
        grid.add(msg, 0, 5);
        GridPane.setColumnSpan(msg, 2);

        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.getChildren().addAll(grid);
        container.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        return new Tab("👤 My Profile", container);
    }

    /* ================= APPLY LEAVE ================= */

    private Tab applyLeaveTab() {
        DatePicker start = new DatePicker();
        start.setStyle(getTextFieldStyle());
        start.setPrefHeight(40);

        DatePicker end = new DatePicker();
        end.setStyle(getTextFieldStyle());
        end.setPrefHeight(40);

        TextArea reason = new TextArea();
        reason.setStyle(getTextAreaStyle());
        reason.setWrapText(true);
        reason.setPrefRowCount(5);
        reason.setFont(Font.font("Segoe UI", 11));

        Button submit = new Button("📤 Submit Leave Request");
        submit.setStyle(getPrimaryButtonStyle());
        submit.setPrefWidth(200);
        submit.setPrefHeight(45);

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
        msg.setWrapText(true);

        submit.setOnAction(e -> {
            if (validateLeaveInputs(start, end, reason, msg)) {
                submit.setDisable(true);
                submit.setText("Submitting...");

                Leave leave = new Leave();
                leave.setStartDate(java.sql.Date.valueOf(start.getValue()));
                leave.setEndDate(java.sql.Date.valueOf(end.getValue()));
                leave.setReason(reason.getText().trim());
                leave.setStatus("PENDING");
                leave.setEmployeeId(loggedIn.getId());

                boolean ok = controller.applyLeave(leave);

                if (ok) {
                    System.out.println("⏳ Waiting 500ms for database commit...");
                    try { Thread.sleep(500); } catch (InterruptedException ex) { ex.printStackTrace(); }

                    msg.setText("✓ Leave request submitted successfully");
                    msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");

                    Employee fresh = controller.viewProfile();
                    if (fresh != null) {
                        loggedIn = fresh;
                        updateHeader();
                    }

                    if (leaveTable != null) {
                        System.out.println("📊 Refreshing table...");
                        leaveTable.getItems().clear();

                        java.util.List<Leave> leaves = controller.getMyLeaves();
                        System.out.println("✓ Leaves retrieved from server: " + leaves.size());

                        if (leaves.isEmpty()) {
                            System.out.println("⚠️  WARNING: Empty list returned!");
                            System.out.println("   Employee ID used for query: " + loggedIn.getId());
                        } else {
                            leaves.forEach(l -> System.out.println(
                                    "   - Leave ID: " + l.getLeaveId() +
                                            " | Emp: " + l.getEmployeeId() +
                                            " | " + l.getStartDate() + " to " + l.getEndDate() +
                                            " | Status: " + l.getStatus()
                            ));
                        }

                        leaveTable.getItems().addAll(leaves);
                        System.out.println("✓ Table updated with " + leaveTable.getItems().size() + " rows");
                    }

                    start.setValue(null);
                    end.setValue(null);
                    reason.clear();
                } else {
                    msg.setText(" REJECT: Dates overlap with existing leave");
                    msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                }

                submit.setDisable(false);
                submit.setText("📤 Submit Leave Request");
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(16);
        grid.setHgap(16);
        grid.setPadding(new Insets(30));
        grid.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                + "-fx-border-radius: 12; -fx-background-radius: 12;");
        grid.setMaxWidth(600);

        grid.addRow(0, createLabel("Start Date"), start);
        grid.addRow(1, createLabel("End Date"), end);
        grid.addRow(2, createLabel("Reason for Leave"), reason);
        grid.add(submit, 1, 3);
        grid.add(msg, 0, 4);
        GridPane.setColumnSpan(msg, 2);

        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.getChildren().add(grid);
        container.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        return new Tab("📋 Apply Leave", container);
    }

    /* ================= EMERGENCY CONTACT ================= */

    private Tab emergencyContactTab() {
        TextField contactName = new TextField();
        contactName.setPromptText("Enter contact person name");
        contactName.setStyle(getTextFieldStyle());
        contactName.setPrefHeight(40);

        ComboBox<String> relationship = new ComboBox<>();
        relationship.getItems().addAll(
                "Spouse",
                "Parent",
                "Sibling",
                "Child",
                "Friend",
                "Relative",
                "Other"
        );
        relationship.setStyle(getTextFieldStyle());
        relationship.setPrefHeight(40);
        relationship.setPromptText("Select relationship");

        TextField contactNumber = new TextField();
        contactNumber.setPromptText("Enter contact number (e.g., +60123456789)");
        contactNumber.setStyle(getTextFieldStyle());
        contactNumber.setPrefHeight(40);

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
        msg.setWrapText(true);

        Button save = new Button("💾 Save Emergency Contact");
        save.setStyle(getPrimaryButtonStyle());
        save.setPrefWidth(180);
        save.setPrefHeight(40);

        Button clear = new Button("🔄 Clear");
        clear.setStyle(getSecondaryButtonStyle());
        clear.setPrefWidth(120);
        clear.setPrefHeight(40);

        // Load existing family details on tab open
        loadFamilyDetails(contactName, relationship, contactNumber);

        save.setOnAction(e -> {
            if (validateEmergencyContact(contactName, relationship, contactNumber, msg)) {
                save.setDisable(true);
                save.setText("Saving...");
                boolean ok = controller.saveFamilyDetail(
                        loggedIn.getId(),
                        contactName.getText().trim(),
                        relationship.getValue(),
                        contactNumber.getText().trim()
                );

                if (ok) {
                    msg.setText("✓ Emergency contact saved successfully");
                    msg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
                    System.out.println("✓ GUI confirmed: Save successful");
                } else {
                    msg.setText("✗ Failed to save emergency contact (check console)");
                    msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                    System.out.println("❌ GUI: Save failed");
                }

                System.out.println("╚════════════════════════════════════════╝\n");

                save.setDisable(false);
                save.setText("💾 Save Emergency Contact");
            }
        });

        clear.setOnAction(e -> {
            contactName.clear();
            relationship.setValue(null);
            contactNumber.clear();
            msg.setText("");
        });

        GridPane grid = new GridPane();
        grid.setVgap(16);
        grid.setHgap(16);
        grid.setPadding(new Insets(30));
        grid.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                + "-fx-border-radius: 12; -fx-background-radius: 12;");
        grid.setMaxWidth(600);

        grid.addRow(0, createLabel("Contact Name"), contactName);
        grid.addRow(1, createLabel("Relationship"), relationship);
        grid.addRow(2, createLabel("Contact Number"), contactNumber);

        HBox buttonBox = new HBox(12, save, clear);
        grid.add(buttonBox, 1, 3);
        grid.add(msg, 0, 4);
        GridPane.setColumnSpan(msg, 2);

        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.getChildren().addAll(grid);
        container.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        return new Tab("🚨 Emergency Contact", container);
    }

    // Load existing family details from server
    private void loadFamilyDetails(TextField name, ComboBox<String> relationship, TextField number) {
        System.out.println("\n Loading family details for Employee ID: " + loggedIn.getId());

        java.util.List<com.hrmrmi.common.model.FamilyDetails> details = controller.getFamilyDetails();

        if (!details.isEmpty()) {
            // Load the first family detail (or you can show a list)
            com.hrmrmi.common.model.FamilyDetails detail = details.getFirst();
            name.setText(detail.getName());
            relationship.setValue(detail.getRelationship());
            number.setText(detail.getContact());

            System.out.println("✓ Loaded: " + detail.getName() + " (" + detail.getRelationship() + ")\n");
        } else {
            System.out.println(" No family details found\n");
        }
    }

    private Tab leaveStatusTab() {
        leaveTable = new TableView<>();
        leaveTable.setStyle("-fx-font-size: 11;");

        TableColumn<Leave, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStartDate().toString())
        );
        startCol.setPrefWidth(120);

        TableColumn<Leave, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getEndDate().toString())
        );
        endCol.setPrefWidth(120);

        TableColumn<Leave, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getReason())
        );
        reasonCol.setPrefWidth(250);

        TableColumn<Leave, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus())
        );
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new StatusCell());

        leaveTable.getColumns().addAll(startCol, endCol, reasonCol, statusCol);

        // DEBUG: Show which employee's leaves are being loaded
        System.out.println("\nLoading Leave Status Tab");
        System.out.println("   Logged-in Employee ID: " + loggedIn.getId());
        System.out.println("   Name: " + loggedIn.getFirstName() + " " + loggedIn.getLastName());

        List<Leave> initialLeaves = controller.getMyLeaves();
        System.out.println("   Leaves found: " + initialLeaves.size());
        initialLeaves.forEach(l -> System.out.println(
                "     - " + l.getStartDate() + " to " + l.getEndDate() + " (" + l.getStatus() + ")"
        ));

        leaveTable.getItems().addAll(initialLeaves);

        Button refresh = new Button("🔄 Refresh Requests");
        refresh.setStyle(getSecondaryButtonStyle());
        refresh.setPrefWidth(160);
        refresh.setPrefHeight(38);
        refresh.setOnAction(e -> {
            refresh.setDisable(true);
            System.out.println("\n🔄 Manual refresh clicked");
            System.out.println("   Employee ID: " + loggedIn.getId());

            leaveTable.getItems().clear();
            java.util.List<Leave> leaves = controller.getMyLeaves();

            System.out.println("   Leaves found: " + leaves.size());
            leaves.forEach(l -> System.out.println(
                    "     - " + l.getStartDate() + " to " + l.getEndDate() + " (" + l.getStatus() + ")"
            ));

            leaveTable.getItems().addAll(leaves);

            Employee freshProfile = controller.viewProfile();
            if(freshProfile != null) {
                loggedIn = freshProfile;
                updateHeader();
                System.out.println("Header updated. New Balance: " + loggedIn.getLeaveBalance());
            }
            refresh.setDisable(false);
        });

        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                + "-fx-border-radius: 12; -fx-background-radius: 12;");
        box.getChildren().addAll(
                createLabel("Your Leave Requests"),
                leaveTable,
                refresh
        );

        VBox container = new VBox();
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        container.getChildren().add(box);

        return new Tab("📅 Leave Status", container);
    }

    /* ================= CUSTOM CELL FOR STATUS ================= */

    private static class StatusCell extends TableCell<Leave, String> {
        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setText(null);
                setStyle("");
            } else {
                setText(status);
                switch (status) {
                    case "APPROVED":
                        setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-weight: bold;");
                        break;
                    case "REJECTED":
                        setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-weight: bold;");
                        break;
                    case "PENDING":
                        setStyle("-fx-text-fill: " + WARNING_COLOR + "; -fx-font-weight: bold;");
                        break;
                }
            }
        }
    }

    /* ================= UTILITY METHODS ================= */

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

    private String getSecondaryButtonStyle() {
        return "-fx-padding: 10 20 10 20; "
                + "-fx-font-size: 12; "
                + "-fx-background-color: white; "
                + "-fx-text-fill: " + SECONDARY_COLOR + "; "
                + "-fx-border-color: " + SECONDARY_COLOR + "; "
                + "-fx-border-width: 1.5; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-cursor: hand;";
    }

    private String getOutlineButtonStyle() {
        return "-fx-padding: 10 20 10 20; "
                + "-fx-font-size: 12; "
                + "-fx-background-color: transparent; "
                + "-fx-text-fill: " + PRIMARY_COLOR + "; "
                + "-fx-border-color: " + PRIMARY_COLOR + "; "
                + "-fx-border-width: 1.5; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-cursor: hand;";
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

    private String getTextAreaStyle() {
        return "-fx-padding: 12; "
                + "-fx-border-color: #E0E0E0; "
                + "-fx-border-width: 1.5; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-font-size: 11; "
                + "-fx-focus-color: " + SECONDARY_COLOR + "; "
                + "-fx-faint-focus-color: rgba(25, 118, 210, 0.1);";
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        label.setTextFill(javafx.scene.paint.Color.web("#333333"));
        return label;
    }

    private Region createSpacer(int height) {
        Region spacer = new Region();
        spacer.setPrefHeight(height);
        return spacer;
    }

    private String TEXT_COLOR() {
        return "#212121";
    }

    private boolean validateLoginInputs(TextField email, PasswordField password, Label msg) {
        if (email.getText().trim().isEmpty()) {
            msg.setText("✗ Email is required");
            return false;
        }
        if (password.getText().isEmpty()) {
            msg.setText("✗ Password is required");
            return false;
        }
        return true;
    }

    private boolean validateProfileInputs(TextField firstName, TextField lastName, Label msg) {
        if (firstName.getText().trim().isEmpty()) {
            msg.setText("✗ First name cannot be empty");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        if (lastName.getText().trim().isEmpty()) {
            msg.setText("✗ Last name cannot be empty");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        return true;
    }

    private boolean validateLeaveInputs(DatePicker start, DatePicker end, TextArea reason, Label msg) {
        if (start.getValue() == null) {
            msg.setText("✗ Start date is required");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        if (end.getValue() == null) {
            msg.setText("✗ End date is required");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }

        // Validation 1: Start date must be at least tomorrow (not same day)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate startDate = start.getValue();

        if (!startDate.isAfter(today)) {
            msg.setText("✗ Leave must start from tomorrow (not today or past dates)");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }

        // Validation 2: Start date must be before end date
        if (startDate.isAfter(end.getValue())) {
            msg.setText("✗ Start date must be before end date");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }

        // Validation 3: Check if user already has a PENDING leave
        java.util.List<Leave> existingLeaves = controller.getMyLeaves();
        boolean hasPending = existingLeaves.stream()
                .anyMatch(l -> "PENDING".equals(l.getStatus()));

        if (hasPending) {
            msg.setText("✗ You already have a pending leave. Please wait for it to be approved or rejected.");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }

        // Validation 4: Reason required
        if (reason.getText().trim().isEmpty()) {
            msg.setText("✗ Please provide a reason for your leave");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        return true;
    }

    private boolean validateEmergencyContact(TextField name, ComboBox<String> relationship, TextField number, Label msg) {
        if (name.getText().trim().isEmpty()) {
            msg.setText("✗ Contact name is required");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        if (relationship.getValue() == null) {
            msg.setText("✗ Please select a relationship");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        if (number.getText().trim().isEmpty()) {
            msg.setText("✗ Contact number is required");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        if (!number.getText().trim().matches("^[\\d+\\-\\s()]+$")) {
            msg.setText("✗ Invalid contact number format");
            msg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
            return false;
        }
        return true;
    }
}