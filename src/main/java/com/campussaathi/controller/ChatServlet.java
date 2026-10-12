package com.campussaathi.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {


private static final long serialVersionUID = 1L;

@Override
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");
    response.setContentType("text/plain;charset=UTF-8");

    String message = request.getParameter("message");

    if (message == null || message.trim().isEmpty()) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Please enter a question.");
        return;
    }

    String question = message.trim().toLowerCase(Locale.ROOT);

    try {
        CollegeInfoService service = new CollegeInfoService();
        List<String> information = service.getAllInformation();

        StringBuilder answer = new StringBuilder();

        for (String record : information) {

            String recordText = record.toLowerCase(Locale.ROOT);

            if (question.contains("placement")
                    && recordText.contains("placement")) {

                answer.append(record).append("\n\n");

            } else if ((question.contains("assignment")
                    || question.contains("deadline"))
                    && recordText.contains("assignment")) {

                answer.append(record).append("\n\n");

            } else if ((question.contains("event")
                    || question.contains("hackathon"))
                    && recordText.contains("event")) {

                answer.append(record).append("\n\n");

            } else if (question.contains("attendance")
                    && recordText.contains("attendance")) {

                answer.append(record).append("\n\n");

            } else if ((question.contains("interview")
                    || question.contains("technical"))
                    && recordText.contains("interview")) {

                answer.append(record).append("\n\n");
            }
        }

        if (answer.length() > 0) {
            response.getWriter().write(answer.toString().trim());
        } else {
            response.getWriter().write(
                "I couldn't find a matching answer in my college " +
                "information database. Please try asking about " +
                "placements, assignments, events, attendance, " +
                "or interview preparation."
            );
        }

    } catch (Exception e) {
        getServletContext().log(
            "Error while processing a CampusSaathi question.", e
        );

        response.setStatus(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );

        response.getWriter().write(
            "Sorry, I couldn't retrieve college information right now. " +
            "Please try again."
        );
    }
}


}
