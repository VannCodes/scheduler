package com.vincent.cpu.gui;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainView extends BorderPane {
    private final ObservableList<ProcessIn> processData = FXCollections.observableArrayList();
    private final TableView<ProcessIn> table = new TableView<>(processData);
    private final ComboBox<String> algoBox = new ComboBox<>();
    private final Button addRowBtn = new Button("Add Process");
    private final Button removeRowBtn = new Button("Remove Selected");
    private final Button runBtn = new Button("Run");
    private final TextArea resultArea = new TextArea();
    private final GanttChartView ganttChart = new GanttChartView();
    private final TableView<JobResult> resultTable = new TableView<>();
    private final Label avgLabel = new Label();
    private final ObjectMapper mapper = new ObjectMapper();

    public MainView() {
        setPadding(new Insets(20));
        setTop(createToolbar());
        setCenter(createProcessTable());
        
        resultArea.setPrefHeight(100);
        resultArea.setEditable(false);

        Label ganttLabel = new Label("Gantt Chart (animated):");
        Label calcLabel = new Label("Process Calculations (CT, TAT, WT, etc):");
        ganttLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 5 0 5 0;");
        calcLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 5 0 5 0;");
        
        // Create a container for the average label to make it more prominent
        VBox avgContainer = new VBox(8);
        Label avgTitle = new Label("Summary:");
        avgTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        avgContainer.getChildren().addAll(avgTitle, avgLabel);
        avgContainer.setPadding(new Insets(15, 20, 15, 20));
        avgContainer.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10px; -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        avgContainer.setMinHeight(80); // Ensure it has minimum height
        
        VBox bottomBox = new VBox(12,
            createActionBar(),
            ganttLabel,
            ganttChart,
            calcLabel,
            resultTable,
            avgContainer
        );
        bottomBox.setPadding(new Insets(15, 0, 20, 0));
        
        // Wrap bottomBox in a ScrollPane to enable scrolling
        ScrollPane scrollPane = new ScrollPane(bottomBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0));
        scrollPane.setPrefViewportHeight(350); // Set preferred viewport height to enable scrolling
        scrollPane.setMinViewportHeight(300); // Minimum height
        scrollPane.setPrefHeight(350); // Preferred height for the ScrollPane itself
        scrollPane.setMaxHeight(500); // Maximum height before scrolling is needed
        
        setBottom(scrollPane);
        
        // Ensure avgLabel is visible and properly styled
        avgLabel.setText("No results yet. Click Run to see averages."); // Initialize with placeholder text
        System.out.println("avgLabel initialized, visible: " + avgLabel.isVisible());

        runBtn.setOnAction(e -> onRunClicked());
        runBtn.getStyleClass().add("run-button");
        initResultTable();
        
        // Apply modern styling to components
        applyModernStyling();
    }
    
    private void applyModernStyling() {
        // Style labels - make it very visible
        avgLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 5 0 5 0;");
        avgLabel.setWrapText(true); // Allow text to wrap if needed
        avgLabel.setMaxWidth(Double.MAX_VALUE); // Allow full width
        avgLabel.setMinHeight(40); // Ensure minimum height
        avgLabel.setVisible(true); // Make sure it's visible
        avgLabel.setManaged(true); // Ensure it's managed by layout
    }

    private HBox createToolbar() {
        algoBox.getItems().addAll("FCFS", "SJF", "SJF Preemptive", "Priority", "Priority Preemptive", "Round Robin");
        algoBox.getSelectionModel().selectFirst();
        
        // Clear results and process table when algorithm selection changes
        algoBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null && newVal != null && !oldVal.equals(newVal)) {
                clearResults();
                clearProcessTable();
            }
        });
        
        Label algoLabel = new Label("Algorithm:");
        algoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        HBox hbox = new HBox(12, algoLabel, algoBox);
        hbox.setPadding(new Insets(15));
        hbox.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 10px; -fx-padding: 15;");
        return hbox;
    }
    
    private void clearResults() {
        ganttChart.setEntries(null);
        resultTable.setItems(FXCollections.observableArrayList());
        avgLabel.setText("No results yet. Click Run to see averages.");
    }
    
    private void clearProcessTable() {
        processData.clear();
    }

    private VBox createProcessTable() {
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(35); // larger row height - this makes table auto-size based on rows
        table.setMinHeight(100); // minimum height for empty table
        // Don't set prefHeight - let it calculate from fixedCellSize * rowCount
        table.setMaxHeight(700); // Max height to prevent it from taking over the whole screen
        table.setMaxWidth(Double.MAX_VALUE);
        // Remove VBox.setVgrow to let table size itself based on content
        Label placeholder = new Label("Add a process...");
        placeholder.setStyle("-fx-text-fill: #999; -fx-font-size: 13px;");
        table.setPlaceholder(placeholder);

        TableColumn<ProcessIn, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().id));
        idCol.setEditable(false);
        idCol.setPrefWidth(150);
        idCol.setMinWidth(100);

        TableColumn<ProcessIn, String> btCol = new TableColumn<>("Burst Time");
        btCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().bt)));
        btCol.setCellFactory(TextFieldTableCell.forTableColumn());
        btCol.setOnEditCommit(e -> {
            try {
                int v = Integer.parseInt(e.getNewValue());
                if (v < 1) throw new NumberFormatException();
                e.getRowValue().bt = v;
            } catch (NumberFormatException ex) {
                showAlert("Burst Time must be a positive integer.");
                table.refresh();
            }
        });
        btCol.setPrefWidth(150);
        btCol.setMinWidth(100);

        TableColumn<ProcessIn, String> atCol = new TableColumn<>("Arrival Time");
        atCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().at != null ? cell.getValue().at : 0)));
        atCol.setCellFactory(TextFieldTableCell.forTableColumn());
        atCol.setOnEditCommit(e -> {
            try {
                int v = Integer.parseInt(e.getNewValue());
                if (v < 0) throw new NumberFormatException();
                e.getRowValue().at = v;
            } catch (NumberFormatException ex) {
                showAlert("Arrival Time must be a non-negative integer.");
                table.refresh();
            }
        });
        atCol.setPrefWidth(150);
        atCol.setMinWidth(100);

        TableColumn<ProcessIn, String> prioCol = new TableColumn<>("Priority");
        prioCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().priority != null ? cell.getValue().priority : 0)));
        prioCol.setCellFactory(TextFieldTableCell.forTableColumn());
        prioCol.setOnEditCommit(e -> {
            try {
                int v = Integer.parseInt(e.getNewValue());
                if (v < 0) throw new NumberFormatException();
                e.getRowValue().priority = v;
            } catch (NumberFormatException ex) {
                showAlert("Priority must be a non-negative integer.");
                table.refresh();
            }
        });
        prioCol.setPrefWidth(150);
        prioCol.setMinWidth(100);

        table.getColumns().setAll(idCol, btCol, atCol, prioCol);
        Label processesLabel = new Label("Processes");
        processesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 8 0;");
        HBox buttonBox = new HBox(10, addRowBtn, removeRowBtn);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        VBox box = new VBox(10, processesLabel, table, buttonBox);
        box.setPadding(new Insets(15, 0, 15, 0));
        // Don't use VBox.setVgrow - let table size itself
        addRowBtn.setOnAction(e -> {
            int nextNum = processData.size() + 1;
            processData.add(new ProcessIn("P" + nextNum, 1, 0, 0));
        });
        removeRowBtn.setOnAction(e -> {
            ProcessIn sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) processData.remove(sel);
        });
        return box;
    }

    private HBox createActionBar() {
        HBox box = new HBox(10, runBtn);
        box.setPadding(new Insets(10, 0, 0, 0));
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return box;
    }

    private void initResultTable() {
        TableColumn<JobResult, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().id));
        TableColumn<JobResult, Number> btCol = new TableColumn<>("BT");
        btCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().bt));
        TableColumn<JobResult, Number> atCol = new TableColumn<>("AT");
        atCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().at));
        TableColumn<JobResult, Number> ctCol = new TableColumn<>("CT");
        ctCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().ct));
        TableColumn<JobResult, Number> tatCol = new TableColumn<>("TAT");
        tatCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().tat));
        TableColumn<JobResult, Number> wtCol = new TableColumn<>("WT");
        wtCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().wt));
        TableColumn<JobResult, Number> prioCol = new TableColumn<>("Priority");
        prioCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().priority != null ? cell.getValue().priority : 0));
        resultTable.getColumns().setAll(idCol, btCol, atCol, ctCol, tatCol, wtCol, prioCol);
        resultTable.setPrefHeight(180);
    }

    private void onRunClicked() {
        if (processData.isEmpty()) {
            showAlert("Please add at least one process.");
            return;
        }
        // Validate all processes have required fields
        for (ProcessIn p : processData) {
            if (p.id == null || p.id.trim().isEmpty()) {
                showAlert("All processes must have an ID.");
                return;
            }
            if (p.bt < 1) {
                showAlert("All processes must have a Burst Time of at least 1.");
                return;
            }
        }
        String selectedAlgo = algoBox.getValue();
        String algoString = mapAlgoToRequest(selectedAlgo);
        Integer quantum = null;
        if (algoString.equals("rr")) {
            TextInputDialog d = new TextInputDialog("2");
            d.setTitle("Quantum");
            d.setHeaderText("Provide Quantum for RR");
            d.setContentText("Quantum value:");
            d.showAndWait();
            try { quantum = Integer.parseInt(d.getEditor().getText()); }
            catch (Exception e) { showAlert("Invalid quantum"); return; }
        }
        var processes = processData.stream().map(p -> new ProcessIn(p.id, p.bt, p.at != null ? p.at : 0, p.priority != null ? p.priority : 0)).collect(Collectors.toList());
        RunRequest req = new RunRequest(algoString, processes, quantum);
        clearResults(); // clear results before running
        new Thread(() -> {
            try {
                RunResponse resp = ApiHelper.runSchedule(req);
                javafx.application.Platform.runLater(() -> {
                    ganttChart.setEntries(resp.gantt);
                    resultTable.setItems(FXCollections.observableList(resp.jobs));
                    double avgCombined = (resp.avg_tat + resp.avg_wt) / 2.0;
                    String labelText = String.format("Average TAT: %.2f  |  Average WT: %.2f  |  Average (TAT+WT)/2: %.2f", 
                        resp.avg_tat, resp.avg_wt, avgCombined);
                    avgLabel.setText(labelText);
                    avgLabel.setVisible(true);
                    System.out.println("Setting avgLabel text: " + labelText); // Debug
                    System.out.println("avgLabel visible: " + avgLabel.isVisible() + ", text: " + avgLabel.getText());
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    clearResults();
                    String errorMsg = ex.getMessage();
                    // Avoid double "Backend error:" prefix
                    if (errorMsg != null && errorMsg.startsWith("Backend error: ")) {
                        showAlert(errorMsg);
                    } else if (errorMsg != null && errorMsg.startsWith("Cannot connect")) {
                        showAlert(errorMsg);
                    } else {
                        showAlert("Backend error: " + (errorMsg != null ? errorMsg : ex.getClass().getSimpleName()));
                    }
                });
            }
        }).start();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
    private String mapAlgoToRequest(String display) {
        return switch(display) {
            case "FCFS" -> "fcfs";
            case "SJF" -> "sjf";
            case "SJF Preemptive" -> "sjf_preemptive";
            case "Priority" -> "priority";
            case "Priority Preemptive" -> "priority_preemptive";
            case "Round Robin" -> "rr";
            default -> "fcfs";
        };
    }
}
