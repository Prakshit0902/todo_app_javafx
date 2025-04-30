package dao;

import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task";
        try (Connection conn = DatabaseManager.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = categoryDAO.findById(rs.getInt("category_id"));
                Task t = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("dueDate") == null ? null : LocalDate.parse(rs.getString("dueDate")),
                    rs.getString("priority") == null ? null : PriorityLevel.valueOf(rs.getString("priority")),
                    rs.getInt("completed") == 1,
                    cat
                );
                tasks.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tasks;
    }

    public void addTask(Task t) {
        String sql = "INSERT INTO task (title, description, dueDate, priority, completed, category_id) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getTitle());
            pstmt.setString(2, t.getDescription());
            pstmt.setString(3, t.getDueDate() == null ? null : t.getDueDate().toString());
            pstmt.setString(4, t.getPriority() == null ? null : t.getPriority().name());
            pstmt.setInt(5, t.isCompleted() ? 1 : 0);
            pstmt.setInt(6, t.getCategory() == null ? 1 : t.getCategory().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateTask(Task t) {
        String sql = "UPDATE task SET title=?, description=?, dueDate=?, priority=?, completed=?, category_id=? WHERE id=?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getTitle());
            pstmt.setString(2, t.getDescription());
            pstmt.setString(3, t.getDueDate() == null ? null : t.getDueDate().toString());
            pstmt.setString(4, t.getPriority() == null ? null : t.getPriority().name());
            pstmt.setInt(5, t.isCompleted() ? 1 : 0);
            pstmt.setInt(6, t.getCategory() == null ? 1 : t.getCategory().getId());
            pstmt.setInt(7, t.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteTask(Task t) {
        String sql = "DELETE FROM task WHERE id=?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, t.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}