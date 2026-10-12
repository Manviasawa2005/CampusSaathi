
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

@WebServlet("/college-info")
public class CollegeInfoServlet extends HttpServlet {

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

        Object roleObject = session.getAttribute("role");

        if (roleObject == null
                || !("TEACHER".equals(roleObject)
                || "STUDENT".equals(roleObject))) {

            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Only teachers and students can access this page.");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection()) {

            String context = request.getContextPath();

            out.println("<!DOCTYPE html><html><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>College Information | CampusSaathi</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;}");
            out.println("body{margin:0;padding:25px;font-family:Arial,sans-serif;background:#f4f3ff;color:#282447;}");
            out.println(".container{max-width:1000px;margin:auto;}");
            out.println(".header{background:linear-gradient(135deg,#6c5ce7,#8e7bff);color:white;padding:28px;border-radius:16px;margin-bottom:22px;}");
            out.println(".header h1{margin-top:0;}");
            out.println(".btn{display:inline-block;background:#6c5ce7;color:white;text-decoration:none;padding:12px 18px;border-radius:8px;margin-bottom:20px;}");
            out.println(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:18px;}");
            out.println(".card{background:white;padding:22px;border-radius:14px;box-shadow:0 4px 15px #0000000d;border-top:4px solid #8e7bff;overflow-wrap:anywhere;}");
            out.println(".category{font-size:12px;text-transform:uppercase;color:#6c5ce7;font-weight:bold;letter-spacing:1px;}");
            out.println(".card h2{font-size:20px;margin:12px 0;}");
            out.println(".description{color:#666;line-height:1.7;white-space:pre-wrap;}");
            out.println(".empty{background:white;padding:25px;border-radius:12px;}");
            out.println("</style></head><body><div class='container'>");

            out.println("<div class='header'>");
            out.println("<h1>College Information</h1>");
            out.println("<p>Important college guidelines, placement information and updates.</p>");
            out.println("</div>");

            String dashboard = "TEACHER".equals(roleObject)
                    ? "/teacher/dashboard"
                    : "/student/dashboard";

            out.println("<a class='btn' href='" + context + dashboard
                    + "'>Back to Dashboard</a>");

            out.println("<div class='grid'>");

            String sql = "SELECT category,title,description "
                    + "FROM college_info ORDER BY category,title";

            boolean found = false;

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    found = true;

                    out.println("<div class='card'>");
                    out.println("<div class='category'>"
                            + escapeHtml(rs.getString("category")) + "</div>");
                    out.println("<h2>" + escapeHtml(rs.getString("title")) + "</h2>");
                    out.println("<div class='description'>"
                            + escapeHtml(rs.getString("description")) + "</div>");
                    out.println("</div>");
                }
            }

            out.println("</div>");

            if (!found) {
                out.println("<div class='empty'>");
                out.println("<h3>No college information available</h3>");
                out.println("<p>Please check again later.</p>");
                out.println("</div>");
            }

            out.println("</div></body></html>");

        } catch (Exception e) {
            throw new ServletException("Unable to load college information.", e);
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

