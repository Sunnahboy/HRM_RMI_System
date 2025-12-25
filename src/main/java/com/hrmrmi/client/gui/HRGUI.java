package com.hrmrmi.client.gui;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.util.Config;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.rmi.Naming;
import java.util.List;

public class HRGUI extends Application {
    private HRMService service;
    private Employee currentUser;

    public HRGUI() { this.currentUser = null; }
    public HRGUI(Employee user) { this.currentUser = user; }

    @Override
    public void start(Stage primaryStage) {
        // 1. CONNECT
        try {
            String url = "rmi://" + Config.RMI_HOST + ":" + Config.RMI_PORT + "/" + Config.RMI_NAME;
            service = (HRMService) Naming.lookup(url);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Connection Failed: " + e.getMessage()).showAndWait();
            return;
        }

        TabPane tabPane = new TabPane();

        // --- TAB 1: MY PROFILE (Visible to Everyone) ---
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


        // --- ADMIN SECTION (Visible only to HR/Admin) ---
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
                    boolean success = service.registerEmployees(
                            fName.getText(), lName.getText(), icNum.getText(),
                            deptField.getText(), posField.getText()
                    );
                    if(success) {
                        new Alert(Alert.AlertType.INFORMATION, "Success!").show();
                        // Clear fields
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

            // A. Search Bar
            HBox searchBox = new HBox(10);
            TextField searchField = new TextField();
            searchField.setPromptText("Search Name or Dept...");
            Button btnSearch = new Button("Search");
            Button btnReset = new Button("Reset");

            searchBox.getChildren().addAll(searchField, btnSearch, btnReset);

            // B. The Table
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

            // Added Salary Column so you can verify changes later
            TableColumn<Employee, Double> colSalary = new TableColumn<>("Salary");
            colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

            table.getColumns().addAll(colId, colName, colLast, colDept, colRole, colEmail, colSalary);

            // C. FIRE n edit BUTTON
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
                    try {
                        if (service.fireEmployee(String.valueOf(selected.getId()), reason)) {
                            new Alert(Alert.AlertType.INFORMATION, "Terminated.").show();
                            table.getItems().remove(selected);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            });

            HBox actionButtons = new HBox(10);

            //edit button
            Button btnEdit = new Button("Edit Details / Promote");
            btnEdit.setOnAction(e -> {
                Employee selected = table.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Select an employee first.").show();
                    return;
                }

                //custom dialog
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Update Employee Status");
                dialog.setHeaderText("Editing: " + selected.getFirstName());

                ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                //layout
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

                            boolean done = service.updateEmployeeStatus(
                                    String.valueOf(selected.getId()),
                            deptInput.getText(),
                            posInput.getText(),
                            newSalary
                            );

                            if (done) {
                                new Alert(Alert.AlertType.INFORMATION, "Updated!").show();
                                btnReset.fire();
                            }
                            else {
                                new Alert(Alert.AlertType.ERROR, "Update failed!").show();
                            }
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Invalid Salary!").show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            });

            // D. Button Logic (Search & Reset)
            btnSearch.setOnAction(e -> {
                try {
                    List<Employee> results = service.searchProfile(searchField.getText());
                    table.setItems(FXCollections.observableArrayList(results));
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            btnReset.setOnAction(e -> {
                try {
                    searchField.clear();
                    List<Employee> all = service.getAllEmployees();
                    table.setItems(FXCollections.observableArrayList(all));
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            // Initial Load
            btnReset.fire();

            actionButtons.getChildren().addAll(btnEdit, btnFire);

            // Add everything to List Box
            listBox.getChildren().addAll(new Label("Employee Directory"), searchBox, table, actionButtons);

            // Add Tab
            Tab listTab = new Tab("Directory", listBox);
            listTab.setClosable(false);
            tabPane.getTabs().add(listTab);
        }

        Scene scene = new Scene(tabPane, 800, 500);
        primaryStage.setTitle("HRM Dashboard - " + currentUser.getFirstName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}