
package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/csv-template")
public class AdminCsvTemplateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Only the College Administrator can download the CSV template."
            );
            return;
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"campussaathi_accounts_template.csv\""
        );
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {

            // Required column headings
            out.println("Full Name,Email,Role,Initial Password,Batch");

            // Example student accounts: replace these sample details before importing
            out.println("Aarav Sharma,aarav@example.com,STUDENT,,AI&DS-2024");
            out.println("Priya Verma,priya@example.com,STUDENT,,AI&DS-2024");

            // Non-student accounts can leave Batch blank
            out.println("Rahul Singh,rahul@example.com,TEACHER,,");
            out.println("Neha Gupta,neha@example.com,HOD,,");
            out.println("Club Leader,club@example.com,CLUB_LEADER,,");

        }
    }
}

