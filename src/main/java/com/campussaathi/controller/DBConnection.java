package com.campussaathi.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {


private static final String URL =
        "jdbc:mysql://localhost:3306/campussaathi_db";

private static final String USER = "root";

// Replace this with your actual MySQL password.
private static final String PASSWORD = "manvi";

public static Connection getConnection() throws SQLException {

    try {
        // Explicitly load the MySQL JDBC driver.
        Class.forName("com.mysql.cj.jdbc.Driver");

    } catch (ClassNotFoundException e) {
        throw new SQLException(
                "MySQL JDBC driver not found. Check whether " +
                "mysql-connector-j-26.7.0.jar is deployed correctly.",
                e
        );
    }

    // Connect Java to the CampusSaathi database.
    return DriverManager.getConnection(URL, USER, PASSWORD);
}


}
