// package controller;

// import javafx.collections.FXCollections;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.*;
// import javafx.stage.Stage;
// import model.*;

// import java.time.LocalDate;
// import java.util.List;

// public class TaskDialogController {
//     @FXML private TextField titleField;
//     @FXML private TextArea descField;
//     @FXML private DatePicker duePicker;
//     @FXML private ComboBox<PriorityLevel> priorityCombo;
//     @FXML private ComboBox<Category> categoryCombo;
//     @FXML private CheckBox completedBox;

//     private Task task;
//     private boolean okClicked = false;

//     public void setTask(Task task, List<Category> categories) {
//         categoryCombo.setItems(FXCollections.observableArrayList(categories));
//         priorityCombo.setItems(FXCollections.observableArrayList(PriorityLevel.values()));

//         if (task != null) {
//             this.task = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getPriority(), task.isCompleted(), task.getCategory());
//             titleField.setText(task.getTitle());
//             descField.setText(task.getDescription());
//             duePicker.setValue(task.getDueDate());
//             priorityCombo.setValue(task.getPriority());
//             completedBox.setSelected(task.isCompleted());
//             if (task.getCategory() != null)  categoryCombo.setValue(task.getCategory());
//         } else {
//             this.task = new Task(0, "", "", null, PriorityLevel.LOW, false, categoryCombo.getItems().get(0));
//             priorityCombo.setValue(PriorityLevel.LOW);
//             categoryCombo.setValue(categoryCombo.getItems().get(0));
//         }
//     }

//     @FXML
//     public void handleOk(ActionEvent e) {
//         task.setTitle(titleField.getText());
//         task.setDescription(descField.getText());
//         task.setDueDate(duePicker.getValue());
//         task.setPriority(priorityCombo.getValue());
//         task.setCompleted(completedBox.isSelected());
//         task.setCategory(categoryCombo.getValue());
//         okClicked = true;
//         ((Stage) titleField.getScene().getWindow()).close();
//     }

//     @FXML
//     public void handleCancel(ActionEvent e) {
//         ((Stage) titleField.getScene().getWindow()).close();
//     }

//     public boolean isOkClicked() { return okClicked; }
//     public Task getTask() { return task; }
// }

package controller;

import dao.CategoryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

import java.util.List;
import java.util.Optional;

public class TaskDialogController {
    @FXML private TextField titleField;
    @FXML private TextArea descField;
    @FXML private DatePicker duePicker;
    @FXML private ComboBox<PriorityLevel> priorityCombo;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private CheckBox completedBox;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private Task task;
    private boolean okClicked = false;

    public void setTask(Task task, List<Category> categories) {
        reloadCategories(null);

        priorityCombo.setItems(FXCollections.observableArrayList(PriorityLevel.values()));
        if (task != null) {
            this.task = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getPriority(), task.isCompleted(), task.getCategory());
            titleField.setText(task.getTitle());
            descField.setText(task.getDescription());
            duePicker.setValue(task.getDueDate());
            priorityCombo.setValue(task.getPriority());
            completedBox.setSelected(task.isCompleted());
            if (task.getCategory() != null)  categoryCombo.setValue(task.getCategory());
        } else {
            this.task = new Task(0, "", "", null, PriorityLevel.LOW, false, categoryCombo.getItems().isEmpty() ? null : categoryCombo.getItems().get(0));
            priorityCombo.setValue(PriorityLevel.LOW);
            if (!categoryCombo.getItems().isEmpty()) categoryCombo.setValue(categoryCombo.getItems().get(0));
        }
    }

    @FXML
    public void handleOk(ActionEvent e) {
        task.setTitle(titleField.getText());
        task.setDescription(descField.getText());
        task.setDueDate(duePicker.getValue());
        task.setPriority(priorityCombo.getValue());
        task.setCompleted(completedBox.isSelected());
        task.setCategory(categoryCombo.getValue());
        okClicked = true;
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    public void handleCancel(ActionEvent e) {
        ((Stage) titleField.getScene().getWindow()).close();
    }

    @FXML
    private void handleAddCategory(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Category");
        dialog.setHeaderText("Create a new category:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            String catName = name.trim();
            if (!catName.isEmpty()) {
                boolean exists = categoryCombo.getItems().stream()
                        .anyMatch(c -> c.getName().equalsIgnoreCase(catName));
                if (!exists) {
                    Category newCategory = new Category(0,catName);
                    categoryDAO.addCategory(newCategory);

                    reloadCategories(catName);
                } else {
                    showError("That category already exists.");
                }
            } else {
                showError("Category name cannot be empty.");
            }
        });
    }

    private void reloadCategories(String selectName) {
        ObservableList<Category> categories = FXCollections.observableArrayList(categoryDAO.getAllCategories());
        categoryCombo.setItems(categories);
        if (selectName != null) {
            categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(selectName))
                    .findFirst()
                    .ifPresent(c -> categoryCombo.setValue(c));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isOkClicked() { return okClicked; }
    public Task getTask() { return task; }
}