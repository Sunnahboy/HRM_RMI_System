package com.hrmrmi.client.gui;

import com.hrmrmi.client.controller.HRController; // Import Controller
import com.hrmrmi.common.model.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class HRGUI extends Application {
    private HRController controller; // Use Controller instead of Service
    private Employee currentUser;

    public HRGUI() { this.currentUser = null; }
    public HRGUI(Employee user) { this.currentUser = user; }

    @Override
    public void start(Stage primaryStage) {
        // 1. CONNECT VIA CONTROLLER
        try {
            controller = new HRController();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }

        TabPane tabPane = new TabPane();

        // --- TAB 1: MY PROFILE ---
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


        // --- ADMIN SECTION ---
        if (currentUser.getRole().equalsIgnoreCase("admin") ||
                currentUser.getDepartment().equalsIgnoreCase("HR")) {

            // --- TAB 2: REGISTER NEW EMPLOYEE ---
            VBox registerBox = new VBox(10);
            registerBox.setPadding(new Insets(20));

            TextField fName = new TextField(); fName.setPromptText("First Name");
            TextField lName = new TextField(); lName.setPromptText("Last Name");
            TextField icNum = new TextField(); icNum.setPromptText("Passport/IC Number");
            TextField deptField = new TextField(); deptField.setPromptText("Department");
            TextField posField = new TextField(); posField.setPromptText("Position");

            Button btnRegister = new Button("Register");
            btnRegister.setOnAction(e -> {
                try {
                    // USE CONTROLLER
                    boolean success = controller.registerEmployees(
                            fName.getText(), lName.getText(), icNum.getText(),
                            deptField.getText(), posField.getText()
                    );
                    if(success) {
                        new Alert(Alert.AlertType.INFORMATION, "Success!").show();
                        fName.clear(); lName.clear(); icNum.clear(); deptField.clear(); posField.clear();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed.").show();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            registerBox.getChildren().addAll(new Label("New Registration"), fName, lName, icNum, deptField, posField, btnRegister);
            Tab regTab = new Tab("Register", registerBox);
            regTab.setClosable(false);
            tabPane.getTabs().add(regTab);


            // --- TAB 3: EMPLOYEE DIRECTORY ---
            VBox listBox = new VBox(10);
            listBox.setPadding(new Insets(10));

            HBox searchBox = new HBox(10);
            TextField searchField = new TextField();
            searchField.setPromptText("Search Name or Dept...");
            Button btnSearch = new Button("Search");
            Button btnReset = new Button("Reset");

            searchBox.getChildren().addAll(searchField, btnSearch, btnReset);

            TableView<Employee> table = new TableView<>();
            TableColumn<Employee, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Employee, String> colName = new TableColumn<>("First Name");
            colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            TableColumn<Employee, String> colLast = new TableColumn<>("Last Name");
            colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            TableColumn<Employee, String> colDept = new TableColumn<>("Dept");
            colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
            TableColumn<Employee, String> colRole = new TableColumn<>("Role");
            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            TableColumn<Employee, String> colEmail = new TableColumn<>("Email");
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn<Employee, Double> colSalary = new TableColumn<>("Salary");
            colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

            table.getColumns().addAll(colId, colName, colLast, colDept, colRole, colEmail, colSalary);

            // FIRE BUTTON
            Button btnFire = new Button("Fire Selected Employee");
            btnFire.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

            btnFire.setOnAction(e -> {
                Employee selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Select an employee to fire.").show();
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Confirm Termination");
                dialog.setHeaderText("Firing: " + selected.getFirstName());
                dialog.setContentText("Reason:");

                dialog.showAndWait().ifPresent(reason -> {
                    if (reason.trim().isEmpty()) return;
                    // USE CONTROLLER
                    if (controller.fireEmployee(String.valueOf(selected.getId()), reason)) {
                        new Alert(Alert.AlertType.INFORMATION, "Terminated.").show();
                        table.getItems().remove(selected);
                    }
                });
            });

            // EDIT BUTTON
            Button btnEdit = new Button("Edit Details / Promote");
            btnEdit.setOnAction(e -> {
                Employee selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Select an employee first.").show();
                    return;
                }

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Update Employee Status");
                dialog.setHeaderText("Editing: " + selected.getFirstName());
                ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10); grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField deptInput = new TextField(selected.getDepartment());
                TextField posInput = new TextField(selected.getPosition());
                TextField salaryInput = new TextField(String.valueOf(selected.getSalary()));

                grid.add(new Label("Department: "), 0, 0); grid.add(deptInput, 1, 0);
                grid.add(new Label("Position: "), 0, 1); grid.add(posInput, 1, 1);
                grid.add(new Label("Salary: "), 0, 2); grid.add(salaryInput, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.showAndWait().ifPresent(response -> {
                    if (response == saveButtonType) {
                        try {
                            double newSalary = Double.parseDouble(salaryInput.getText());
                            // USE CONTROLLER
                            boolean done = controller.updateEmployeeStatus(
                                    String.valueOf(selected.getId()),
                                    deptInput.getText(),
                                    posInput.getText(),
                                    newSalary
                            );

                            if (done) {
                                new Alert(Alert.AlertType.INFORMATION, "Updated!").show();
                                btnReset.fire();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Update failed!").show();
                            }
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Invalid Salary!").show();
                        }
                    }
                });
            });

            HBox actionButtons = new HBox(10, btnEdit, btnFire);

            // SEARCH & RESET LOGIC
            btnSearch.setOnAction(e -> {
                // USE CONTROLLER
                List<Employee> results = controller.searchProfile(searchField.getText());
                table.setItems(FXCollections.observableArrayList(results));
            });

            btnReset.setOnAction(e -> {
                searchField.clear();
                // USE CONTROLLER
                List<Employee> all = controller.getAllEmployees();
                table.setItems(FXCollections.observableArrayList(all));
            });

            btnReset.fire(); // Initial Load

            listBox.getChildren().addAll(new Label("Employee Directory"), searchBox, table, actionButtons);
            Tab listTab = new Tab("Directory", listBox);
            listTab.setClosable(false);
            tabPane.getTabs().add(listTab);


            // --- TAB 4: LEAVE REQUESTS ---
            VBox leaveBox = new VBox(10);
            leaveBox.setPadding(new Insets(10));

            TableView<Leave> leaveTable = new TableView<>();
            TableColumn<Leave, Integer> colLId = new TableColumn<>("Leave ID");
            colLId.setCellValueFactory(new PropertyValueFactory<>("leaveId"));
            TableColumn<Leave, Integer> colEmpId = new TableColumn<>("Emp ID");
            colEmpId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
            TableColumn<Leave, String> colStart = new TableColumn<>("Start Date");
            colStart.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate().toString()));
            TableColumn<Leave, String> colEnd = new TableColumn<>("End Date");
            colEnd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate().toString()));
            TableColumn<Leave, String> colReason = new TableColumn<>("Reason");
            colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
            TableColumn<Leave, String> colStatus = new TableColumn<>("Status");
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            leaveTable.getColumns().addAll(colLId, colEmpId, colStart, colEnd, colReason, colStatus);

            Button btnRefreshLeaves = new Button("Refresh Requests");
            btnRefreshLeaves.setOnAction(e -> {
                // USE CONTROLLER
                List<Leave> pending = controller.getAllPendingLeaves();
                leaveTable.setItems(FXCollections.observableArrayList(pending));
            });

            Button btnApprove = new Button("Approve");
            btnApprove.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            Button btnReject = new Button("Reject");
            btnReject.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            btnApprove.setOnAction(e -> {
                Leave selected = leaveTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    // USE CONTROLLER
                    boolean ok = controller.approveLeave(String.valueOf(selected.getLeaveId()), "Approved");
                    if (ok) {
                        new Alert(Alert.AlertType.INFORMATION, "Leave Approved!").show();
                        btnRefreshLeaves.fire();
                    }
                }
            });

            btnReject.setOnAction(e -> {
                Leave selected = leaveTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    // USE CONTROLLER
                    boolean ok = controller.approveLeave(String.valueOf(selected.getLeaveId()), "Rejected");
                    if (ok) {
                        new Alert(Alert.AlertType.INFORMATION, "Leave Rejected!").show();
                        btnRefreshLeaves.fire();
                    }
                }
            });

            HBox leaveActions = new HBox(10, btnApprove, btnReject, btnRefreshLeaves);
            btnRefreshLeaves.fire(); // Initial Load

            leaveBox.getChildren().addAll(new Label("Pending Leave Requests"), leaveTable, leaveActions);
            Tab leaveTab = new Tab("Leave Requests", leaveBox);
            leaveTab.setClosable(false);
            tabPane.getTabs().add(leaveTab);
        }

        // --- TAB 5: REPORTS ---
        VBox reportBox = new VBox(15);
        reportBox.setPadding(new Insets(20));

        Label lblReportHeader = new Label("Generate Annual Leave Report");
        lblReportHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        HBox inputRow = new HBox(10);
        TextField txtReportId = new TextField(); txtReportId.setPromptText("EmployeeID");
        TextField txtReportYear = new TextField(); txtReportYear.setPromptText("Year"); txtReportYear.setText("2024");
        Button btnGenerate = new Button("Generate Report");

        inputRow.getChildren().addAll(new Label("ID: "), txtReportId, new Label("Year: "), txtReportYear, btnGenerate);

        TextArea reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setPromptText("Report details will appear here shortly...");
        reportOutput.setPrefHeight(300);
        reportOutput.setStyle("-fx-font-family: 'Monospaced';");

        btnGenerate.setOnAction(e -> {
            String idStr = txtReportId.getText();
            String yearStr = txtReportYear.getText();

            if (idStr.isEmpty() || yearStr.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter valid ID and Year").show();
                return;
            }

            try {
                int year = Integer.parseInt(yearStr);
                // USE CONTROLLER
                Report report = controller.generateReport(idStr, year);

                if(report != null) {
                    String displayText = String.format(
                            "========================================\n" +
                                    "          ANNUAL LEAVE REPORT           \n" +
                                    "========================================\n\n" +
                                    "  Employee Name  : %s\n" +
                                    "  Employee ID    : %d\n" +
                                    "  Year           : %d\n\n" +
                                    "----------------------------------------\n" +
                                    "  Total Approved Leaves :   %2d days\n" +
                                    "  Remaining Balance     :   %2d days\n" +
                                    "----------------------------------------\n\n" +
                                    "  Generated on   : %s\n" +
                                    "  Generated by   : %s\n\n" +
                                    "========================================",
                            report.getEmployeeName(), report.getEmployeeId(), year,
                            report.getTotalLeavesTaken(), report.getRemainingLeaveBalance(),
                            report.getGeneratedDate().toString(), report.getGeneratedBy()
                    );
                    reportOutput.setText(displayText);
                } else {
                    reportOutput.setText("No data found.\nConfirm if Employee ID exists in company database");
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "ID and Year must be numbers!").show();
            }
        });

        reportBox.getChildren().addAll(lblReportHeader, inputRow, reportOutput);
        Tab reportTab = new Tab("Reports", reportBox);
        reportTab.setClosable(false);
        tabPane.getTabs().add(reportTab);

        Scene scene = new Scene(tabPane, 800, 500);
        primaryStage.setTitle("HRM Dashboard - " + currentUser.getFirstName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}