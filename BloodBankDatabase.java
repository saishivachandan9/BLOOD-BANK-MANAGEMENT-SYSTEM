import java.sql.*;

public class BloodBankDatabase {
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/new_schema";
    static final String USERNAME = "root";
    static final String PASSWORD = "@Saishiva2004";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int insertDonor(String name, String bloodGroup) {
        String sql = "INSERT INTO donors (name, blood_group) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, bloodGroup);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the generated user ID
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if user ID retrieval fails
    }

    public static ResultSet getAllDonors() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT id, blood_group FROM donors;");
    }

    public static boolean removeDonor(int userId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            // Retrieve donor information before removal
            String selectDonorSQL = "SELECT name, blood_group FROM donors WHERE id = ?";
            PreparedStatement selectDonorStatement = connection.prepareStatement(selectDonorSQL);
            selectDonorStatement.setInt(1, userId);
            ResultSet donorResultSet = selectDonorStatement.executeQuery();

            if (donorResultSet.next()) {
                String donorName = donorResultSet.getString("name");
                String bloodGroup = donorResultSet.getString("blood_group");

                // Insert the donor information into the history table
                String insertHistorySQL = "INSERT INTO donor_history (id, name, blood_group) VALUES (?, ?, ?)";
                PreparedStatement insertHistoryStatement = connection.prepareStatement(insertHistorySQL);
                insertHistoryStatement.setInt(1, userId);
                insertHistoryStatement.setString(2, donorName);
                insertHistoryStatement.setString(3, bloodGroup);
                insertHistoryStatement.executeUpdate();
            }

            // Remove the donor from the main donors table
            String removeDonorSQL = "DELETE FROM donors WHERE id = ?";
            PreparedStatement removeDonorStatement = connection.prepareStatement(removeDonorSQL);
            removeDonorStatement.setInt(1, userId);
            int rowsAffected = removeDonorStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add a method to retrieve donor history
    public static ResultSet getDonorHistory() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT id, name, blood_group FROM donor_history;");
    }

    public static ResultSet getBloodGroupCount() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT blood_group, COUNT(*) as count FROM donors GROUP BY blood_group;");
    }
}
