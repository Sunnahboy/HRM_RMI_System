//package com.hrmrmi.client.gui;
//
//import com.hrmrmi.client.controller.HRController; // Import Controller
//import com.hrmrmi.common.model.*;
//import javafx.application.Application;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.stage.Stage;
//
//import java.util.List;
//
//public class HRGUI extends Application {
//    private HRController controller; // Use Controller instead of Service
//    private Employee currentUser;
//
//    public HRGUI() { this.currentUser = null; }
//    public HRGUI(Employee user) { this.currentUser = user; }
//
//    @Override
//    public void start(Stage primaryStage) {
//        // 1. CONNECT VIA CONTROLLER
//        try {
//            controller = new HRController();
//        } catch (Exception e) {
//            new Alert(Alert.AlertType.ERROR, "Connection Failed: " + e.getMessage()).showAndWait();
//            return;
//        }
//
//        TabPane tabPane = new TabPane();
//
//        // --- TAB 1: MY PROFILE ---
//        VBox profileBox = new VBox(15);
//        profileBox.setPadding(new Insets(20));
//
//        Label welcomeLabel = new Label("Welcome, " + currentUser.getFirstName() + "!");
//        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//
//        Label detailsLabel = new Label(
//                "ID: " + currentUser.getId() + "\n" +
//                        "Role: " + currentUser.getRole() + "\n" +
//                        "Department: " + currentUser.getDepartment() + "\n" +
//                        "Email: " + currentUser.getEmail()
//        );
//        profileBox.getChildren().addAll(welcomeLabel, detailsLabel);
//        Tab profileTab = new Tab("My Profile", profileBox);
//        profileTab.setClosable(false);
//        tabPane.getTabs().add(profileTab);
//
//
//        // --- ADMIN SECTION ---
//        if (currentUser.getRole().equalsIgnoreCase("admin") ||
//                currentUser.getDepartment().equalsIgnoreCase("HR")) {
//
//            // --- TAB 2: REGISTER NEW EMPLOYEE ---
//            VBox registerBox = new VBox(10);
//            registerBox.setPadding(new Insets(20));
//
//            TextField fName = new TextField(); fName.setPromptText("First Name");
//            TextField lName = new TextField(); lName.setPromptText("Last Name");
//            TextField icNum = new TextField(); icNum.setPromptText("Passport/IC Number");
//            TextField deptField = new TextField(); deptField.setPromptText("Department");
//            TextField posField = new TextField(); posField.setPromptText("Position");
//
//            Button btnRegister = new Button("Register");
//            btnRegister.setOnAction(e -> {
//                try {
//                    // USE CONTROLLER
//                    boolean success = controller.registerEmployees(
//                            fName.getText(), lName.getText(), icNum.getText(),
//                            deptField.getText(), posField.getText()
//                    );
//                    if(success) {
//                        new Alert(Alert.AlertType.INFORMATION, "Success!").show();
//                        fName.clear(); lName.clear(); icNum.clear(); deptField.clear(); posField.clear();
//                    } else {
//                        new Alert(Alert.AlertType.ERROR, "Failed.").show();
//                    }
//                } catch (Exception ex) { ex.printStackTrace(); }
//            });
//
//            registerBox.getChildren().addAll(new Label("New Registration"), fName, lName, icNum, deptField, posField, btnRegister);
//            Tab regTab = new Tab("Register", registerBox);
//            regTab.setClosable(false);
//            tabPane.getTabs().add(regTab);
//
//
//            // --- TAB 3: EMPLOYEE DIRECTORY ---
//            VBox listBox = new VBox(10);
//            listBox.setPadding(new Insets(10));
//
//            HBox searchBox = new HBox(10);
//            TextField searchField = new TextField();
//            searchField.setPromptText("Search Name or Dept...");
//            Button btnSearch = new Button("Search");
//            Button btnReset = new Button("Reset");
//
//            searchBox.getChildren().addAll(searchField, btnSearch, btnReset);
//
//            TableView<Employee> table = new TableView<>();
//            TableColumn<Employee, Integer> colId = new TableColumn<>("ID");
//            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
//            TableColumn<Employee, String> colName = new TableColumn<>("First Name");
//            colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
//            TableColumn<Employee, String> colLast = new TableColumn<>("Last Name");
//            colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
//            TableColumn<Employee, String> colDept = new TableColumn<>("Dept");
//            colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
//            TableColumn<Employee, String> colRole = new TableColumn<>("Role");
//            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
//            TableColumn<Employee, String> colEmail = new TableColumn<>("Email");
//            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
//            TableColumn<Employee, Double> colSalary = new TableColumn<>("Salary");
//            colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
//
//            table.getColumns().addAll(colId, colName, colLast, colDept, colRole, colEmail, colSalary);
//
//            // FIRE BUTTON
//            Button btnFire = new Button("Fire Selected Employee");
//            btnFire.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
//
//            btnFire.setOnAction(e -> {
//                Employee selected = table.getSelectionModel().getSelectedItem();
//                if (selected == null) {
//                    new Alert(Alert.AlertType.WARNING, "Select an employee to fire.").show();
//                    return;
//                }
//                TextInputDialog dialog = new TextInputDialog();
//                dialog.setTitle("Confirm Termination");
//                dialog.setHeaderText("Firing: " + selected.getFirstName());
//                dialog.setContentText("Reason:");
//
//                dialog.showAndWait().ifPresent(reason -> {
//                    if (reason.trim().isEmpty()) return;
//                    // USE CONTROLLER
//                    if (controller.fireEmployee(String.valueOf(selected.getId()), reason)) {
//                        new Alert(Alert.AlertType.INFORMATION, "Terminated.").show();
//                        table.getItems().remove(selected);
//                    }
//                });
//            });
//
//            // EDIT BUTTON
//            Button btnEdit = new Button("Edit Details / Promote");
//            btnEdit.setOnAction(e -> {
//                Employee selected = table.getSelectionModel().getSelectedItem();
//                if (selected == null) {
//                    new Alert(Alert.AlertType.WARNING, "Select an employee first.").show();
//                    return;
//                }
//
//                Dialog<ButtonType> dialog = new Dialog<>();
//                dialog.setTitle("Update Employee Status");
//                dialog.setHeaderText("Editing: " + selected.getFirstName());
//                ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
//                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
//
//                GridPane grid = new GridPane();
//                grid.setHgap(10); grid.setVgap(10);
//                grid.setPadding(new Insets(20, 150, 10, 10));
//
//                TextField deptInput = new TextField(selected.getDepartment());
//                TextField posInput = new TextField(selected.getPosition());
//                TextField salaryInput = new TextField(String.valueOf(selected.getSalary()));
//
//                grid.add(new Label("Department: "), 0, 0); grid.add(deptInput, 1, 0);
//                grid.add(new Label("Position: "), 0, 1); grid.add(posInput, 1, 1);
//                grid.add(new Label("Salary: "), 0, 2); grid.add(salaryInput, 1, 2);
//
//                dialog.getDialogPane().setContent(grid);
//
//                dialog.showAndWait().ifPresent(response -> {
//                    if (response == saveButtonType) {
//                        try {
//                            double newSalary = Double.parseDouble(salaryInput.getText());
//                            // USE CONTROLLER
//                            boolean done = controller.updateEmployeeStatus(
//                                    String.valueOf(selected.getId()),
//                                    deptInput.getText(),
//                                    posInput.getText(),
//                                    newSalary
//                            );
//
//                            if (done) {
//                                new Alert(Alert.AlertType.INFORMATION, "Updated!").show();
//                                btnReset.fire();
//                            } else {
//                                new Alert(Alert.AlertType.ERROR, "Update failed!").show();
//                            }
//                        } catch (NumberFormatException ex) {
//                            new Alert(Alert.AlertType.ERROR, "Invalid Salary!").show();
//                        }
//                    }
//                });
//            });
//
//            HBox actionButtons = new HBox(10, btnEdit, btnFire);
//
//            // SEARCH & RESET LOGIC
//            btnSearch.setOnAction(e -> {
//                // USE CONTROLLER
//                List<Employee> results = controller.searchProfile(searchField.getText());
//                table.setItems(FXCollections.observableArrayList(results));
//            });
//
//            btnReset.setOnAction(e -> {
//                searchField.clear();
//                // USE CONTROLLER
//                List<Employee> all = controller.getAllEmployees();
//                table.setItems(FXCollections.observableArrayList(all));
//            });
//
//            btnReset.fire(); // Initial Load
//
//            listBox.getChildren().addAll(new Label("Employee Directory"), searchBox, table, actionButtons);
//            Tab listTab = new Tab("Directory", listBox);
//            listTab.setClosable(false);
//            tabPane.getTabs().add(listTab);
//
//
//            // --- TAB 4: LEAVE REQUESTS ---
//            VBox leaveBox = new VBox(10);
//            leaveBox.setPadding(new Insets(10));
//
//            TableView<Leave> leaveTable = new TableView<>();
//            TableColumn<Leave, Integer> colLId = new TableColumn<>("Leave ID");
//            colLId.setCellValueFactory(new PropertyValueFactory<>("leaveId"));
//            TableColumn<Leave, Integer> colEmpId = new TableColumn<>("Emp ID");
//            colEmpId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
//            TableColumn<Leave, String> colStart = new TableColumn<>("Start Date");
//            colStart.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate().toString()));
//            TableColumn<Leave, String> colEnd = new TableColumn<>("End Date");
//            colEnd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate().toString()));
//            TableColumn<Leave, String> colReason = new TableColumn<>("Reason");
//            colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
//            TableColumn<Leave, String> colStatus = new TableColumn<>("Status");
//            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
//
//            leaveTable.getColumns().addAll(colLId, colEmpId, colStart, colEnd, colReason, colStatus);
//
//            Button btnRefreshLeaves = new Button("Refresh Requests");
//            btnRefreshLeaves.setOnAction(e -> {
//                // USE CONTROLLER
//                List<Leave> pending = controller.getAllPendingLeaves();
//                leaveTable.setItems(FXCollections.observableArrayList(pending));
//            });
//
//            Button btnApprove = new Button("Approve");
//            btnApprove.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
//
//            Button btnReject = new Button("Reject");
//            btnReject.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
//
//            btnApprove.setOnAction(e -> {
//                Leave selected = leaveTable.getSelectionModel().getSelectedItem();
//                if (selected != null) {
//                    // USE CONTROLLER
//                    boolean ok = controller.approveLeave(String.valueOf(selected.getLeaveId()), "Approved");
//                    if (ok) {
//                        new Alert(Alert.AlertType.INFORMATION, "Leave Approved!").show();
//                        btnRefreshLeaves.fire();
//                    }
//                }
//            });
//
//            btnReject.setOnAction(e -> {
//                Leave selected = leaveTable.getSelectionModel().getSelectedItem();
//                if (selected != null) {
//                    // USE CONTROLLER
//                    boolean ok = controller.approveLeave(String.valueOf(selected.getLeaveId()), "Rejected");
//                    if (ok) {
//                        new Alert(Alert.AlertType.INFORMATION, "Leave Rejected!").show();
//                        btnRefreshLeaves.fire();
//                    }
//                }
//            });
//
//            HBox leaveActions = new HBox(10, btnApprove, btnReject, btnRefreshLeaves);
//            btnRefreshLeaves.fire(); // Initial Load
//
//            leaveBox.getChildren().addAll(new Label("Pending Leave Requests"), leaveTable, leaveActions);
//            Tab leaveTab = new Tab("Leave Requests", leaveBox);
//            leaveTab.setClosable(false);
//            tabPane.getTabs().add(leaveTab);
//        }
//
//        // --- TAB 5: REPORTS ---
//        VBox reportBox = new VBox(15);
//        reportBox.setPadding(new Insets(20));
//
//        Label lblReportHeader = new Label("Generate Annual Leave Report");
//        lblReportHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//
//        HBox inputRow = new HBox(10);
//        TextField txtReportId = new TextField(); txtReportId.setPromptText("EmployeeID");
//        TextField txtReportYear = new TextField(); txtReportYear.setPromptText("Year"); txtReportYear.setText("2024");
//        Button btnGenerate = new Button("Generate Report");
//
//        inputRow.getChildren().addAll(new Label("ID: "), txtReportId, new Label("Year: "), txtReportYear, btnGenerate);
//
//        TextArea reportOutput = new TextArea();
//        reportOutput.setEditable(false);
//        reportOutput.setPromptText("Report details will appear here shortly...");
//        reportOutput.setPrefHeight(300);
//        reportOutput.setStyle("-fx-font-family: 'Monospaced';");
//
//        btnGenerate.setOnAction(e -> {
//            String idStr = txtReportId.getText();
//            String yearStr = txtReportYear.getText();
//
//            if (idStr.isEmpty() || yearStr.isEmpty()) {
//                new Alert(Alert.AlertType.WARNING, "Please enter valid ID and Year").show();
//                return;
//            }
//
//            try {
//                int year = Integer.parseInt(yearStr);
//                Report report = controller.generateReport(idStr, year);
//
//                if(report != null) {
//                    Employee emp = report.getEmployeeProfile();
//
//                    // Handle null employee profile
//                    if(emp == null) {
//                        reportOutput.setText("Error: Employee profile not found for the given ID.");
//                        return;
//                    }
//
//                    // --- BUILD FAMILY STRING ---
//                    StringBuilder famStr = new StringBuilder();
//                    if(report.getFamilyDetails() == null || report.getFamilyDetails().isEmpty()) {
//                        famStr.append("  (No family records found)\n");
//                    } else {
//                        for (FamilyDetails f : report.getFamilyDetails()) {
//                            famStr.append(String.format("  - %-15s (%s) | Tel: %s\n",
//                                    f.getName(), f.getRelationship(), f.getContact()));
//                        }
//                    }
//
//                    // --- BUILD LEAVE HISTORY STRING ---
//                    StringBuilder leaveStr = new StringBuilder();
//                    if(report.getLeaveHistory() == null || report.getLeaveHistory().isEmpty()) {
//                        leaveStr.append("  (No leave applications this year)\n");
//                    } else {
//                        for (Leave l : report.getLeaveHistory()) {
//                            leaveStr.append(String.format("  - %s to %s : %s [%s]\n",
//                                    l.getStartDate(), l.getEndDate(), l.getStatus(), l.getReason()));
//                        }
//                    }
//
//                    // --- FINAL OUTPUT ---
//                    String displayText = String.format(
//                            "============================================================\n" +
//                                    "                  CONFIDENTIAL ANNUAL REPORT                \n" +
//                                    "============================================================\n\n" +
//                                    "[1] EMPLOYEE PROFILE\n" +
//                                    "--------------------\n" +
//                                    "  Name       : %s %s\n" +
//                                    "  ID         : %d\n" +
//                                    "  IC/Passport: %s\n" +
//                                    "  Department : %s\n" +
//                                    "  Position   : %s\n" +
//                                    "  Email      : %s\n\n" +
//
//                                    "[2] FAMILY DETAILS\n" +
//                                    "------------------\n" +
//                                    "%s\n" +
//
//                                    "[3] LEAVE HISTORY (%d)\n" +
//                                    "----------------------\n" +
//                                    "%s\n" +
//
//                                    "------------------------------------------------------------\n" +
//                                    "  SUMMARY:\n" +
//                                    "  Total Approved Leaves Taken: %d days\n" +
//                                    "  Remaining Leave Balance    : %d days\n" +
//                                    "------------------------------------------------------------\n" +
//                                    "  Generated on: %s\n" +
//                                    "============================================================",
//                            emp.getFirstName(), emp.getLastName(),
//                            emp.getId(),
//                            emp.getPassportNumber(),
//                            emp.getDepartment(),
//                            emp.getPosition(),
//                            emp.getEmail(),
//                            famStr.toString(),
//                            year,
//                            leaveStr.toString(),
//                            report.getTotalLeavesTaken(),
//                            emp.getLeaveBalance(),
//                            report.getGeneratedDate().toString()
//                    );
//                    reportOutput.setText(displayText);
//                } else {
//                    reportOutput.setText("No data found.");
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                reportOutput.setText("Error: " + ex.getMessage());
//            }
//        });
//
//        reportBox.getChildren().addAll(lblReportHeader, inputRow, reportOutput);
//        Tab reportTab = new Tab("Reports", reportBox);
//        reportTab.setClosable(false);
//        tabPane.getTabs().add(reportTab);
//
//        Scene scene = new Scene(tabPane, 800, 500);
//        primaryStage.setTitle("HRM Dashboard - " + currentUser.getFirstName());
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//}













package com.hrmrmi.client.gui;

import com.hrmrmi.client.controller.HRController;
import com.hrmrmi.common.model.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class HRGUI extends Application {

    // Design Constants
    private static final String PRIMARY_COLOR = "#2E7D32";
    private static final String SECONDARY_COLOR = "#1976D2";
    private static final String SUCCESS_COLOR = "#43A047";
    private static final String ERROR_COLOR = "#E53935";
    private static final String WARNING_COLOR = "#FB8C00";
    private static final String BACKGROUND_COLOR = "#F5F5F5";
    private static final String CARD_BG = "#FFFFFF";

    private HRController controller;
    private Employee currentUser;

    public HRGUI() { this.currentUser = null; }
    public HRGUI(Employee user) { this.currentUser = user; }

    @Override
    public void start(Stage primaryStage) {
        try {
            controller = new HRController();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 12; -fx-padding: 15;");

        // --- TAB 1: MY PROFILE ---
        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(30));
        profileBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox profileCard = new VBox(15);
        profileCard.setPadding(new Insets(30));
        profileCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                + "-fx-border-radius: 12; -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 2);");

        Label welcomeLabel = new Label("👤 Welcome, " + currentUser.getFirstName() + "!");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcomeLabel.setTextFill(javafx.scene.paint.Color.web(PRIMARY_COLOR));

        Label detailsLabel = new Label(
                "ID: " + currentUser.getId() + "\n" +
                        "Role: " + currentUser.getRole() + "\n" +
                        "Department: " + currentUser.getDepartment() + "\n" +
                        "Email: " + currentUser.getEmail()
        );
        detailsLabel.setFont(Font.font("Segoe UI", 13));
        detailsLabel.setTextFill(javafx.scene.paint.Color.web("#555555"));

        profileCard.getChildren().addAll(welcomeLabel, detailsLabel);
        profileBox.getChildren().add(profileCard);

        Tab profileTab = new Tab("👤 My Profile", profileBox);
        profileTab.setClosable(false);
        tabPane.getTabs().add(profileTab);

        // --- ADMIN SECTION ---
        if (currentUser.getRole().equalsIgnoreCase("admin") ||
                currentUser.getDepartment().equalsIgnoreCase("HR")) {

            // --- TAB 2: REGISTER NEW EMPLOYEE ---
            VBox registerBox = new VBox(20);
            registerBox.setPadding(new Insets(30));
            registerBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

            VBox registerCard = new VBox(15);
            registerCard.setPadding(new Insets(30));
            registerCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                    + "-fx-border-radius: 12; -fx-background-radius: 12;");

            TextField fName = createTextField("First Name");
            TextField lName = createTextField("Last Name");
            TextField icNum = createTextField("Passport/IC Number");
            TextField deptField = createTextField("Department");
            TextField posField = createTextField("Position");

            Button btnRegister = createPrimaryButton("✅ Register Employee");
            Label regMsg = new Label();
            regMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");

            btnRegister.setOnAction(e -> {
                try {
                    boolean success = controller.registerEmployees(
                            fName.getText().trim(), lName.getText().trim(), icNum.getText().trim(),
                            deptField.getText().trim(), posField.getText().trim()
                    );
                    if(success) {
                        regMsg.setText("✓ Employee registered successfully");
                        regMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
                        fName.clear(); lName.clear(); icNum.clear(); deptField.clear(); posField.clear();
                    } else {
                        regMsg.setText("✗ Registration failed");
                        regMsg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                    }
                } catch (Exception ex) {
                    regMsg.setText("✗ Error: " + ex.getMessage());
                    regMsg.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 12;");
                }
            });

            registerCard.getChildren().addAll(
                    createLabel("👥 New Employee Registration"),
                    fName, lName, icNum, deptField, posField, btnRegister, regMsg
            );
            registerBox.getChildren().add(registerCard);

            Tab regTab = new Tab("👥 Register", registerBox);
            regTab.setClosable(false);
            tabPane.getTabs().add(regTab);

            // --- TAB 3: EMPLOYEE DIRECTORY ---
            VBox listBox = new VBox(15);
            listBox.setPadding(new Insets(30));
            listBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

            VBox searchCard = new VBox(15);
            searchCard.setPadding(new Insets(20));
            searchCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                    + "-fx-border-radius: 12; -fx-background-radius: 12;");

            HBox searchBox = new HBox(10);
            searchBox.setAlignment(Pos.CENTER_LEFT);
            TextField searchField = createTextField("Search Name or Department...");
            searchField.setPrefWidth(300);
            Button btnSearch = createSecondaryButton("🔍 Search");
            Button btnReset = createSecondaryButton("🔄 Reset");
            searchBox.getChildren().addAll(searchField, btnSearch, btnReset);
            searchCard.getChildren().add(searchBox);

            TableView<Employee> table = new TableView<>();
            table.setStyle("-fx-font-size: 11;");

            TableColumn<Employee, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(50);

            TableColumn<Employee, String> colName = new TableColumn<>("First Name");
            colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            colName.setPrefWidth(100);

            TableColumn<Employee, String> colLast = new TableColumn<>("Last Name");
            colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            colLast.setPrefWidth(100);

            TableColumn<Employee, String> colDept = new TableColumn<>("Department");
            colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
            colDept.setPrefWidth(120);

            TableColumn<Employee, String> colRole = new TableColumn<>("Role");
            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            colRole.setPrefWidth(80);

            TableColumn<Employee, String> colEmail = new TableColumn<>("Email");
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colEmail.setPrefWidth(150);

            TableColumn<Employee, Double> colSalary = new TableColumn<>("Salary");
            colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
            colSalary.setPrefWidth(100);

            table.getColumns().addAll(colId, colName, colLast, colDept, colRole, colEmail, colSalary);

            Button btnFire = new Button("🚪 Terminate Employee");
            btnFire.setStyle(getDangerButtonStyle());

            Button btnEdit = new Button("✏️ Edit / Promote");
            btnEdit.setStyle(getPrimaryButtonStyle());

            btnFire.setOnAction(e -> {
                Employee selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Select an employee to terminate.").show();
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Confirm Termination");
                dialog.setHeaderText("Terminating: " + selected.getFirstName() + " " + selected.getLastName());
                dialog.setContentText("Reason for termination:");

                dialog.showAndWait().ifPresent(reason -> {
                    if (reason.trim().isEmpty()) return;
                    if (controller.fireEmployee(String.valueOf(selected.getId()), reason)) {
                        new Alert(Alert.AlertType.INFORMATION, "Employee terminated successfully.").show();
                        table.getItems().remove(selected);
                    }
                });
            });

            btnEdit.setOnAction(e -> {
                Employee selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Select an employee first.").show();
                    return;
                }

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Update Employee Status");
                dialog.setHeaderText("Editing: " + selected.getFirstName() + " " + selected.getLastName());
                ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(15); grid.setVgap(15);
                grid.setPadding(new Insets(20));

                TextField deptInput = createTextField(selected.getDepartment());
                TextField posInput = createTextField(selected.getPosition());
                TextField salaryInput = createTextField(String.valueOf(selected.getSalary()));

                grid.add(createLabel("Department:"), 0, 0);
                grid.add(deptInput, 1, 0);
                grid.add(createLabel("Position:"), 0, 1);
                grid.add(posInput, 1, 1);
                grid.add(createLabel("Salary:"), 0, 2);
                grid.add(salaryInput, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.showAndWait().ifPresent(response -> {
                    if (response == saveButtonType) {
                        try {
                            double newSalary = Double.parseDouble(salaryInput.getText());
                            boolean done = controller.updateEmployeeStatus(
                                    String.valueOf(selected.getId()),
                                    deptInput.getText().trim(),
                                    posInput.getText().trim(),
                                    newSalary
                            );

                            if (done) {
                                new Alert(Alert.AlertType.INFORMATION, "Employee updated successfully!").show();
                                btnReset.fire();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Update failed!").show();
                            }
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Invalid salary format!").show();
                        }
                    }
                });
            });

            HBox actionButtons = new HBox(10);
            actionButtons.setPadding(new Insets(15));
            actionButtons.getChildren().addAll(btnEdit, btnFire);

            btnSearch.setOnAction(e -> {
                List<Employee> results = controller.searchProfile(searchField.getText().trim());
                table.setItems(FXCollections.observableArrayList(results));
            });

            btnReset.setOnAction(e -> {
                searchField.clear();
                List<Employee> all = controller.getAllEmployees();
                table.setItems(FXCollections.observableArrayList(all));
            });

            btnReset.fire();

            listBox.getChildren().addAll(searchCard, table, actionButtons);
            Tab listTab = new Tab("📋 Directory", listBox);
            listTab.setClosable(false);
            tabPane.getTabs().add(listTab);

            // --- TAB 4: LEAVE REQUESTS ---
            VBox leaveBox = new VBox(15);
            leaveBox.setPadding(new Insets(30));
            leaveBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

            TableView<Leave> leaveTable = new TableView<>();
            leaveTable.setStyle("-fx-font-size: 11;");

            TableColumn<Leave, Integer> colLId = new TableColumn<>("Leave ID");
            colLId.setCellValueFactory(new PropertyValueFactory<>("leaveId"));
            colLId.setPrefWidth(80);

            TableColumn<Leave, Integer> colEmpId = new TableColumn<>("Emp ID");
            colEmpId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
            colEmpId.setPrefWidth(80);

            TableColumn<Leave, String> colStart = new TableColumn<>("Start Date");
            colStart.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate().toString()));
            colStart.setPrefWidth(120);

            TableColumn<Leave, String> colEnd = new TableColumn<>("End Date");
            colEnd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate().toString()));
            colEnd.setPrefWidth(120);

            TableColumn<Leave, String> colReason = new TableColumn<>("Reason");
            colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
            colReason.setPrefWidth(200);

            TableColumn<Leave, String> colStatus = new TableColumn<>("Status");
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colStatus.setPrefWidth(100);
            colStatus.setCellFactory(col -> new StatusCell());

            leaveTable.getColumns().addAll(colLId, colEmpId, colStart, colEnd, colReason, colStatus);

            Button btnRefreshLeaves = createSecondaryButton("🔄 Refresh");
            Button btnApprove = new Button("✅ Approve");
            btnApprove.setStyle(getSuccessButtonStyle());
            Button btnReject = new Button("❌ Reject");
            btnReject.setStyle(getDangerButtonStyle());

            btnRefreshLeaves.setOnAction(e -> {
                List<Leave> pending = controller.getAllPendingLeaves();
                leaveTable.setItems(FXCollections.observableArrayList(pending));
            });

            btnApprove.setOnAction(e -> {
                Leave selected = leaveTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    boolean ok = controller.approveLeave(String.valueOf(selected.getLeaveId()), "Approved");
                    if (ok) {
                        new Alert(Alert.AlertType.INFORMATION, "✓ Leave approved!").show();
                        btnRefreshLeaves.fire();
                    }
                }
            });

            btnReject.setOnAction(e -> {
                Leave selected = leaveTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    boolean ok = controller.approveLeave(String.valueOf(selected.getLeaveId()), "Rejected");
                    if (ok) {
                        new Alert(Alert.AlertType.INFORMATION, "✗ Leave rejected!").show();
                        btnRefreshLeaves.fire();
                    }
                }
            });

            HBox leaveActions = new HBox(10);
            leaveActions.setPadding(new Insets(15));
            leaveActions.getChildren().addAll(btnApprove, btnReject, btnRefreshLeaves);

            btnRefreshLeaves.fire();

            leaveBox.getChildren().addAll(createLabel("📅 Pending Leave Requests"), leaveTable, leaveActions);
            Tab leaveTab = new Tab("📅 Leave Requests", leaveBox);
            leaveTab.setClosable(false);
            tabPane.getTabs().add(leaveTab);
        }

        // --- TAB 5: REPORTS ---
        VBox reportBox = new VBox(20);
        reportBox.setPadding(new Insets(30));
        reportBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox reportCard = new VBox(15);
        reportCard.setPadding(new Insets(30));
        reportCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                + "-fx-border-radius: 12; -fx-background-radius: 12;");

        Label lblReportHeader = createLabel("📊 Generate Annual Leave Report");

        HBox inputRow = new HBox(15);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtReportId = createTextField("Employee ID");
        txtReportId.setPrefWidth(150);
        TextField txtReportYear = createTextField("2024");
        txtReportYear.setPrefWidth(150);
        Button btnGenerate = createPrimaryButton("📈 Generate Report");

        inputRow.getChildren().addAll(
                new Label("Employee ID:"), txtReportId,
                new Label("Year:"), txtReportYear, btnGenerate
        );

        TextArea reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setPromptText("Report details will appear here...");
        reportOutput.setPrefHeight(400);
        reportOutput.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        reportOutput.setWrapText(true);

        btnGenerate.setOnAction(e -> {
            String idStr = txtReportId.getText().trim();
            String yearStr = txtReportYear.getText().trim();

            if (idStr.isEmpty() || yearStr.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter valid ID and Year").show();
                return;
            }

            try {
                int year = Integer.parseInt(yearStr);
                Report report = controller.generateReport(idStr, year);

                if(report != null) {
                    Employee emp = report.getEmployeeProfile();

                    if(emp == null) {
                        reportOutput.setText("Error: Employee profile not found for the given ID.");
                        return;
                    }

                    StringBuilder famStr = new StringBuilder();
                    if(report.getFamilyDetails() == null || report.getFamilyDetails().isEmpty()) {
                        famStr.append("  (No family records found)\n");
                    } else {
                        for (FamilyDetails f : report.getFamilyDetails()) {
                            famStr.append(String.format("  - %-15s (%s) | Tel: %s\n",
                                    f.getName(), f.getRelationship(), f.getContact()));
                        }
                    }

                    StringBuilder leaveStr = new StringBuilder();
                    if(report.getLeaveHistory() == null || report.getLeaveHistory().isEmpty()) {
                        leaveStr.append("  (No leave applications this year)\n");
                    } else {
                        for (Leave l : report.getLeaveHistory()) {
                            leaveStr.append(String.format("  - %s to %s : %s [%s]\n",
                                    l.getStartDate(), l.getEndDate(), l.getStatus(), l.getReason()));
                        }
                    }

                    String displayText = String.format(
                            "============================================================\n" +
                                    "                  CONFIDENTIAL ANNUAL REPORT                \n" +
                                    "============================================================\n\n" +
                                    "[1] EMPLOYEE PROFILE\n" +
                                    "--------------------\n" +
                                    "  Name       : %s %s\n" +
                                    "  ID         : %d\n" +
                                    "  IC/Passport: %s\n" +
                                    "  Department : %s\n" +
                                    "  Position   : %s\n" +
                                    "  Email      : %s\n\n" +

                                    "[2] FAMILY DETAILS\n" +
                                    "------------------\n" +
                                    "%s\n" +

                                    "[3] LEAVE HISTORY (%d)\n" +
                                    "----------------------\n" +
                                    "%s\n" +

                                    "------------------------------------------------------------\n" +
                                    "  SUMMARY:\n" +
                                    "  Total Approved Leaves Taken: %d days\n" +
                                    "  Remaining Leave Balance    : %d days\n" +
                                    "------------------------------------------------------------\n" +
                                    "  Generated on: %s\n" +
                                    "============================================================",
                            emp.getFirstName(), emp.getLastName(),
                            emp.getId(),
                            emp.getPassportNumber(),
                            emp.getDepartment(),
                            emp.getPosition(),
                            emp.getEmail(),
                            famStr.toString(),
                            year,
                            leaveStr.toString(),
                            report.getTotalLeavesTaken(),
                            emp.getLeaveBalance(),
                            report.getGeneratedDate().toString()
                    );
                    reportOutput.setText(displayText);
                } else {
                    reportOutput.setText("No data found for the specified ID and year.");
                }
            } catch (NumberFormatException ex) {
                reportOutput.setText("Error: Invalid year format. Please enter a valid year.");
            } catch (Exception ex) {
                reportOutput.setText("Error: " + ex.getMessage());
            }
        });

        reportCard.getChildren().addAll(lblReportHeader, inputRow, reportOutput);
        reportBox.getChildren().add(reportCard);

        Tab reportTab = new Tab("📊 Reports", reportBox);
        reportTab.setClosable(false);
        tabPane.getTabs().add(reportTab);

        Scene scene = new Scene(tabPane, 1200, 800);
        primaryStage.setTitle("🏢 HRM Dashboard - " + currentUser.getFirstName());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // ============ UTILITY METHODS ============

    private TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(getTextFieldStyle());
        tf.setPrefHeight(40);
        return tf;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.setTextFill(javafx.scene.paint.Color.web("#333333"));
        return label;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(getPrimaryButtonStyle());
        btn.setPrefHeight(40);
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(getSecondaryButtonStyle());
        btn.setPrefHeight(40);
        return btn;
    }

    private String getPrimaryButtonStyle() {
        return "-fx-padding: 10 20 10 20; -fx-font-size: 12; -fx-font-weight: bold; "
                + "-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);";
    }

    private String getSecondaryButtonStyle() {
        return "-fx-padding: 10 20 10 20; -fx-font-size: 12; "
                + "-fx-background-color: white; -fx-text-fill: " + SECONDARY_COLOR + "; "
                + "-fx-border-color: " + SECONDARY_COLOR + "; -fx-border-width: 1.5; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String getSuccessButtonStyle() {
        return "-fx-padding: 10 20 10 20; -fx-font-size: 12; -fx-font-weight: bold; "
                + "-fx-background-color: " + SUCCESS_COLOR + "; -fx-text-fill: white; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String getDangerButtonStyle() {
        return "-fx-padding: 10 20 10 20; -fx-font-size: 12; -fx-font-weight: bold; "
                + "-fx-background-color: " + ERROR_COLOR + "; -fx-text-fill: white; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String getTextFieldStyle() {
        return "-fx-padding: 10 15 10 15; -fx-border-color: #E0E0E0; -fx-border-width: 1.5; "
                + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; "
                + "-fx-focus-color: " + SECONDARY_COLOR + "; "
                + "-fx-faint-focus-color: rgba(25, 118, 210, 0.1);";
    }

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
                    case "Approved":
                        setStyle("-fx-text-fill: #43A047; -fx-font-weight: bold;");
                        break;
                    case "REJECTED":
                    case "Rejected":
                        setStyle("-fx-text-fill: #E53935; -fx-font-weight: bold;");
                        break;
                    case "PENDING":
                        setStyle("-fx-text-fill: #FB8C00; -fx-font-weight: bold;");
                        break;
                }
            }
        }
    }
}