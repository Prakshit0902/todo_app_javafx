    package dao;

    import model.Category;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class CategoryDAO {
        public List<Category> getAllCategories() {
            List<Category> categories = new ArrayList<>();
            String sql = "SELECT id, name FROM category";
            try (Connection conn = DatabaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    categories.add(new Category(rs.getInt("id"), rs.getString("name")));
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return categories;
        }

        public Category findById(int id) {
            String sql = "SELECT id, name FROM category WHERE id=?";
            try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) return new Category(rs.getInt("id"), rs.getString("name"));
            } catch (SQLException e) { e.printStackTrace(); }
            return null;
        }

        public void addCategory(Category c) {
            String sql = "INSERT INTO category (name) VALUES (?)";
            try (Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, c.getName());
                pstmt.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }