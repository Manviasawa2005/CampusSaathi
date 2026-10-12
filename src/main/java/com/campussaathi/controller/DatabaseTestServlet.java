package com.campussaathi.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dbtest")
public class DatabaseTestServlet extends HttpServlet {

private static final long serialVersionUID = 1L;

@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws ServletException, IOException {

    response.setContentType("text/html;charset=UTF-8");

    try (Connection connection = DBConnection.getConnection();
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(
                 "SELECT COUNT(*) FROM college_info")) {

        resultSet.next();

        int totalRecords = resultSet.getInt(1);

        response.getWriter().println(
            "<h1>CampusSaathi Database Connected!</h1>" +
            "<p>Java backend successfully connected to MySQL.</p>" +
            "<p>College information records: " + totalRecords + "</p>"
        );

    } catch (Exception e) {

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        response.getWriter().println(
            "<h1>Database Connection Failed</h1>" +
            "<p>Error: " + e.getMessage() + "</p>"
        );

        e.printStackTrace();
    }
}


}
