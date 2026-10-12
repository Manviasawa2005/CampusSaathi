
package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/teacher/announcements")
public class TeacherAnnouncementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || !"TEACHER".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html><head><meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Teacher Announcements | CampusSaathi</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;margin:0;padding:0;}");
            out.println("body{font-family:Arial,sans-serif;background:#f4f5fb;color:#25243a;padding:25px;}");
            out.println(".container{max-width:850px;margin:25px auto;}");
            out.println(".header{background:linear-gradient(135deg,#5546d8,#8274ed);color:white;padding:28px;border-radius:18px;margin-bottom:24px;}");
            out.println(".header h1{margin-bottom:8px;font-size:28px;}");
            out.println(".card{background:white;padding:28px;border-radius:16px;box-shadow:0 5px 22px rgba(35,30,90,.08);margin-bottom:22px;}");
            out.println("label{display:block;font-weight:bold;margin:18px 0 8px;}");
            out.println("input,textarea,select{width:100%;padding:12px;border:1px solid #d9d8e8;border-radius:8px;font-size:15px;}");
            out.println("textarea{min-height:130px;resize:vertical;}");
            out.println("button,.btn{display:inline-block;border:0;background:#5a4bdb;color:white;padding:12px 20px;border-radius:8px;font-size:15px;text-decoration:none;cursor:pointer;margin-top:20px;}");
            out.println(".btn-secondary{background:#e9e7fb;color:#4437b5;margin-left:8px;}");
            out.println(".note{background:#f0efff;padding:12px;border-radius:8px;color:#4b438e;margin-top:12px;font-size:14px;}");
            out.println(".message{padding:12px;border-radius:8px;margin-bottom:15px;background:#e4f7e9;color:#176b32;}");
            out.println(".error{background:#ffe8e8;color:#a32323;}");
            out.println(".item{padding:18px 0;border-bottom:1px solid #ecebf4;}");
            out.println(".item:last-child{border-bottom:0;}");
            out.println(".meta{font-size:13px;color:#777;margin:9px 0;}");
            out.println(".announcement-message{white-space:pre-wrap;overflow-wrap:anywhere;line-height:1.5;}");
            out.println("@media(max-width:600px){body{padding:12px}.card{padding:18px}.header h1{font-size:23px}.btn-secondary{margin-left:0;}}");
            out.println("</style></head><body>");

            out.println("<div class='container'>");
            out.println("<div class='header'>");
            out.println("<h1>Teacher Announcements</h1>");
            out.println("<p>Share important updates with all students or a specific batch.</p>");
            out.println("</div>");

            String context = request.getContextPath();
            String status = request.getParameter("status");

            if ("success".equals(status)) {
                out.println("<div class='message'>Announcement published successfully!</div>");
            } else if ("invalid".equals(status)) {
                out.println("<div class='message error'>Please enter a valid title and message, and select a valid audience.</div>");
            } else if ("error".equals(status)) {
                out.println("<div class='message error'>The announcement could not be published. Please try again.</div>");
            }

            out.println("<div class='card'><h2>Publish an Announcement</h2>");
            out.println("<form method='post' action='" + context + "/teacher/announcements'>");

            out.println("<label for='title'>Announcement Title</label>");
            out.println("<input id='title' name='title' type='text' maxlength='200' required>");

            out.println("<label for='message'>Announcement Message</label>");
            out.println("<textarea id='message' name='message' maxlength='10000' required></textarea>");

            out.println("<label for='audience'>Send To</label>");
            out.println("<select id='audience' name='audience' required onchange='toggleBatch()'>");
            out.println("<option value='ALL_STUDENTS'>All Students</option>");
            out.println("<option value='SPECIFIC_CLASS'>Specific Batch</option>");
            out.println("</select>");

            out.println("<div id='batchSection' style='display:none;'>");
            out.println("<label for='targetClass'>Select Student Batch</label>");
            out.println("<select id='targetClass' name='targetClass'>");

            boolean foundBatch = false;

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "SELECT DISTINCT batch FROM users "
                     + "WHERE role = 'STUDENT' AND status = 'ACTIVE' "
                     + "AND batch IS NOT NULL AND TRIM(batch) <> '' "
                     + "ORDER BY batch");
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    String batch = result.getString("batch");
                    out.println("<option value='" + escapeHtml(batch) + "'>"
                            + escapeHtml(batch) + "</option>");
                    foundBatch = true;
                }

            } catch (SQLException e) {
                getServletContext().log("Unable to load student batches.", e);
                out.println("<option value=''>Unable to load batches</option>");
            }

            if (!foundBatch) {
                out.println("<option value=''>No student batches available</option>");
            }

            out.println("</select>");
            out.println("<div class='note'>Only active student accounts assigned to this batch will receive the announcement.</div>");
            out.println("</div>");

            out.println("<button type='submit'>Publish Announcement</button>");
            out.println("<a class='btn btn-secondary' href='" + context + "/teacher/dashboard'>Back to Dashboard</a>");
            out.println("</form></div>");

            out.println("<div class='card'><h2>Your Recent Announcements</h2>");

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                     "SELECT announcement_id, title, message, audience_type, "
                     + "target_class, created_at FROM announcements "
                     + "WHERE sender_id = ? "
                     + "AND audience_type IN ('ALL_STUDENTS','SPECIFIC_CLASS') "
                     + "ORDER BY created_at DESC LIMIT 20")) {

                statement.setInt(1, (Integer) session.getAttribute("userId"));

                try (ResultSet result = statement.executeQuery()) {
                    boolean hasAnnouncements = false;

                    while (result.next()) {
                        hasAnnouncements = true;

                        out.println("<div class='item'>");
                        out.println("<h3>" + escapeHtml(result.getString("title")) + "</h3>");

                        String audience = result.getString("audience_type");

                        if ("SPECIFIC_CLASS".equals(audience)) {
                            out.println("<p class='meta'>Audience: Batch "
                                    + escapeHtml(result.getString("target_class")) + "</p>");
                        } else {
                            out.println("<p class='meta'>Audience: All Students</p>");
                        }

                        out.println("<p class='announcement-message'>"
                                + escapeHtml(result.getString("message")) + "</p>");
                        out.println("<p class='meta'>Published: "
                                + escapeHtml(String.valueOf(result.getTimestamp("created_at")))
                                + "</p>");
                        out.println("</div>");
                    }

                    if (!hasAnnouncements) {
                        out.println("<p>You haven't published any student announcements yet.</p>");
                    }
                }

            } catch (SQLException e) {
                getServletContext().log("Unable to load teacher announcements.", e);
                out.println("<p>Unable to load announcement history right now.</p>");
            }

            out.println("</div></div>");

            out.println("<script>");
            out.println("function toggleBatch(){");
            out.println("var specific=document.getElementById('audience').value==='SPECIFIC_CLASS';");
            out.println("var section=document.getElementById('batchSection');");
            out.println("var batch=document.getElementById('targetClass');");
            out.println("section.style.display=specific?'block':'none';");
            out.println("batch.required=specific;");
            out.println("}");
            out.println("toggleBatch();");
            out.println("</script>");

            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || !"TEACHER".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String audience = request.getParameter("audience");
        String targetClass = request.getParameter("targetClass");

        if (title == null || message == null
                || title.trim().isEmpty()
                || message.trim().isEmpty()
                || title.trim().length() > 200
                || message.trim().length() > 10000) {
            response.sendRedirect(request.getContextPath()
                    + "/teacher/announcements?status=invalid");
            return;
        }

        title = title.trim();
        message = message.trim();

        if (!"ALL_STUDENTS".equals(audience)
                && !"SPECIFIC_CLASS".equals(audience)) {
            response.sendRedirect(request.getContextPath()
                    + "/teacher/announcements?status=invalid");
            return;
        }

        if ("SPECIFIC_CLASS".equals(audience)) {
            if (targetClass == null || targetClass.trim().isEmpty()
                    || targetClass.trim().length() > 50) {
                response.sendRedirect(request.getContextPath()
                        + "/teacher/announcements?status=invalid");
                return;
            }

            targetClass = targetClass.trim();
        } else {
            targetClass = null;
        }

        String sql = "INSERT INTO announcements "
                + "(title, message, sender_id, audience_type, target_class) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setString(2, message);
            statement.setInt(3, (Integer) session.getAttribute("userId"));
            statement.setString(4, audience);

            if (targetClass == null) {
                statement.setNull(5, java.sql.Types.VARCHAR);
            } else {
                statement.setString(5, targetClass);
            }

            int inserted = statement.executeUpdate();

            if (inserted == 1) {
                response.sendRedirect(request.getContextPath()
                        + "/teacher/announcements?status=success");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/teacher/announcements?status=error");
            }

        } catch (SQLException e) {
            getServletContext().log("Unable to publish teacher announcement.", e);
            response.sendRedirect(request.getContextPath()
                    + "/teacher/announcements?status=error");
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

