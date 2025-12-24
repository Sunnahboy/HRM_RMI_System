package com.hrmrmi.client.gui.components;
import com.hrmrmi.common.model.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ReportPanel extends VBox {
    private final ObservableList<Report> reportData;

    public ReportPanel() {
        setSpacing(10);
        setPadding(new Insets(20));

        Label title = new Label("Employee Report");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Report> reportTable = new TableView<>();
        reportData = FXCollections.observableArrayList();
        reportTable.setItems(reportData);

        TableColumn<Report, String> employeeIdCol =
                new TableColumn<>("Employee ID");
        employeeIdCol.setCellValueFactory(
                new PropertyValueFactory<>("employeeId")
        );

        TableColumn<Report, String> employeeNameCol =
                new TableColumn<>("Employee Name");
        employeeNameCol.setCellValueFactory(
                new PropertyValueFactory<>("employeeName")
        );

        TableColumn<Report, Integer> totalLeavesCol =
                new TableColumn<>("Leaves Taken");
        totalLeavesCol.setCellValueFactory(
                new PropertyValueFactory<>("totalLeavesTaken")
        );

        TableColumn<Report, Integer> remainingCol =
                new TableColumn<>("Remaining Leave");
        remainingCol.setCellValueFactory(
                new PropertyValueFactory<>("remainingLeaveBalance")
        );

        TableColumn<Report, LocalDate> dateCol =
                new TableColumn<>("Generated On");
        dateCol.setCellValueFactory(
                new PropertyValueFactory<>("generatedDate")
        );

        reportTable.getColumns().addAll(
                employeeIdCol,
                employeeNameCol,
                totalLeavesCol,
                remainingCol,
                dateCol
        );

        reportTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        getChildren().addAll(title, reportTable);
    }

    // ----------------------
    // Public API
    // ----------------------

    public void setReport(Report report) {
        reportData.clear();
        if (report != null) {
            reportData.add(report);
        }
    }

    public void clear() {
        reportData.clear();
    }

}
