package controller;

import dao.CategoryDAO;
import dao.TaskDAO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.*;
import model.*;

import java.time.LocalDate;
import java.util.List;

public class MainController {
    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> titleColumn, categoryColumn, dueColumn, priorityColumn, overdueColumn;
    @FXML private TableColumn<Task, Boolean> completedColumn;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button addBtn, editBtn, deleteBtn, markCompleteBtn;

    private final TaskDAO taskDAO = new TaskDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup TableView columns
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        categoryColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCategory() != null ? c.getValue().getCategory().getName() : ""));
        dueColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDueDate() != null ? c.getValue().getDueDate().toString() : ""));
        priorityColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPriority() != null ? c.getValue().getPriority().toString() : ""));
        completedColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCompleted()));
        overdueColumn.setCellValueFactory(c -> new SimpleStringProperty(
                (!c.getValue().isCompleted() && c.getValue().getDueDate() != null && c.getValue().getDueDate().isBefore(LocalDate.now()))
                        ? "⚠" : ""
        ));

        // Overdue row styling
        taskTableView.setRowFactory(tv -> new TableRow<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty) {
                    setStyle("");
                } else if (!task.isCompleted() && task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffe0e0;"); // Light red for overdue
                } else {
                    setStyle("");
                }
            }
        });

        // Populate category ComboBox
        categoryList.setAll(categoryDAO.getAllCategories());
        categoryComboBox.setItems(categoryList);
        if (!categoryList.isEmpty()) categoryComboBox.setValue(categoryList.get(0));

        categoryComboBox.setOnAction(e -> filterTasks());

        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList("All", "Pending", "Completed"));
        statusFilterCombo.setValue("All");
        statusFilterCombo.setOnAction(e -> filterTasks());

        filterTasks();
    }

    private void refreshCategories() {
        Category prev = categoryComboBox.getValue();
        categoryList.setAll(categoryDAO.getAllCategories());
        // Try to restore selection
        if (prev != null) {
            Category stillExists = categoryList.stream()
                    .filter(c -> c.getId() == prev.getId())
                    .findFirst()
                    .orElse(null);
            if (stillExists != null) {
                categoryComboBox.setValue(stillExists);
                return;
            }
        }
        if (!categoryList.isEmpty())
            categoryComboBox.setValue(categoryList.get(0));
    }

    private void filterTasks() {
        Category selectedCategory = categoryComboBox.getValue();
        String status = statusFilterCombo.getValue();

        List<Task> filtered = taskDAO.getAllTasks().stream()
                .filter(t -> selectedCategory == null || (t.getCategory() != null && t.getCategory().getId() == selectedCategory.getId()))
                .filter(t -> {
                    if ("Pending".equals(status)) return !t.isCompleted();
                    if ("Completed".equals(status)) return t.isCompleted();
                    return true;
                })
                .sorted((a, b) -> b.getPriority().compareTo(a.getPriority())) // High-to-Low sort
                .toList();
        taskTableView.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void handleAddTask(ActionEvent e) {
        showTaskDialog(null);
    }

    @FXML
    public void handleEditTask(ActionEvent e) {
        Task selected = taskTableView.getSelectionModel().getSelectedItem();
        if (selected != null)
            showTaskDialog(selected);
    }

    @FXML
    public void handleDeleteTask(ActionEvent e) {
        Task selected = taskTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected task?", ButtonType.YES, ButtonType.NO);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                taskDAO.deleteTask(selected);
                filterTasks();
            }
        }
    }

    @FXML
    public void handleMarkComplete(ActionEvent e) {
        Task selected = taskTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setCompleted(!selected.isCompleted());
            taskDAO.updateTask(selected);
            filterTasks();
        }
    }

    private void showTaskDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(task == null ? "Add Task" : "Edit Task");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(taskTableView.getScene() != null ? taskTableView.getScene().getWindow() : null);
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            TaskDialogController controller = loader.getController();
            controller.setTask(task, categoryDAO.getAllCategories());

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                if (task == null)
                    taskDAO.addTask(controller.getTask());
                else
                    taskDAO.updateTask(controller.getTask());
                refreshCategories();
                filterTasks();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}




// package controller;

// import dao.CategoryDAO;
// import dao.TaskDAO;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.fxml.FXMLLoader;
// import javafx.stage.*;
// import model.*;

// import java.util.List;

// public class MainController {
//     @FXML private ListView<Task> taskListView;
//     @FXML private ComboBox<Category> categoryComboBox;
//     @FXML private Label titleLabel, descLabel, dueLabel, priorityLabel, completedLabel, categoryLabel;
//     @FXML private Button addBtn, editBtn, deleteBtn, markCompleteBtn;

