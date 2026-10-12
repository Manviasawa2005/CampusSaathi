package com.campussaathi.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollegeInfoService {

public List<String> getAllInformation() throws SQLException {

    List<String> information = new ArrayList<>();

    String query =
            "SELECT category, title, description FROM college_info";

    try (Connection connection = DBConnection.getConnection();
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {

        while (resultSet.next()) {

            String category = resultSet.getString("category");
            String title = resultSet.getString("title");
            String description = resultSet.getString("description");

            String record =
                    "Category: " + category +
                    "\nTitle: " + title +
                    "\nInformation: " + description;

            information.add(record);
        }
    }

    return information;
}


}
