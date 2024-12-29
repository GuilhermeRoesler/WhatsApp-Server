package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;

import model.User;

public class UserDAO extends MySQLConnection {
    public UserDAO() {
        super();
    }

    public User createUser(ResultSet res) {
        try {
            User user = new User();
            user.setId(res.getInt("id"));
            user.setName(res.getString("name"));
            user.setPhone(res.getString("phone"));
            user.setPassword(res.getString("password"));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(User user) {
        String query = "INSERT INTO user VALUES (null, ?, ?, ?)";

        try {
            PreparedStatement st = db.prepareStatement(query);
            st.setString(1, user.getName());
            st.setString(2, user.getPassword());
            st.setString(3, user.getPhone());

            st.executeUpdate();
            System.out.println("User registered successfully");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("User with this phone number already exists");
            return false;
        } catch (SQLSyntaxErrorException e) {
            System.out.println("Database query syntax error");
            return false;
        } catch (SQLTimeoutException e) {
            System.out.println("Database connection timeout");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to register user");
            return false;
        }
    }

    public User login(User loginRequest) {
        String query = "SELECT * FROM user WHERE phone = ? AND password = ?";

        try {
            PreparedStatement st = db.prepareStatement(query);
            st.setString(1, loginRequest.getPhone());
            st.setString(2, loginRequest.getPassword());

            ResultSet res = st.executeQuery();

            if (res.next()) {
                System.out.println("Login successful");
                return createUser(res);
            } else {
                System.out.println("Login failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Login failed");
        }
        return null;
    }

    public boolean existsInDatabase(String phone) {
        String query = "SELECT * FROM user WHERE phone = ?";

        try {
            PreparedStatement st = db.prepareStatement(query);
            st.setString(1, phone);

            ResultSet res = st.executeQuery();

            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInDatabase(User user) {
        String query = "SELECT * FROM user WHERE phone = ?";

        try {
            PreparedStatement st = db.prepareStatement(query);
            st.setString(1, user.getPhone());

            ResultSet res = st.executeQuery();

            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByPhone(String phone) {
        String query = "SELECT * FROM user WHERE phone = ?";

        try {
            PreparedStatement st = db.prepareStatement(query);
            st.setString(1, phone);

            ResultSet res = st.executeQuery();
            if (res.next()) {
                return createUser(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
