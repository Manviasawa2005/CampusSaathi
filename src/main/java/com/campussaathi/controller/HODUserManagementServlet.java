package com.campussaathi.controller;

import java.io.IOException;
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

@WebServlet("/hod/users")
public class HODUserManagementServlet extends HttpServlet {


private static final long serialVersionUID = 1L;

@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession(false);

    // Only logged-in users can continue.
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Only the HOD can manage user accounts.
    String role = (String) session.getAttribute("role");

    if (!"HOD".equals(role)) {
        response.sendError(
            HttpServletResponse.SC_FORBIDDEN,
            "Only the HOD can access user management."
        );
        return;
    }

    response.setContentType("text/html;charset=UTF-8");

    StringBuilder rows = new StringBuilder();

    String sql = "SELECT user_id, full_name, email, role, status " +
                 "FROM users ORDER BY user_id";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            rows.append("<tr>")
                .append("<td>")
                .append(rs.getInt("user_id"))
                .append("</td>")
                .append("<td>")
                .append(escapeHtml(rs.getString("full_name")))
                .append("</td>")
                .append("<td>")
                .append(escapeHtml(rs.getString("email")))
                .append("</td>")
                .append("<td>")
                .append(escapeHtml(rs.getString("role")))
                .append("</td>")
                .append("<td>")
                .append(escapeHtml(rs.getString("status")))
                .append("</td>")
                .append("</tr>");
        }

    } catch (SQLException e) {
        getServletContext().log(
            "Unable to load CampusSaathi user accounts.", e
        );

        response.sendError(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Unable to load user accounts. Check the server logs."
        );
        return;
    }

    if (rows.length() == 0) {
        rows.append(
            "<tr><td colspan='5'>No accounts found.</td></tr>"
        );
    }

    String html =
        "<!DOCTYPE html>" +
        "<html lang='en'>" +
        "<head>" +
        "<meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
        "<title>CampusSaathi | User Management</title>" +

        "<style>" +
        "*{box-sizing:border-box;}" +
        "body{margin:0;font-family:Arial,sans-serif;" +
        "background:#f5f3ff;color:#272343;}" +

        "header{background:#5b3cc4;color:white;padding:22px 6%;" +
        "display:flex;justify-content:space-between;align-items:center;" +
        "flex-wrap:wrap;gap:15px;}" +

        "header h1{margin:0;font-size:26px;}" +
        "header p{margin:7px 0 0;color:#e6deff;}" +

        ".logout{background:white;color:#5b3cc4;padding:11px 16px;" +
        "border-radius:8px;text-decoration:none;font-weight:bold;}" +

        ".container{max-width:1150px;margin:35px auto;padding:0 22px;}" +
        ".back{color:#5b3cc4;text-decoration:none;font-weight:bold;}" +
        "h2{margin-top:25px;}" +
        ".description{color:#716c83;line-height:1.7;}" +

        ".table-box{margin-top:25px;background:white;padding:20px;" +
        "border-radius:14px;overflow-x:auto;" +
        "box-shadow:0 5px 18px rgba(50,30,100,.05);}" +

        "table{width:100%;border-collapse:collapse;min-width:650px;}" +
        "th,td{text-align:left;padding:15px;border-bottom:1px solid #eee9ff;}" +
        "th{background:#eee9ff;color:#4d32a7;}" +
        "tr:last-child td{border-bottom:none;}" +

        ".note{margin-top:22px;background:#eee9ff;padding:18px;" +
        "border-radius:10px;line-height:1.7;}" +

        "footer{text-align:center;padding:30px;color:#817b94;font-size:13px;}" +
        "</style>" +
        "</head>" +

        "<body>" +

        "<header>" +
        "<div><h1>CampusSaathi</h1>" +
        "<p>HOD | User Management</p></div>" +
        "<a class='logout' href='" +
        request.getContextPath() + "/logout'>Log Out</a>" +
        "</header>" +

        "<main class='container'>" +

        "<a class='back' href='" +
        request.getContextPath() +
        "/hod/dashboard'>&larr; Back to HOD Dashboard</a>" +

        "<h2>Registered User Accounts</h2>" +

        "<p class='description'>" +
        "View the accounts registered in CampusSaathi, including " +
        "their assigned roles and account status." +
        "</p>" +

        "<section class='table-box'>" +
        "<table>" +
        "<thead><tr>" +
        "<th>User ID</th>" +
        "<th>Full Name</th>" +
        "<th>Email</th>" +
        "<th>Role</th>" +
        "<th>Status</th>" +
        "</tr></thead>" +
        "<tbody>" + rows + "</tbody>" +
        "</table>" +
        "</section>" +

        "<div class='note'>" +
        "<strong>Account Management</strong><br>" +
        "This page currently displays registered accounts. " +
        "In the next step, we will add a secure form so the HOD " +
        "can create accounts for teachers, students, and club leaders." +
        "</div>" +

        "</main>" +

        "<footer>CampusSaathi &copy; 2026 | HOD Portal</footer>" +
        "</body></html>";

    response.getWriter().write(html);
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
