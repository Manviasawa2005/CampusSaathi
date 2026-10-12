package com.campussaathi.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InitialUserSetup {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("==========================================");
            System.out.println(" CampusSaathi - Initial Administrator Setup");
            System.out.println("==========================================");

            System.out.print("Enter Administrator full name: ");
            String fullName = scanner.nextLine().trim();

            System.out.print("Enter Administrator email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter a strong password: ");
            String password = scanner.nextLine();

            if (fullName.isEmpty()
                    || fullName.length() > 100
                    || email.isEmpty()
                    || email.length() > 150
                    || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                    || password.length() < 12
                    || password.length() > 128) {

                System.out.println();
                System.out.println("Setup cancelled.");
                System.out.println("Enter valid details and use a password");
                System.out.println("between 12 and 128 characters.");
                return;
            }

            String checkSql =
                    "SELECT COUNT(*) FROM users WHERE role = 'COLLEGE_ADMIN'";

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement checkStatement =
                         connection.prepareStatement(checkSql);
                 ResultSet resultSet = checkStatement.executeQuery()) {

                resultSet.next();

                if (resultSet.getInt(1) > 0) {
                    System.out.println();
                    System.out.println(
                            "A College Administrator account already exists."
                    );
                    System.out.println("No account was created.");
                    return;
                }

                String passwordHash = PasswordUtil.hashPassword(password);

                String insertSql =
                        "INSERT INTO users "
                        + "(full_name, email, password_hash, role, status) "
                        + "VALUES (?, ?, ?, 'COLLEGE_ADMIN', 'ACTIVE')";

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(insertSql)) {

                    insertStatement.setString(1, fullName);
                    insertStatement.setString(2, email);
                    insertStatement.setString(3, passwordHash);

                    insertStatement.executeUpdate();

                    System.out.println();
                    System.out.println("SUCCESS!");
                    System.out.println(
                            "College Administrator account created."
                    );
                    System.out.println("Email: " + email);
                    System.out.println(
                            "You can now log in through the College Administrator portal."
                    );
                }

            } catch (SQLException e) {

                if ("23000".equals(e.getSQLState())) {
                    System.out.println();
                    System.out.println(
                            "An account with this email already exists."
                    );
                    System.out.println("Please use a different email address.");
                } else {
                    throw e;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Could not create the College Administrator account."
            );
            System.out.println("Reason: " + e.getMessage());
        }
    }
}