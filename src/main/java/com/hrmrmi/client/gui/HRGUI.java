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
import javafx.scene.layout.VBox;
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
                        fName.clear();
                        lName.clear();
                        icNum.clear();
                        deptField.clear();
                        posField.clear();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed.").show();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            registerBox.getChildren().addAll(new Label("New Registration"), fName, lName, icNum, deptField, posField, btnRegister);
            Tab regTab = new Tab("Register", registerBox);
            regTab.setClosable(false);
            tabPane.getTabs().add(regTab);


            // --- TAB 3: EMPLOYEE DIRECTORY (The New Part!) ---
            VBox listBox = new VBox(10);
            listBox.setPadding(new Insets(10));

            // Create the Table
            TableView<Employee> table = new TableView<>();

            // Define Columns (Must match getters in Employee.java)
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

            table.getColumns().addAll(colId, colName, colLast, colDept, colRole, colEmail);

            // "Load Data" Button
            Button btnRefresh = new Button("Refresh List");
            btnRefresh.setOnAction(e -> {
                try {
                    // 1. Ask Server for the List
                    List<Employee> list = service.getAllEmployees();
                    // 2. Convert to JavaFX Format
                    ObservableList<Employee> fxList = FXCollections.observableArrayList(list);
                    // 3. Put into Table
                    table.setItems(fxList);
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            // Auto-load data when opening
            btnRefresh.fire();

            listBox.getChildren().addAll(new Label("All Employees"), btnRefresh, table);
            Tab listTab = new Tab("Directory", listBox);
            listTab.setClosable(false);
            tabPane.getTabs().add(listTab);
        }

        Scene scene = new Scene(tabPane, 750, 500); // Made window slightly wider
        primaryStage.setTitle("HRM Dashboard - " + currentUser.getFirstName());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}