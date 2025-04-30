package model;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private PriorityLevel priority;
    private boolean completed;
    private Category category;

    public Task(int id, String title, String desc, LocalDate dueDate, PriorityLevel priority, boolean completed, Category category) {
        this.id = id;
        this.title = title;
        this.description = desc;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        this.category = category;
    }
    // Getters and setters...
    // (generate with IDE or as needed)
    // toString (for debugging)
    @Override
    public String toString() {
        return title + " [" + priority + "]" + (completed ? " (Done)" : "");
    }

    // Getters and setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}