//     private final TaskDAO taskDAO = new TaskDAO();
//     private final CategoryDAO categoryDAO = new CategoryDAO();
//     private ObservableList<Task> taskList;
//     private ObservableList<Category> categoryList;

//     @FXML
//     public void initialize() {
//         // Load categories from database
//         categoryList = FXCollections.observableArrayList(categoryDAO.getAllCategories());
//         categoryComboBox.setItems(categoryList);

//         // Select first by default
//         if (!categoryList.isEmpty())
//             categoryComboBox.setValue(categoryList.get(0));

//         // Initial load/filter of tasks based on selected category
//         filterTasksByCategory();

//         // Show task details when selection changes
//         taskListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showTaskDetails(newVal));

//         // Filter tasks when category selection changes
//         categoryComboBox.setOnAction(e -> filterTasksByCategory());
//     }

//     private void refreshCategories() {
//         List<Category> updated = categoryDAO.getAllCategories();
//         categoryList.setAll(updated);

//         // Keep current category if possible, else select first
//         Category current = categoryComboBox.getValue();
//         if (current != null) {
//             for (Category c : categoryList) {
//                 if (c.getId() == current.getId()) {
//                     categoryComboBox.setValue(c);
//                     return;
//                 }
//             }
//         }
//         if (!categoryList.isEmpty()) {
//             categoryComboBox.setValue(categoryList.get(0));
//         }
//     }

//     private void filterTasksByCategory() {
//         Category selectedCategory = categoryComboBox.getValue();
//         // Defensive: just in case
//         if (selectedCategory == null) {
//             taskListView.setItems(FXCollections.observableArrayList());
//             return;
//         }
//         // Query and filter tasks
//         List<Task> filtered = taskDAO.getAllTasks().stream()
//             .filter(t -> t.getCategory() != null && t.getCategory().getId() == selectedCategory.getId())
//             .toList();
//         if (taskList == null) {
//             taskList = FXCollections.observableArrayList(filtered);
//             taskListView.setItems(taskList);
//         } else {
//             taskList.setAll(filtered);
//         }
//     }

//     private void showTaskDetails(Task t) {
//         if (t == null) {
//             titleLabel.setText("");
//             descLabel.setText("");
//             dueLabel.setText("");
//             priorityLabel.setText("");
//             completedLabel.setText("");
//             categoryLabel.setText("");
//             return;
//         }
//         titleLabel.setText(t.getTitle());
//         descLabel.setText(t.getDescription());
//         dueLabel.setText(t.getDueDate() != null ? t.getDueDate().toString() : "");
//         priorityLabel.setText(t.getPriority() != null ? t.getPriority().toString() : "");
//         completedLabel.setText(t.isCompleted() ? "Yes" : "No");
//         categoryLabel.setText(t.getCategory() != null ? t.getCategory().getName() : "");
//     }

//     @FXML
//     public void handleAddTask(ActionEvent e) {
//         showTaskDialog(null);
//     }

//     @FXML
//     public void handleEditTask(ActionEvent e) {
//         Task selected = taskListView.getSelectionModel().getSelectedItem();
//         if (selected != null) showTaskDialog(selected);
//     }

//     @FXML
//     public void handleDeleteTask(ActionEvent e) {
//         Task selected = taskListView.getSelectionModel().getSelectedItem();
//         if (selected != null) {
//             taskDAO.deleteTask(selected);
//             filterTasksByCategory();
//         }
//     }

//     @FXML
//     public void handleMarkComplete(ActionEvent e) {
//         Task selected = taskListView.getSelectionModel().getSelectedItem();
//         if (selected != null) {
//             selected.setCompleted(true);
//             taskDAO.updateTask(selected);
//             filterTasksByCategory();
//         }
//     }

//     private void showTaskDialog(Task task) {
//         try {
//             FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskDialog.fxml"));
//             Stage dialogStage = new Stage();
//             dialogStage.setTitle(task == null ? "Add Task" : "Edit Task");
//             dialogStage.initModality(Modality.WINDOW_MODAL);
//             dialogStage.initOwner(taskListView.getScene().getWindow());
//             Scene scene = new Scene(loader.load());
//             dialogStage.setScene(scene);

//             TaskDialogController controller = loader.getController();
//             controller.setTask(task, categoryDAO.getAllCategories());

//             dialogStage.showAndWait();

//             if (controller.isOkClicked()) {
//                 if (task == null)
//                     taskDAO.addTask(controller.getTask());
//                 else
//                     taskDAO.updateTask(controller.getTask());

//                 // Reload category list after possible modifications
//                 refreshCategories();
//                 // Filter tasks for the (possibly new) selection
//                 filterTasksByCategory();
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
//     }
// }
