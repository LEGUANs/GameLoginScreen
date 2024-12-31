import java.sql.*;

public class JDBC {
    private Connection connection;
    
    public JDBC() throws SQLException {
        try {
            loadJdbcDriver();
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user?serverTimezone=UTC", "root", "Hys011231");
        } catch (ClassNotFoundException e) {//                                        连数据库，改成自己的mysql用户
            System.out.println("JDBC driver not found.");
            throw new SQLException("JDBC driver not found.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            throw e;
        }
    }
    
    public void loadJdbcDriver() throws ClassNotFoundException {
        
        Class.forName("com.mysql.cj.jdbc.Driver");
    }
    
    public boolean registerUser(String username, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
        statement.setString(1, username);
        statement.setString(2, password);
        int rowsAffected = statement.executeUpdate();
        statement.close();
        return rowsAffected > 0;
    }

    public boolean verifyUser(String username, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();
        boolean userExists = resultSet.next();
        resultSet.close();
        statement.close();
        return userExists;
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}