
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

@WebServlet("/hod/college-info")
public class HODCollegeInfoServlet extends HttpServlet {

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

        if (!"HOD".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied.");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection()) {

            String context = request.getContextPath();
            String editId = request.getParameter("edit");

            out.println("<!DOCTYPE html><html><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>College Information | CampusSaathi</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;}");
            out.println("body{margin:0;padding:25px;font-family:Arial,sans-serif;background:#f4f3ff;color:#282447;}");
            out.println(".container{max-width:1050px;margin:auto;}");
            out.println(".header{background:linear-gradient(135deg,#6c5ce7,#8e7bff);color:white;padding:28px;border-radius:16px;margin-bottom:22px;}");
            out.println(".header h1{margin-top:0;}");
            out.println(".panel{background:white;padding:24px;border-radius:14px;margin-bottom:24px;box-shadow:0 4px 15px #0000000d;}");
            out.println("label{display:block;font-weight:bold;margin:14px 0 7px;}");
            out.println("input,textarea{width:100%;padding:12px;border:1px solid #ddd;border-radius:8px;font-size:15px;}");
            out.println("textarea{min-height:120px;resize:vertical;}");
            out.println(".btn{display:inline-block;border:0;background:#6c5ce7;color:white;text-decoration:none;padding:11px 16px;border-radius:8px;margin:10px 5px 0 0;cursor:pointer;font-size:14px;}");
            out.println(".cancel{background:#777;}");
            out.println(".delete{background:#dc3545;}");
            out.println(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:18px;}");
            out.println(".card{background:white;padding:22px;border-radius:14px;box-shadow:0 4px 15px #0000000d;border-top:4px solid #8e7bff;overflow-wrap:anywhere;}");
            out.println(".category{font-size:12px;text-transform:uppercase;color:#6c5ce7;font-weight:bold;letter-spacing:1px;}");
            out.println(".card h2{font-size:20px;margin:12px 0;}");
            out.println(".description{color:#666;line-height:1.7;white-space:pre-wrap;}");
            out.println(".notice{background:#e9e5ff;padding:12px;border-radius:8px;margin-bottom:18px;}");
            out.println("</style></head><body><div class='container'>");

            out.println("<div class='header'>");
            out.println("<h1>College Information</h1>");
            out.println("<p>Add, edit, and manage important college information.</p>");
            out.println("</div>");

            out.println("<a class='btn' href='" + context
                    + "/hod/dashboard'>Back to HOD Dashboard</a>");

            if ("success".equals(request.getParameter("status"))) {
                out.println("<div class='notice'>Information updated successfully.</div>");
            }

            int idToEdit = -1;
            String category = "";
            String title = "";
            String description = "";

            if (editId != null) {
                try {
                    idToEdit = Integer.parseInt(editId);

                    String editSql = "SELECT category,title,description "
                            + "FROM college_info WHERE info_id=?";

                    try (PreparedStatement ps = con.prepareStatement(editSql)) {
                        ps.setInt(1, idToEdit);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                category = rs.getString("category");
                                title = rs.getString("title");
                                description = rs.getString("description");
                            } else {
                                idToEdit = -1;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    idToEdit = -1;
                }
            }

            out.println("<div class='panel'>");
            out.println("<h2>" + (idToEdit > 0
                    ? "Edit College Information" : "Add College Information") + "</h2>");

            out.println("<form method='post' action='" + context
                    + "/hod/college-info'>");

            out.println("<input type='hidden' name='action' value='"
                    + (idToEdit > 0 ? "update" : "add") + "'>");

            if (idToEdit > 0) {
                out.println("<input type='hidden' name='infoId' value='"
                        + idToEdit + "'>");
            }

            out.println("<label>Category</label>");
            out.println("<input name='category' maxlength='100' required value='"
                    + escapeHtml(category) + "'>");

            out.println("<label>Title</label>");
            out.println("<input name='title' maxlength='200' required value='"
                    + escapeHtml(title) + "'>");

            out.println("<label>Description</label>");
            out.println("<textarea name='description' required>"
                    + escapeHtml(description) + "</textarea>");

            out.println("<button class='btn' type='submit'>"
                    + (idToEdit > 0 ? "Save Changes" : "Add Information")
                    + "</button>");

            if (idToEdit > 0) {
                out.println("<a class='btn cancel' href='" + context
                        + "/hod/college-info'>Cancel</a>");
            }

            out.println("</form></div>");

            out.println("<h2>Existing Information</h2>");
            out.println("<div class='grid'>");

            String sql = "SELECT info_id,category,title,description "
                    + "FROM college_info ORDER BY category,title";

            boolean found = false;

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    found = true;
                    int infoId = rs.getInt("info_id");

                    out.println("<div class='card'>");
                    out.println("<div class='category'>"
                            + escapeHtml(rs.getString("category")) + "</div>");
                    out.println("<h2>" + escapeHtml(rs.getString("title")) + "</h2>");
                    out.println("<div class='description'>"
                            + escapeHtml(rs.getString("description")) + "</div>");

                    out.println("<a class='btn' href='" + context
                            + "/hod/college-info?edit=" + infoId + "'>Edit</a>");

                    out.println("<form method='post' action='" + context
                            + "/hod/college-info' style='display:inline;' "
                            + "onsubmit=\"return confirm('Delete this information?');\">");

                    out.println("<input type='hidden' name='action' value='delete'>");
                    out.println("<input type='hidden' name='infoId' value='"
                            + infoId + "'>");
                    out.println("<button class='btn delete' type='submit'>Delete</button>");
                    out.println("</form>");

                    out.println("</div>");
                }
            }

