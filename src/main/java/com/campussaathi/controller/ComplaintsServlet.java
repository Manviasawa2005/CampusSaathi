
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

@WebServlet(urlPatterns = {
    "/hod/complaints",
    "/teacher/complaints",
    "/student/complaints",
    "/club/complaints"
})
public class ComplaintsServlet extends HttpServlet {

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

        String role = (String) session.getAttribute("role");
        String path = request.getServletPath();

        if (!isAuthorizedPath(path, role)) {
            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "You are not authorized to access this page."
            );
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            String contextPath = request.getContextPath();
            String fullName = (String) session.getAttribute("fullName");

            if (fullName == null) {
                fullName = "User";
            }

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>CampusSaathi | Complaints</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;}");
            out.println("body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#272343;}");
            out.println(".header{background:#5b3cc4;color:white;padding:22px 6%;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;}");
            out.println(".header h1{margin:0;font-size:26px;}");
            out.println(".header p{margin:6px 0 0;color:#e6deff;}");
            out.println(".header a{background:white;color:#5b3cc4;padding:10px 15px;border-radius:8px;text-decoration:none;font-weight:bold;}");
            out.println(".container{max-width:1050px;margin:32px auto;padding:0 18px;}");
            out.println(".panel{background:white;border:1px solid #e8e2fa;border-radius:14px;padding:24px;margin-bottom:24px;box-shadow:0 5px 18px rgba(50,30,100,.05);}");
            out.println("h2{margin-top:0;}");
            out.println("label{display:block;font-weight:bold;margin:16px 0 7px;}");
            out.println("input,textarea{width:100%;padding:12px;border:1px solid #d9d1ef;border-radius:8px;font:inherit;}");
            out.println("textarea{min-height:120px;resize:vertical;}");
            out.println("button,.btn{display:inline-block;margin-top:16px;background:#5b3cc4;color:white;padding:11px 17px;border:0;border-radius:8px;text-decoration:none;font-weight:bold;cursor:pointer;}");
            out.println(".message{padding:12px 15px;border-radius:8px;background:#eee9ff;margin-bottom:20px;}");
            out.println(".complaint{border:1px solid #e8e2fa;border-radius:10px;padding:18px;margin:15px 0;}");
            out.println(".complaint h3{margin:0 0 10px;overflow-wrap:anywhere;}");
            out.println(".details{font-size:13px;color:#716c83;line-height:1.8;}");
            out.println(".description{white-space:pre-wrap;overflow-wrap:anywhere;line-height:1.7;}");
            out.println(".status{display:inline-block;background:#eee9ff;color:#5b3cc4;padding:6px 10px;border-radius:15px;font-size:12px;font-weight:bold;}");
            out.println(".empty{color:#716c83;padding:15px 0;}");
            out.println("footer{text-align:center;color:#817b94;padding:25px;font-size:13px;}");
            out.println("@media(max-width:600px){.container{margin:20px auto;}.panel{padding:18px;}.header{padding:18px;}}");
            out.println("</style></head><body>");

            out.println("<header class='header'><div>");
            out.println("<h1>CampusSaathi</h1>");
            out.println("<p>Complaints Management</p></div>");
            out.println("<a href='" + contextPath + dashboardPath(role) + "'>Back to Dashboard</a>");
            out.println("</header>");

            out.println("<main class='container'>");
            out.println("<h2>Welcome, " + escapeHtml(fullName) + "!</h2>");

            String message = request.getParameter("message");

            if ("submitted".equals(message)) {
                out.println("<div class='message'>Your complaint was submitted successfully.</div>");
            } else if ("invalid".equals(message)) {
                out.println("<div class='message'>Please enter a valid title and description.</div>");
            } else if ("error".equals(message)) {
                out.println("<div class='message'>Something went wrong while saving your complaint. Please try again.</div>");
            }

            // Submission form: students, teachers and club leaders.
            if ("STUDENT".equals(role)
                    || "TEACHER".equals(role)
                    || "CLUB_LEADER".equals(role)) {

                out.println("<section class='panel'>");
                out.println("<h2>Submit a Complaint</h2>");
                out.println("<p>Describe the issue clearly so the appropriate college staff can review it.</p>");

                out.println("<form method='post' action='" + contextPath
                    + request.getServletPath() + "'>");

                out.println("<label for='title'>Complaint Title</label>");
                out.println("<input id='title' name='title' type='text' maxlength='200' required>");

                out.println("<label for='description'>Description</label>");
                out.println("<textarea id='description' name='description' maxlength='5000' required></textarea>");

                out.println("<button type='submit'>Submit Complaint</button>");
                out.println("</form></section>");
            }

            out.println("<section class='panel'>");

            if ("HOD".equals(role)) {
                out.println("<h2>Complaints from Teachers and Club Leaders</h2>");
            } else if ("TEACHER".equals(role)) {
                out.println("<h2>Student Complaints</h2>");
            } else {
                out.println("<h2>My Complaints</h2>");
            }

            displayComplaints(out, role, session.getAttribute("userId"));

            out.println("</section></main>");
            out.println("<footer>CampusSaathi &copy; 2026 | Complaints Management</footer>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        String path = request.getServletPath();

        if (!isAuthorizedPath(path, role)
                || !("STUDENT".equals(role)
                || "TEACHER".equals(role)
                || "CLUB_LEADER".equals(role))) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "You are not authorized to submit complaints here."
            );
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");

        if (title == null || description == null
                || title.trim().isEmpty()
                || description.trim().isEmpty()
                || title.trim().length() > 200
                || description.trim().length() > 5000) {

            response.sendRedirect(
                request.getContextPath() + path + "?message=invalid"
            );
            return;
        }

        String sql = "INSERT INTO complaints "
            + "(title, description, submitted_by, submitter_role) "
            + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, title.trim());
            statement.setString(2, description.trim());
            statement.setInt(3, Integer.parseInt(
                String.valueOf(session.getAttribute("userId"))
            ));
            statement.setString(4, role);

            statement.executeUpdate();

            response.sendRedirect(
                request.getContextPath() + path + "?message=submitted"
            );

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Unable to save complaint.", e);
        }
    }

    private void displayComplaints(PrintWriter out,
                                   String role,
                                   Object userId) throws ServletException {

        String sql;
        boolean filterByUser = false;

        if ("HOD".equals(role)) {
            sql = "SELECT c.complaint_id, c.title, c.description, "
                + "c.submitter_role, c.status, c.created_at, u.full_name "
                + "FROM complaints c JOIN users u ON c.submitted_by = u.user_id "
                + "WHERE c.submitter_role IN ('TEACHER', 'CLUB_LEADER') "
                + "ORDER BY c.created_at DESC";

        } else if ("TEACHER".equals(role)) {
            sql = "SELECT c.complaint_id, c.title, c.description, "
                + "c.submitter_role, c.status, c.created_at, u.full_name "
                + "FROM complaints c JOIN users u ON c.submitted_by = u.user_id "
                + "WHERE c.submitter_role = 'STUDENT' "
                + "ORDER BY c.created_at DESC";

        } else {
            sql = "SELECT c.complaint_id, c.title, c.description, "
                + "c.submitter_role, c.status, c.created_at, u.full_name "
                + "FROM complaints c JOIN users u ON c.submitted_by = u.user_id "
                + "WHERE c.submitted_by = ? "
                + "ORDER BY c.created_at DESC";

            filterByUser = true;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (filterByUser) {
                statement.setInt(1, Integer.parseInt(String.valueOf(userId)));
            }

            try (ResultSet rs = statement.executeQuery()) {

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    out.println("<article class='complaint'>");
                    out.println("<h3>#"
                        + rs.getInt("complaint_id") + " - "
                        + escapeHtml(rs.getString("title")) + "</h3>");

                    out.println("<div class='details'>");
                    out.println("Submitted by: "
                        + escapeHtml(rs.getString("full_name")));
                    out.println(" | Role: "
                        + escapeHtml(rs.getString("submitter_role")));
                    out.println("<br>Date: "
                        + escapeHtml(String.valueOf(rs.getTimestamp("created_at"))));
                    out.println("</div>");

                    out.println("<p class='description'>"
                        + escapeHtml(rs.getString("description"))
                        + "</p>");

                    out.println("<span class='status'>"
                        + escapeHtml(rs.getString("status"))
                        + "</span>");

                    out.println("</article>");
                }

                if (!found) {
                    out.println("<p class='empty'>No complaints to display yet.</p>");
                }
            }

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Unable to retrieve complaints.", e);
        }
    }

    private boolean isAuthorizedPath(String path, String role) {

        if (path == null || role == null) {
            return false;
        }

        switch (path) {
            case "/hod/complaints":
                return "HOD".equals(role);

            case "/teacher/complaints":
                return "TEACHER".equals(role);

            case "/student/complaints":
                return "STUDENT".equals(role);

            case "/club/complaints":
                return "CLUB_LEADER".equals(role);

            default:
                return false;
        }
    }

    private String dashboardPath(String role) {

        switch (role) {
            case "HOD":
                return "/hod/dashboard";

            case "TEACHER":
                return "/teacher/dashboard";

            case "STUDENT":
                return "/student/dashboard";

            case "CLUB_LEADER":
                return "/club/dashboard";

            default:
                return "/";
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
