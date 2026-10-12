
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

@WebServlet("/teacher/classes")
public class TeacherClassesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"TEACHER".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied.");
            return;
        }

        int teacherId = ((Number) session.getAttribute("userId")).intValue();

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection()) {

            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>My Classes | CampusSaathi</title>");

            out.println("<style>");
            out.println("body{font-family:Arial,sans-serif;background:#f4f3ff;margin:0;padding:25px;color:#252344;}");
            out.println(".container{max-width:1000px;margin:auto;}");
            out.println(".header{background:#6c5ce7;color:white;padding:25px;border-radius:16px;margin-bottom:20px;}");
            out.println(".card{background:white;padding:22px;border-radius:14px;box-shadow:0 4px 15px #0000000d;overflow-x:auto;}");
            out.println("table{width:100%;border-collapse:collapse;min-width:650px;}");
            out.println("th,td{text-align:left;padding:13px;border-bottom:1px solid #eee;}");
            out.println("th{background:#f0edff;color:#5142b5;}");
            out.println(".btn{display:inline-block;background:#6c5ce7;color:white;text-decoration:none;padding:11px 17px;border-radius:8px;margin-bottom:18px;}");
            out.println(".empty{padding:25px;text-align:center;color:#777;}");
            out.println("</style></head><body><div class='container'>");

            out.println("<div class='header'><h1>My Classes</h1>");
            out.println("<p>View your assigned class schedule.</p></div>");

            out.println("<a class='btn' href='" + request.getContextPath()
                    + "/teacher/dashboard'>Back to Dashboard</a>");

            out.println("<div class='card'><h2>Class Schedule</h2>");

            String sql = "SELECT subject, batch, day_of_week, start_time, "
                    + "end_time, classroom FROM class_schedule "
                    + "WHERE teacher_id = ? "
                    + "ORDER BY FIELD(LOWER(day_of_week), "
                    + "'monday','tuesday','wednesday','thursday',"
                    + "'friday','saturday','sunday'), start_time";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, teacherId);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {
                        if (!found) {
                            out.println("<table><thead><tr>");
                            out.println("<th>Subject</th><th>Batch</th>");
                            out.println("<th>Day</th><th>Start Time</th>");
                            out.println("<th>End Time</th><th>Classroom</th>");
                            out.println("</tr></thead><tbody>");
                            found = true;
                        }

                        out.println("<tr>");
                        out.println("<td>" + escapeHtml(rs.getString("subject")) + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("batch")) + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("day_of_week")) + "</td>");
                        out.println("<td>" + rs.getTime("start_time") + "</td>");
                        out.println("<td>" + rs.getTime("end_time") + "</td>");
                        out.println("<td>" + escapeHtml(rs.getString("classroom")) + "</td>");
                        out.println("</tr>");
                    }

                    if (found) {
                        out.println("</tbody></table>");
                    } else {
                        out.println("<div class='empty'>");
                        out.println("<h3>No classes scheduled yet</h3>");
                        out.println("<p>Your class schedule will appear here once it has been added.</p>");
                        out.println("</div>");
                    }
                }
            }

            out.println("</div></div></body></html>");

        } catch (Exception e) {
            throw new ServletException("Unable to load class schedule.", e);
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

