package com.hrmrmi.client.gui;

import com.hrmrmi.client.controller.EmployeeController;
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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class HRGUI {//extends Application {

    // Design Constants
    private static final String PRIMARY_COLOR = "#2E7D32";
    private static final String SECONDARY_COLOR = "#1976D2";
    private static final String SUCCESS_COLOR = "#43A047";
    private static final String ERROR_COLOR = "#E53935";
    private static final String WARNING_COLOR = "#FB8C00";
    private static final String BACKGROUND_COLOR = "#F5F5F5";
    private static final String CARD_BG = "#FFFFFF";

    private HRController controller;
    private EmployeeController empController;
    private Employee currentUser;

    public HRGUI() { this.currentUser = null; }
    public HRGUI(Employee user) { this.currentUser = user; }


    public void show(Stage primaryStage) {
        try {
            controller = new HRController();

            empController = new EmployeeController();
            empController.setLoggedInEmployee(currentUser);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }

        // --- TABS SETUP ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 12; -fx-padding: 15;");

        // [TAB 1: MY PROFILE]
        VBox profileBox = new VBox(15);
        profileBox.setPadding(new Insets(30));
        profileBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox profileCard = new VBox(15);
        profileCard.setPadding(new Insets(30));
        profileCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; -fx-border-radius: 12;");

        // Interactive Fields
        TextField pfFirstName = new TextField(currentUser.getFirstName());
        pfFirstName.setStyle(getTextFieldStyle());
        TextField pfLastName = new TextField(currentUser.getLastName());
        pfLastName.setStyle(getTextFieldStyle());
        TextField pfPhone = new TextField(currentUser.getPhoneNumber());
        pfPhone.setStyle(getTextFieldStyle());
        TextField pfEmail = new TextField(currentUser.getEmail());
        pfEmail.setStyle(getTextFieldStyle());
        pfEmail.setDisable(true);
        TextField pfDept = new TextField(currentUser.getDepartment());
        pfDept.setStyle(getTextFieldStyle());
        pfDept.setDisable(true);

        Button btnSaveProfile = createPrimaryButton("💾 Update Profile");
        Button btnRefreshProfile = createSecondaryButton("🔄 Refresh");
        Label profileMsg = new Label();

        // Save Action using empController
        btnSaveProfile.setOnAction(e -> {
            currentUser.setFirstName(pfFirstName.getText());
            currentUser.setLastName(pfLastName.getText());
            currentUser.setPhoneNumber(pfPhone.getText());

            if (empController.updateProfile(currentUser)) {
                profileMsg.setText("✓ Profile updated successfully");
                profileMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + ";");
                btnRefreshProfile.fire(); // Refresh to ensure sync
            } else {
                profileMsg.setText("✗ Update failed");
                profileMsg.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
            }
        });

        // Refresh Action using empController
        btnRefreshProfile.setOnAction(e -> {
            Employee fresh = empController.viewProfile();
            if (fresh != null) {
                currentUser = fresh;
                pfFirstName.setText(fresh.getFirstName());
                pfLastName.setText(fresh.getLastName());
                pfPhone.setText(fresh.getPhoneNumber());
                profileMsg.setText("✓ Data refreshed");
            }
        });

        GridPane pGrid = new GridPane();
        pGrid.setHgap(15); pGrid.setVgap(15);
        pGrid.addRow(0, createLabel("First Name"), pfFirstName);
        pGrid.addRow(1, createLabel("Last Name"), pfLastName);
        pGrid.addRow(2, createLabel("Phone No."), pfPhone);
        pGrid.addRow(3, createLabel("Email"), pfEmail);
        pGrid.addRow(4, createLabel("Department"), pfDept);

        profileCard.getChildren().addAll(
                createLabel("👤 My Profile Details"),
                pGrid,
                new HBox(10, btnSaveProfile, btnRefreshProfile),
                profileMsg
        );
        profileBox.getChildren().add(profileCard);

        Tab profileTab = new Tab("👤 My Profile", profileBox);
        profileTab.setClosable(false);
        tabPane.getTabs().add(profileTab);

        // --- TAB: APPLY LEAVE ---
        VBox applyBox = new VBox(15);
        applyBox.setPadding(new Insets(30));
        applyBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox applyCard = new VBox(15);
        applyCard.setPadding(new Insets(30));
        applyCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; -fx-border-radius: 12;");

        DatePicker startPicker = new DatePicker(); startPicker.setStyle(getTextFieldStyle()); startPicker.setPrefHeight(40);
        DatePicker endPicker = new DatePicker(); endPicker.setStyle(getTextFieldStyle()); endPicker.setPrefHeight(40);
        TextArea reasonArea = new TextArea(); reasonArea.setStyle("-fx-padding: 10 15; -fx-border-color: #E0E0E0; -fx-background-radius: 6;"); reasonArea.setPrefRowCount(3);

        Button btnSubmitLeave = createPrimaryButton("📤 Submit Application");
        Label leaveMsg = new Label();

        btnSubmitLeave.setOnAction(e -> {
            if (startPicker.getValue() == null || endPicker.getValue() == null || reasonArea.getText().isEmpty()) {
                leaveMsg.setText("✗ All fields are required");
                leaveMsg.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
                return;
            }
            Leave leave = new Leave();
            leave.setEmployeeId(currentUser.getId());
            leave.setStartDate(java.sql.Date.valueOf(startPicker.getValue()));
            leave.setEndDate(java.sql.Date.valueOf(endPicker.getValue()));
            leave.setReason(reasonArea.getText());
            leave.setStatus("PENDING");

            // Use empController here
            if (empController.applyLeave(leave)) {
                leaveMsg.setText("✓ Leave submitted successfully");
                leaveMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + ";");
                startPicker.setValue(null); endPicker.setValue(null); reasonArea.clear();
            } else {
                leaveMsg.setText("✗ Failed: Invalid dates or overlapping leave");
                leaveMsg.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
            }
        });

        GridPane lGrid = new GridPane();
        lGrid.setHgap(15); lGrid.setVgap(15);
        lGrid.addRow(0, createLabel("Start Date"), startPicker);
        lGrid.addRow(1, createLabel("End Date"), endPicker);
        lGrid.addRow(2, createLabel("Reason"), reasonArea);

        applyCard.getChildren().addAll(createLabel("📋 Apply for Leave"), lGrid, btnSubmitLeave, leaveMsg);
        applyBox.getChildren().add(applyCard);
        tabPane.getTabs().add(new Tab("📋 Apply Leave", applyBox));


        // --- TAB: LEAVE STATUS (Personal) ---
        VBox statusBox = new VBox(15);
        statusBox.setPadding(new Insets(30));
        statusBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        TableView<Leave> myLeaveTable = new TableView<>();
        myLeaveTable.setStyle("-fx-font-size: 11;");

        TableColumn<Leave, String> colMyStart = new TableColumn<>("Start");
        colMyStart.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));

        TableColumn<Leave, String> colMyEnd = new TableColumn<>("End");
        colMyEnd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndDate().toString()));

        TableColumn<Leave, String> colMyStatus = new TableColumn<>("Status");
        colMyStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        colMyStatus.setCellFactory(col -> new StatusCell());

        TableColumn<Leave, String> colMyReason = new TableColumn<>("Reason");
        colMyReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));

        myLeaveTable.getColumns().addAll(colMyStart, colMyEnd, colMyStatus, colMyReason);
        myLeaveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnRefreshMyStatus = createSecondaryButton("🔄 Refresh List");
        btnRefreshMyStatus.setOnAction(e -> {
            // Use empController here
            List<Leave> leaves = empController.getMyLeaves();
            myLeaveTable.setItems(FXCollections.observableArrayList(leaves));
        });

        btnRefreshMyStatus.fire(); // Initial Load

        statusBox.getChildren().addAll(createLabel("📅 My Leave History"), myLeaveTable, btnRefreshMyStatus);
        tabPane.getTabs().add(new Tab("📅 My Status", statusBox));


        // --- TAB: EMERGENCY CONTACT ---
        VBox emBox = new VBox(15);
        emBox.setPadding(new Insets(30));
        emBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox emCard = new VBox(15);
        emCard.setPadding(new Insets(30));
        emCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; -fx-border-radius: 12;");

        TextField emName = createTextField("");
        TextField emContact = createTextField("");
        ComboBox<String> emRel = new ComboBox<>();
        emRel.getItems().addAll("Spouse", "Parent", "Child", "Sibling", "Friend");
        emRel.setStyle(getTextFieldStyle()); emRel.setPrefHeight(40);

        Button btnSaveEm = createPrimaryButton("💾 Save Contact");
        Label emMsg = new Label();

        // Load existing using empController
        try {
            List<FamilyDetails> fam = empController.getFamilyDetails();
            if (fam != null && !fam.isEmpty()) {
                emName.setText(fam.get(0).getName());
                emRel.setValue(fam.get(0).getRelationship());
                emContact.setText(fam.get(0).getContact());
            }
        } catch(Exception e) { /* ignore */ }

        btnSaveEm.setOnAction(e -> {
            // Use empController here with separate arguments
            if(empController.saveFamilyDetail(currentUser.getId(), emName.getText(), emRel.getValue(), emContact.getText())) {
                emMsg.setText("✓ Emergency contact saved");
                emMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + ";");
            } else {
                emMsg.setText("✗ Failed to save");
                emMsg.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
            }
        });

        GridPane emGrid = new GridPane();
        emGrid.setHgap(15); emGrid.setVgap(15);
        emGrid.addRow(0, createLabel("Name"), emName);
        emGrid.addRow(1, createLabel("Relationship"), emRel);
        emGrid.addRow(2, createLabel("Phone"), emContact);

        emCard.getChildren().addAll(createLabel("🚨 Emergency Contact"), emGrid, btnSaveEm, emMsg);
        emBox.getChildren().add(emCard);
        tabPane.getTabs().add(new Tab("🚨 Emergency", emBox));

        // --- ADMIN SECTION ---
        if (currentUser.getRole().equalsIgnoreCase("admin") ||
                currentUser.getDepartment().equalsIgnoreCase("HR")) {

            // [TAB 2: REGISTER]
            VBox registerBox = new VBox(20);
            registerBox.setPadding(new Insets(30));
            registerBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

            VBox registerCard = new VBox(15);
            registerCard.setPadding(new Insets(30));
            registerCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; "
                    + "-fx-border-radius: 12; -fx-background-radius: 12;");

            TextField fName = createTextField("First Name");
            TextField lName = createTextField("Last Name");
            TextField phoneField = createTextField("Phone Number");
            TextField icNum = createTextField("Passport/IC Number");
            TextField deptField = createTextField("Department");
            TextField posField = createTextField("Position");

            Button btnRegister = createPrimaryButton("✅ Register Employee");
            Label regMsg = new Label();
            regMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");

            btnRegister.setOnAction(e -> {
                try {
                    boolean success = controller.registerEmployees(
                            fName.getText().trim(), lName.getText().trim(), phoneField.getText().trim(),
                            icNum.getText().trim(), deptField.getText().trim(), posField.getText().trim()
                    );
                    if(success) {
                        regMsg.setText("✓ Employee registered successfully");
                        regMsg.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-size: 12;");
                        fName.clear(); lName.clear(); phoneField.clear(); icNum.clear(); deptField.clear(); posField.clear();
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
                    fName, lName, phoneField, icNum, deptField, posField, btnRegister, regMsg
            );
            registerBox.getChildren().add(registerCard);
            tabPane.getTabs().add(new Tab("👥 Register", registerBox));


            // [TAB 3: DIRECTORY]
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

            TableColumn<Employee, String> colPhone = new TableColumn<>("Phone");
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            colPhone.setPrefWidth(100);

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

            table.getColumns().addAll(colId, colName, colLast, colPhone, colDept, colRole, colEmail, colSalary);

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
                dialog.setHeaderText("Editing: " + selected.getFirstName());
                ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(15); grid.setVgap(15); grid.setPadding(new Insets(20));

                TextField deptInput = createTextField(selected.getDepartment());
                TextField posInput = createTextField(selected.getPosition());
                TextField salaryInput = createTextField(String.valueOf(selected.getSalary()));

                grid.add(createLabel("Department:"), 0, 0); grid.add(deptInput, 1, 0);
                grid.add(createLabel("Position:"), 0, 1); grid.add(posInput, 1, 1);
                grid.add(createLabel("Salary:"), 0, 2); grid.add(salaryInput, 1, 2);
                dialog.getDialogPane().setContent(grid);

                dialog.showAndWait().ifPresent(response -> {
                    if (response == saveType) {
                        try {
                            double newSalary = Double.parseDouble(salaryInput.getText());
                            if (controller.updateEmployeeStatus(String.valueOf(selected.getId()), deptInput.getText(), posInput.getText(), newSalary)) {
                                new Alert(Alert.AlertType.INFORMATION, "Updated!").show();
                                btnReset.fire();
                            }
                        } catch (Exception ex) {
                            new Alert(Alert.AlertType.ERROR, "Invalid input!").show();
                        }
                    }
                });
            });

            HBox actionButtons = new HBox(10, btnEdit, btnFire);
            actionButtons.setPadding(new Insets(15));

            btnSearch.setOnAction(e -> table.setItems(FXCollections.observableArrayList(controller.searchProfile(searchField.getText()))));
            btnReset.setOnAction(e -> { searchField.clear(); table.setItems(FXCollections.observableArrayList(controller.searchProfile(""))); }); //table.setItems(FXCollections.observableArrayList(controller.getAllEmployees())); });
            btnReset.fire();

            listBox.getChildren().addAll(searchCard, table, actionButtons);
            tabPane.getTabs().add(new Tab("📋 Directory", listBox));


            // [TAB 4: LEAVE REQUESTS]
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
            colStart.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate().toString()));
            colStart.setPrefWidth(120);

            TableColumn<Leave, String> colEnd = new TableColumn<>("End Date");
            colEnd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndDate().toString()));
            colEnd.setPrefWidth(120);

            TableColumn<Leave, Integer> colDays = new TableColumn<>("Days");
            colDays.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalDays()));
            colDays.setPrefWidth(50);

            TableColumn<Leave, String> colReason = new TableColumn<>("Reason");
            colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
            colReason.setPrefWidth(200);

            TableColumn<Leave, String> colStatus = new TableColumn<>("Status");
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colStatus.setPrefWidth(100);
            colStatus.setCellFactory(col -> new StatusCell());

            leaveTable.getColumns().addAll(colLId, colEmpId, colStart, colEnd, colDays, colReason, colStatus);

            Button btnRefreshLeaves = createSecondaryButton("🔄 Refresh");
            Button btnApprove = new Button("✅ Approve"); btnApprove.setStyle(getSuccessButtonStyle());
            Button btnReject = new Button("❌ Reject"); btnReject.setStyle(getDangerButtonStyle());

            btnRefreshLeaves.setOnAction(e -> leaveTable.setItems(FXCollections.observableArrayList(controller.getAllPendingLeaves())));
            btnApprove.setOnAction(e -> {
                Leave sel = leaveTable.getSelectionModel().getSelectedItem();
                if (sel != null && controller.approveLeave(String.valueOf(sel.getLeaveId()), "Approved")) {
                    new Alert(Alert.AlertType.INFORMATION, "Leave Approved!").show(); btnRefreshLeaves.fire();
                }
            });
            btnReject.setOnAction(e -> {
                Leave sel = leaveTable.getSelectionModel().getSelectedItem();
                if (sel != null && controller.approveLeave(String.valueOf(sel.getLeaveId()), "Rejected")) {
                    new Alert(Alert.AlertType.INFORMATION, "Leave Rejected!").show(); btnRefreshLeaves.fire();
                }
            });

            HBox leaveActions = new HBox(10, btnApprove, btnReject, btnRefreshLeaves);
            leaveActions.setPadding(new Insets(15));
            btnRefreshLeaves.fire();

            leaveBox.getChildren().addAll(createLabel("📅 Pending Leave Requests"), leaveTable, leaveActions);
            tabPane.getTabs().add(new Tab("📅 Leave Requests", leaveBox));
        }


        // [TAB 5: REPORTS]
        VBox reportBox = new VBox(20);
        reportBox.setPadding(new Insets(30));
        reportBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        VBox reportCard = new VBox(15);
        reportCard.setPadding(new Insets(30));
        reportCard.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #E0E0E0; -fx-border-radius: 12;");

        Label lblReportHeader = createLabel("📊 Generate Annual Leave Report");
        HBox inputRow = new HBox(15);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextField txtReportId = createTextField("Employee ID"); txtReportId.setPrefWidth(150);
        TextField txtReportYear = createTextField("2024"); txtReportYear.setPrefWidth(150);
        Button btnGenerate = createPrimaryButton("📈 Generate Report");
        inputRow.getChildren().addAll(new Label("Employee ID:"), txtReportId, new Label("Year:"), txtReportYear, btnGenerate);

        TextArea reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setPromptText("Report details will appear here...");
        reportOutput.setPrefHeight(400);
        reportOutput.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");

        btnGenerate.setOnAction(e -> {
            try {
                int year = Integer.parseInt(txtReportYear.getText().trim());
                Report report = controller.generateReport(txtReportId.getText().trim(), year);
                if (report != null && report.getEmployeeProfile() != null) {
                    Employee emp = report.getEmployeeProfile();

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
                                    "  Phone No.  : %s\n" +
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
                            emp.getPhoneNumber(),
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
                    reportOutput.setText("No data found.");
                }
            } catch (Exception ex) { reportOutput.setText("Error: " + ex.getMessage()); }
        });

        reportCard.getChildren().addAll(lblReportHeader, inputRow, reportOutput);
        reportBox.getChildren().add(reportCard);
        tabPane.getTabs().add(new Tab("📊 Reports", reportBox));

        // --- ROOT LAYOUT (With Header) ---
        BorderPane root = new BorderPane();
        root.setTop(header()); // <--- The new Header with Logout
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("🏢 HRM Dashboard - " + currentUser.getFirstName());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // ---HEADER METHOD ---
    private HBox header() {
        Label headerName = new Label("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        headerName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        headerName.setTextFill(javafx.scene.paint.Color.web("#212121"));

        Label headerBalance = new Label("📊 Leave Balance: " + currentUser.getLeaveBalance() + " days");
        headerBalance.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        headerBalance.setTextFill(javafx.scene.paint.Color.web(PRIMARY_COLOR));

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle(getOutlineButtonStyle());
        logoutBtn.setOnAction(e -> {
            ((Stage) logoutBtn.getScene().getWindow()).close();
            try { new LoginGUI().start(new Stage()); } catch (Exception ex) { ex.printStackTrace(); }
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

    // --- STYLES ---
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

    private String getOutlineButtonStyle() {
        return "-fx-padding: 10 20 10 20; -fx-font-size: 12; "
                + "-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; "
                + "-fx-border-color: " + PRIMARY_COLOR + "; -fx-border-width: 1.5; "
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