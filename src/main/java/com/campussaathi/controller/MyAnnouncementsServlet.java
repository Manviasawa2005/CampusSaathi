
package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/my-announcements")
public class MyAnnouncementsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("role") == null
                || session.getAttribute("userId") == null) {

            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        Object userIdObject = session.getAttribute("userId");

        if (!(userIdObject instanceof Integer)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = (Integer) userIdObject;

        if (!"TEACHER".equals(role)
                && !"STUDENT".equals(role)
                && !"CLUB_LEADER".equals(role)) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "This page is for teachers, students, and club leaders."
            );
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        String contextPath = request.getContextPath();

        String sql =
            "SELECT a.title, a.message, a.audience_type, " +
            "a.created_at, sender.full_name AS sender_name " +
            "FROM announcements a " +
            "JOIN users sender ON a.sender_id = sender.user_id " +
            "WHERE ( " +

            // Teachers receive announcements for all teachers,
            // announcements directed to their account, and everyone.
            " (? = 'TEACHER' AND ( " +
            "     a.audience_type = 'ALL_TEACHERS' " +
            "     OR (a.audience_type = 'SPECIFIC_TEACHER' " +
            "         AND a.target_user_id = ?) " +
            "     OR a.audience_type = 'EVERYONE' " +
            " )) " +

            " OR " +

            // Students receive general student announcements,
            // announcements for everyone, and announcements
            // specifically targeted to their own batch.
            " (? = 'STUDENT' AND ( " +
            "     a.audience_type = 'ALL_STUDENTS' " +
            "     OR a.audience_type = 'EVERYONE' " +
            "     OR (a.audience_type = 'SPECIFIC_CLASS' " +
            "         AND a.target_class = (" +
            "             SELECT u.batch FROM users u " +
            "             WHERE u.user_id = ?" +
            "         )) " +
            " )) " +

            " OR " +

            // Club leaders receive club announcements and everyone.
            " (? = 'CLUB_LEADER' AND ( " +
            "     a.audience_type = 'ALL_CLUB_LEADERS' " +
            "     OR a.audience_type = 'EVERYONE' " +
            " )) " +

            ") " +
            "ORDER BY a.created_at DESC";

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>My Announcements - CampusSaathi</title>");

            out.println("<style>");

            out.println("*{box-sizing:border-box}");

            out.println(
                "body{margin:0;padding:30px;font-family:Arial,sans-serif;" +
                "background:#f5f3ff;color:#25213b}"
            );

            out.println(".container{max-width:900px;margin:auto}");

            out.println(
                ".card{background:white;padding:25px;margin-bottom:18px;" +
                "border-radius:14px;box-shadow:0 4px 18px rgba(91,33,182,.10)}"
            );

            out.println("h1{color:#5b21b6;margin-top:0}");

            out.println(
                ".notice{border:1px solid #e5def5;border-radius:10px;" +
                "padding:18px;margin-top:16px;overflow-wrap:anywhere}"
            );

            out.println(".notice h3{color:#5b21b6;margin-top:0}");

            out.println(
                ".meta{font-size:13px;color:#666;margin-top:15px;line-height:1.8}"
            );

            out.println(
                ".back{display:inline-block;margin-top:15px;color:#5b21b6;" +
                "font-weight:bold;text-decoration:none}"
            );

            out.println(".back:hover{text-decoration:underline}");

            out.println(
                ".empty{padding:15px;background:#f5f3ff;border-radius:8px}"
            );

            out.println(
                "@media(max-width:600px){body{padding:15px}.card{padding:18px}}"
            );

            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='card'>");
            out.println("<h1>📢 My Announcements</h1>");
            out.println("<p>Important updates intended for your role and batch.</p>");

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, role);
                ps.setInt(2, userId);
                ps.setString(3, role);
                ps.setInt(4, userId);
                ps.setString(5, role);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        out.println("<div class='notice'>");

                        out.println(
                            "<h3>" +
                            escapeHtml(rs.getString("title")) +
                            "</h3>"
                        );

                        String message = escapeHtml(
                            rs.getString("message")
                        );

                        message = message.replace("\r\n", "<br>")
                                         .replace("\n", "<br>");

                        out.println("<p>" + message + "</p>");

                        out.println("<div class='meta'>");

                        out.println(
                            "<strong>Audience:</strong> " +
                            escapeHtml(
                                audienceLabel(rs.getString("audience_type"))
                            )
                        );

                        out.println(
                            "<br><strong>Published by:</strong> " +
                            escapeHtml(rs.getString("sender_name"))
                        );

                        out.println(
                            "<br><strong>Date:</strong> " +
                            escapeHtml(
                                String.valueOf(rs.getTimestamp("created_at"))
                            )
                        );

                        out.println("</div>");
                        out.println("</div>");
                    }

                    if (!found) {
                        out.println(
                            "<p class='empty'>" +
                            "No announcements for you yet." +
                            "</p>"
                        );
                    }
                }

            } catch (Exception e) {

                log("Unable to load announcements.", e);

                out.println(
                    "<p class='empty'>" +
                    "Unable to load announcements. " +
                    "Please check the server log." +
                    "</p>"
                );
            }

            out.println("</div>");

            String dashboard;

            switch (role) {
                case "TEACHER":
                    dashboard = "/teacher/dashboard";
                    break;

                case "STUDENT":
                    dashboard = "/student/dashboard";
                    break;

                default:
                    dashboard = "/club/dashboard";
                    break;
            }

            out.println(
                "<a class='back' href='" +
                contextPath + dashboard +
                "'>← Back to Dashboard</a>"
            );

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String audienceLabel(String audience) {

        if (audience == null) {
            return "Unknown";
        }

        switch (audience) {
            case "ALL_TEACHERS":
                return "All Teachers";

            case "SPECIFIC_TEACHER":
                return "One Specific Teacher";

            case "ALL_STUDENTS":
                return "All Students";

            case "SPECIFIC_CLASS":
                return "Specific Class";

            case "ALL_CLUB_LEADERS":
                return "All Club Leaders";

            case "EVERYONE":
                return "Everyone";

            default:
                return audience;
        }
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}

