package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/hod/announcements")
public class HODAnnouncementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || !"HOD".equals(session.getAttribute("role"))) {
            response.sendRedirect(
                request.getContextPath() + "/login.jsp"
            );
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        String contextPath = request.getContextPath();

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>HOD Announcements - CampusSaathi</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;}");
            out.println("body{margin:0;padding:30px;font-family:Arial,sans-serif;background:#f5f3ff;color:#25213b;}");
            out.println(".container{max-width:1000px;margin:auto;}");
            out.println(".card{background:white;padding:28px;margin-bottom:25px;border-radius:16px;box-shadow:0 5px 20px rgba(40,20,80,.08);}");
            out.println("h1{color:#5b21b6;margin-top:0;}");
            out.println("h2{color:#4c1d95;}");
            out.println("label{display:block;font-weight:bold;margin:18px 0 8px;}");
            out.println("input,textarea,select{width:100%;padding:12px;border:1px solid #d8d1e8;border-radius:8px;font-size:15px;}");
            out.println("textarea{min-height:120px;resize:vertical;}");
            out.println("button{margin-top:20px;background:#6d28d9;color:white;border:0;padding:12px 22px;border-radius:8px;font-weight:bold;cursor:pointer;}");
            out.println("button:hover{background:#4c1d95;}");
            out.println(".success{background:#dcfce7;color:#166534;padding:12px;border-radius:8px;margin-bottom:18px;}");
            out.println(".error{background:#fee2e2;color:#991b1b;padding:12px;border-radius:8px;margin-bottom:18px;}");
            out.println(".announcement{border:1px solid #e5def5;border-radius:10px;padding:18px;margin-top:16px;}");
            out.println(".announcement h3{margin-top:0;color:#5b21b6;}");
            out.println(".meta{font-size:13px;color:#666;margin-top:12px;}");
            out.println(".back{display:inline-block;color:#5b21b6;text-decoration:none;font-weight:bold;margin-top:10px;}");
            out.println(".hint{color:#666;font-size:13px;}");
            out.println("@media(max-width:600px){body{padding:15px;}.card{padding:18px;}}");
            out.println("</style>");

            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            out.println("<div class='card'>");
            out.println("<h1>📢 HOD Announcements</h1>");
            out.println("<p>Publish important information for teachers, students, and club leaders.</p>");

            if ("true".equals(request.getParameter("published"))) {
                out.println("<div class='success'>Announcement published successfully!</div>");
            }

            if ("publish".equals(request.getParameter("error"))) {
                out.println("<div class='error'>Unable to publish the announcement. Please check your details and try again.</div>");
            }

            out.println("<form method='post' action='" + contextPath + "/hod/announcements'>");

            out.println("<label for='title'>Announcement Title</label>");
            out.println("<input type='text' id='title' name='title' maxlength='200' required>");

            out.println("<label for='message'>Message</label>");
            out.println("<textarea id='message' name='message' required></textarea>");

            out.println("<label for='audience'>Send Announcement To</label>");
            out.println("<select id='audience' name='audience' required>");
            out.println("<option value='ALL_TEACHERS'>All Teachers</option>");
            out.println("<option value='SPECIFIC_TEACHER'>One Specific Teacher</option>");
            out.println("<option value='ALL_STUDENTS'>All Students</option>");
            out.println("<option value='ALL_CLUB_LEADERS'>All Club Leaders</option>");
            out.println("</select>");

            out.println("<div id='teacherSection' style='display:none;'>");
            out.println("<label for='targetUserId'>Select Teacher</label>");
            out.println("<select id='targetUserId' name='targetUserId'>");
            out.println("<option value=''>Select a teacher</option>");

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     "SELECT user_id, full_name, email "
                   + "FROM users "
                   + "WHERE role = 'TEACHER' AND status = 'ACTIVE' "
                   + "ORDER BY full_name");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String fullName = rs.getString("full_name");
                    String email = rs.getString("email");

                    out.println(
                        "<option value='" + userId + "'>"
                        + escapeHtml(fullName)
                        + " (" + escapeHtml(email) + ")"
                        + "</option>"
                    );
                }

            } catch (SQLException e) {
                log("Unable to load teachers for HOD announcements.", e);
                out.println("<option value=''>Unable to load teachers</option>");
            }

            out.println("</select>");
            out.println("</div>");

            out.println("<button type='submit'>Publish Announcement</button>");
            out.println("</form>");
            out.println("</div>");

            out.println("<div class='card'>");
            out.println("<h2>Published Announcements</h2>");

            String historySql =
                "SELECT a.announcement_id, a.title, a.message, "
              + "a.audience_type, a.created_at, "
              + "sender.full_name AS sender_name, "
              + "target.full_name AS target_name "
              + "FROM announcements a "
              + "JOIN users sender ON a.sender_id = sender.user_id "
              + "LEFT JOIN users target ON a.target_user_id = target.user_id "
              + "WHERE a.sender_id = ? "
              + "ORDER BY a.created_at DESC";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(historySql)) {

                ps.setInt(1, (Integer) session.getAttribute("userId"));

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {
                        found = true;

                        out.println("<div class='announcement'>");
                        out.println("<h3>" + escapeHtml(rs.getString("title")) + "</h3>");
                        out.println("<p>" + escapeHtml(rs.getString("message"))
                            .replace("\n", "<br>") + "</p>");

                        String audience = rs.getString("audience_type");

                        out.println("<div class='meta'><strong>Audience:</strong> "
                            + escapeHtml(audienceLabel(audience)));

                        String targetName = rs.getString("target_name");

                        if ("SPECIFIC_TEACHER".equals(audience)
                                && targetName != null) {
                            out.println("<br><strong>Teacher:</strong> "
                                + escapeHtml(targetName));
                        }

                        out.println("<br><strong>Published by:</strong> "
                            + escapeHtml(rs.getString("sender_name")));

                        out.println("<br><strong>Date:</strong> "
                            + escapeHtml(String.valueOf(rs.getTimestamp("created_at"))));

                        out.println("</div>");
                        out.println("</div>");
                    }

                    if (!found) {
                        out.println("<p>No announcements published yet.</p>");
                    }
                }

            } catch (SQLException e) {
                log("Unable to load HOD announcement history.", e);
                out.println("<div class='error'>Unable to load announcement history. Please check the server console.</div>");
            }

            out.println("</div>");

            out.println("<a class='back' href='" + contextPath
                + "/hod/dashboard'>← Back to HOD Dashboard</a>");

            out.println("</div>");

            out.println("<script>");
            out.println("const audience = document.getElementById('audience');");
            out.println("const teacherSection = document.getElementById('teacherSection');");
            out.println("const targetUserId = document.getElementById('targetUserId');");

            out.println("function updateTeacherSelection(){");
            out.println(" const specific = audience.value === 'SPECIFIC_TEACHER';");
            out.println(" teacherSection.style.display = specific ? 'block' : 'none';");
            out.println(" targetUserId.required = specific;");
            out.println("}");

            out.println("audience.addEventListener('change', updateTeacherSelection);");
            out.println("updateTeacherSelection();");
            out.println("</script>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || !"HOD".equals(session.getAttribute("role"))) {
            response.sendRedirect(
                request.getContextPath() + "/login.jsp"
            );
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String audience = request.getParameter("audience");
        String targetUserIdText = request.getParameter("targetUserId");

        String redirectUrl =
            request.getContextPath() + "/hod/announcements";

        if (title == null || title.trim().isEmpty()
                || message == null || message.trim().isEmpty()
                || title.trim().length() > 200
                || audience == null) {

            response.sendRedirect(redirectUrl + "?error=publish");
            return;
        }

        title = title.trim();
        message = message.trim();

        boolean validAudience =
                "ALL_TEACHERS".equals(audience)
             || "SPECIFIC_TEACHER".equals(audience)
             || "ALL_STUDENTS".equals(audience)
             || "ALL_CLUB_LEADERS".equals(audience);

        if (!validAudience) {
            response.sendRedirect(redirectUrl + "?error=publish");
            return;
        }

        Integer targetUserId = null;

        if ("SPECIFIC_TEACHER".equals(audience)) {
            try {
                targetUserId = Integer.valueOf(targetUserIdText);

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                         "SELECT user_id FROM users "
                       + "WHERE user_id = ? "
                       + "AND role = 'TEACHER' "
                       + "AND status = 'ACTIVE'")) {

                    ps.setInt(1, targetUserId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            response.sendRedirect(redirectUrl + "?error=publish");
                            return;
                        }
                    }
                }

            } catch (NumberFormatException | SQLException e) {
                log("Invalid teacher selected for announcement.", e);
                response.sendRedirect(redirectUrl + "?error=publish");
                return;
            }
        }

        String insertSql =
            "INSERT INTO announcements "
          + "(title, message, sender_id, audience_type, target_user_id, target_class) "
          + "VALUES (?, ?, ?, ?, ?, NULL)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            ps.setString(1, title);
            ps.setString(2, message);
            ps.setInt(3, (Integer) session.getAttribute("userId"));
            ps.setString(4, audience);

            if (targetUserId != null) {
                ps.setInt(5, targetUserId);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted == 1) {
                response.sendRedirect(redirectUrl + "?published=true");
            } else {
                response.sendRedirect(redirectUrl + "?error=publish");
            }

        } catch (SQLException e) {
            log("Unable to publish HOD announcement.", e);
            response.sendRedirect(redirectUrl + "?error=publish");
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
            case "ALL_COLLEGE_ADMINS":
                return "All College Administrators";
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

