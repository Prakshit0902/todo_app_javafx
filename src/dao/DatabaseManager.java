package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:todoapp.db";
    
    static {
        // Initialize schema on load
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS category (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS task (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "dueDate TEXT," +
                    "priority TEXT," +
                    "completed INTEGER," +
                    "category_id INTEGER," +
                    "FOREIGN KEY (category_id) REFERENCES category (id))");
            // Insert default category if not exists
            stmt.execute("INSERT OR IGNORE INTO category (id, name) VALUES (1, 'General')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}