            out.println("</div>");

            if (!found) {
                out.println("<div class='panel'>No college information has been added yet.</div>");
            }

            out.println("</div></body></html>");

        } catch (Exception e) {
            throw new ServletException("Unable to load college information.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"HOD".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Access denied.");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String context = request.getContextPath();

        try (Connection con = DBConnection.getConnection()) {

            if ("add".equals(action) || "update".equals(action)) {

                String category = request.getParameter("category");
                String title = request.getParameter("title");
                String description = request.getParameter("description");

                if (category == null || title == null || description == null
                        || category.trim().isEmpty()
                        || title.trim().isEmpty()
                        || description.trim().isEmpty()) {

                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "All fields are required.");
                    return;
                }

                category = category.trim();
                title = title.trim();
                description = description.trim();

                if (category.length() > 100 || title.length() > 200) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Category or title is too long.");
                    return;
                }

                if ("add".equals(action)) {

                    String sql = "INSERT INTO college_info "
                            + "(category,title,description) VALUES (?,?,?)";

                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, category);
                        ps.setString(2, title);
                        ps.setString(3, description);
                        ps.executeUpdate();
                    }

                } else {

                    int infoId;

                    try {
                        infoId = Integer.parseInt(request.getParameter("infoId"));
                    } catch (Exception e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "Invalid information ID.");
                        return;
                    }

                    String sql = "UPDATE college_info SET category=?,title=?,"
                            + "description=? WHERE info_id=?";

                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, category);
                        ps.setString(2, title);
                        ps.setString(3, description);
                        ps.setInt(4, infoId);
                        ps.executeUpdate();
                    }
                }

            } else if ("delete".equals(action)) {

                int infoId;

                try {
                    infoId = Integer.parseInt(request.getParameter("infoId"));
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid information ID.");
                    return;
                }

                String sql = "DELETE FROM college_info WHERE info_id=?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, infoId);
                    ps.executeUpdate();
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid action.");
                return;
            }

            response.sendRedirect(context + "/hod/college-info?status=success");

        } catch (Exception e) {
            throw new ServletException("Unable to save college information.", e);
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

