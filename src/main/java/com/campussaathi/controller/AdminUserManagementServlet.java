
package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

@WebServlet("/admin/users")
public class AdminUserManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 10;

    private static final String[] ROLES = {
        "COLLEGE_ADMIN",
        "HOD",
        "TEACHER",
        "STUDENT",
        "CLUB_LEADER"
    };

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || !"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String search = request.getParameter("search");

        if (search == null) {
            search = "";
        }

        search = search.trim();

        String role = request.getParameter("role");

        if (role == null || role.isBlank()) {
            role = "ALL";
        }

        boolean validRole = "ALL".equals(role);

        for (String allowedRole : ROLES) {
            if (allowedRole.equals(role)) {
                validRole = true;
                break;
            }
        }

        if (!validRole) {
            role = "ALL";
        }

        int page = 1;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException | NullPointerException ignored) {
            page = 1;
        }

        if (page < 1) {
            page = 1;
        }

        StringBuilder where = new StringBuilder();
        where.append(" WHERE (full_name LIKE ? OR email LIKE ?) ");

        if (!"ALL".equals(role)) {
            where.append(" AND role = ? ");
        }

        String countSql = "SELECT COUNT(*) FROM users " + where;

        // Batch is now included in the account query.
        String listSql =
            "SELECT user_id, full_name, email, role, status, batch " +
            "FROM users " + where +
            "ORDER BY user_id LIMIT ? OFFSET ?";

        int totalAccounts = 0;

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(countSql)) {

                bindFilters(ps, search, role);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalAccounts = rs.getInt(1);
                    }
                }
            }

            int totalPages = Math.max(
                1,
                (int) Math.ceil(totalAccounts / (double) PAGE_SIZE)
            );

            if (page > totalPages) {
                page = totalPages;
            }

            int offset = (page - 1) * PAGE_SIZE;

            PrintWriter out = response.getWriter();

            printHeader(out);
            printNavigation(out, request.getContextPath());
            printPasswordResetMessage(out, request);
            printBulkImportSection(out, request.getContextPath());
            printSearchForm(out, request.getContextPath(), search, role);

            out.println("<section class='panel'>");
            out.println("<h2>Registered Accounts</h2>");

            out.println(
                "<p class='muted'>Total matching accounts: <strong>" +
                totalAccounts + "</strong></p>"
            );

            out.println("<div class='table-wrap'><table>");
            out.println("<thead><tr>");
            out.println("<th>ID</th>");
            out.println("<th>Full Name</th>");
            out.println("<th>Email</th>");
            out.println("<th>Role</th>");
            out.println("<th>Batch</th>");
            out.println("<th>Status</th>");
            out.println("<th>Actions</th>");
            out.println("</tr></thead><tbody>");

            try (PreparedStatement ps = con.prepareStatement(listSql)) {

                int index = bindFilters(ps, search, role);

                ps.setInt(index++, PAGE_SIZE);
                ps.setInt(index, offset);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        int userId = rs.getInt("user_id");

                        String name =
                            escapeHtml(rs.getString("full_name"));

                        String email =
                            escapeHtml(rs.getString("email"));

                        String accountRole =
                            escapeHtml(rs.getString("role"));

                        String batch =
                            escapeHtml(rs.getString("batch"));

                        String status =
                            escapeHtml(rs.getString("status"));

                        if (batch.isBlank()) {
                            batch = "—";
                        }

                        out.println("<tr>");

                        out.println("<td>" + userId + "</td>");
                        out.println("<td>" + name + "</td>");
                        out.println("<td>" + email + "</td>");

                        out.println(
                            "<td><span class='role-badge'>" +
                            accountRole +
                            "</span></td>"
                        );

                        out.println("<td>" + batch + "</td>");

                        out.println(
                            "<td><span class='status-badge'>" +
                            status +
                            "</span></td>"
                        );

                        out.println(
                            "<td><a class='reset-btn' href='" +
                            request.getContextPath() +
                            "/admin/reset-password?userId=" + userId +
                            "'>Reset Password</a></td>"
                        );

                        out.println("</tr>");
                    }

                    if (!found) {
                        out.println(
                            "<tr><td colspan='7' class='empty'>" +
                            "No accounts found. Try another search or filter." +
                            "</td></tr>"
                        );
                    }
                }
            }

            out.println("</tbody></table></div>");

            // Pagination
            out.println("<div class='pagination'>");

            String encodedSearch = URLEncoder.encode(
                search, StandardCharsets.UTF_8
            );

            String encodedRole = URLEncoder.encode(
                role, StandardCharsets.UTF_8
            );

            if (page > 1) {
                out.println(
                    "<a class='page-link' href='" +
                    request.getContextPath() +
                    "/admin/users?page=" + (page - 1) +
                    "&search=" + encodedSearch +
                    "&role=" + encodedRole +
                    "'>&larr; Previous</a>"
                );
            } else {
                out.println(
                    "<span class='page-disabled'>" +
                    "&larr; Previous</span>"
                );
            }

            out.println(
                "<span class='page-number'>Page " +
                page + " of " + totalPages + "</span>"
            );

            if (page < totalPages) {
                out.println(
                    "<a class='page-link' href='" +
                    request.getContextPath() +
                    "/admin/users?page=" + (page + 1) +
                    "&search=" + encodedSearch +
                    "&role=" + encodedRole +
                    "'>Next &rarr;</a>"
                );
            } else {
                out.println(
                    "<span class='page-disabled'>Next &rarr;</span>"
                );
            }

            out.println("</div>");
            out.println("</section>");

            printFooter(out);

        } catch (SQLException e) {

            getServletContext().log(
                "Error loading administrator account management page.", e
            );

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            PrintWriter out = response.getWriter();

            printHeader(out);
            out.println("<section class='panel error'>");
            out.println("<h2>Unable to load accounts</h2>");
            out.println(
                "<p>Please check the database connection and try again.</p>"
            );
            out.println("</section>");
            printFooter(out);
        }
    }

    private int bindFilters(PreparedStatement ps,
                            String search,
                            String role) throws SQLException {

        int index = 1;
        String searchPattern = "%" + search + "%";

        ps.setString(index++, searchPattern);
        ps.setString(index++, searchPattern);

        if (!"ALL".equals(role)) {
            ps.setString(index++, role);
        }

        return index;
    }

    private void printHeader(PrintWriter out) {

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println(
            "<meta name='viewport' " +
            "content='width=device-width, initial-scale=1.0'>"
        );
        out.println("<title>Account Management | CampusSaathi</title>");

        out.println("<style>");

        out.println("*{box-sizing:border-box}");

        out.println(
            "body{margin:0;font-family:Arial,sans-serif;" +
            "background:#f5f3ff;color:#24213a}"
        );

        out.println(
            "header{background:#4f46a5;color:white;padding:22px 7%;" +
            "display:flex;justify-content:space-between;align-items:center;" +
            "gap:15px;flex-wrap:wrap}"
        );

        out.println("header h1{margin:0;font-size:25px}");
        out.println("header p{margin:6px 0 0;color:#e8e6ff}");

        out.println(
            ".container{width:90%;max-width:1150px;margin:30px auto}"
        );

        out.println(".top-links{display:flex;gap:10px;flex-wrap:wrap}");

        out.println(
            ".btn{display:inline-block;text-decoration:none;border:0;" +
            "border-radius:8px;padding:11px 16px;font-weight:bold;" +
            "cursor:pointer;font-size:14px}"
        );

        out.println(".btn-primary{background:#5145b5;color:white}");
        out.println(".btn-secondary{background:#ede9fe;color:#4338a8}");
        out.println(".btn-green{background:#087f5b;color:white}");

        out.println(
            ".panel{background:white;border-radius:14px;padding:24px;" +
            "margin-bottom:24px;box-shadow:0 4px 18px rgba(45,35,100,.07)}"
        );

        out.println("h2{margin-top:0;font-size:21px}");
        out.println(".muted{color:#6b6682;font-size:14px}");

        out.println(
            ".bulk-box{background:#f8f7ff;border:1px solid #e4dfff;" +
            "border-radius:10px;padding:20px;margin-top:15px}"
        );

        out.println(
            ".bulk-actions{display:flex;gap:12px;flex-wrap:wrap;margin-top:15px}"
        );

        out.println(
            ".search-form{display:flex;gap:12px;align-items:end;" +
            "flex-wrap:wrap;margin-top:18px}"
        );

        out.println(
            ".field{display:flex;flex-direction:column;gap:7px;" +
            "flex:1;min-width:180px}"
        );

        out.println(
            "label{font-size:13px;font-weight:bold;color:#4b4568}"
        );

        out.println(
            "input,select{padding:11px;border:1px solid #d8d4e8;" +
            "border-radius:8px;font-size:14px;background:white}"
        );

        out.println(
            "table{width:100%;border-collapse:collapse;min-width:950px}"
        );

        out.println(
            "th,td{text-align:left;padding:14px 12px;" +
            "border-bottom:1px solid #eeeaf7;font-size:14px}"
        );

        out.println("th{background:#f7f5ff;color:#5145a5}");
        out.println(".table-wrap{width:100%;overflow-x:auto}");

        out.println(
            ".role-badge{background:#ede9fe;color:#5145a5;padding:5px 8px;" +
            "border-radius:6px;font-size:12px;font-weight:bold}"
        );

        out.println(
            ".status-badge{background:#dcfce7;color:#166534;padding:5px 8px;" +
            "border-radius:6px;font-size:12px;font-weight:bold}"
        );

        out.println(
            ".reset-btn{display:inline-block;background:#fff1f2;" +
            "color:#be123c;text-decoration:none;padding:8px 10px;" +
            "border-radius:7px;font-size:12px;font-weight:bold;" +
            "white-space:nowrap}"
        );

        out.println(".reset-btn:hover{background:#ffe4e6}");

        out.println(
            ".pagination{display:flex;justify-content:center;align-items:center;" +
            "gap:18px;flex-wrap:wrap;margin-top:22px}"
        );

        out.println(
            ".page-link{background:#5145b5;color:white;text-decoration:none;" +
            "padding:9px 13px;border-radius:7px;font-size:13px;font-weight:bold}"
        );

        out.println(".page-disabled{color:#aaa5b9;font-size:13px}");
        out.println(".page-number{font-weight:bold;font-size:14px}");
        out.println(".empty{text-align:center;padding:28px;color:#777}");

        out.println(
            ".success{background:#dcfce7;color:#166534;" +
            "border-left:5px solid #16a34a;padding:15px;" +
            "border-radius:8px;margin-bottom:24px}"
        );

        out.println(".error{border-left:5px solid #dc2626}");

        out.println(
            "footer{text-align:center;color:#89839c;font-size:12px;" +
            "padding:10px 0 30px}"
        );

        out.println(
            "@media(max-width:600px){" +
            ".container{width:94%;margin:18px auto}" +
            ".panel{padding:16px}header{padding:18px 5%}}"
        );

        out.println("</style></head><body>");

        out.println("<header><div><h1>CampusSaathi</h1>");
        out.println("<p>College Administrator Portal</p></div></header>");

        out.println("<main class='container'>");
    }

    private void printNavigation(PrintWriter out, String contextPath) {

        out.println("<section class='panel'>");
        out.println("<h2>Account Management</h2>");

        out.println(
            "<p class='muted'>" +
            "Search, filter and manage CampusSaathi accounts.</p>"
        );

        out.println("<div class='top-links'>");

        out.println(
            "<a class='btn btn-secondary' href='" +
            contextPath + "/admin/dashboard'>Back to Dashboard</a>"
        );

        out.println(
            "<a class='btn btn-primary' href='" +
            contextPath + "/admin/create-user'>+ Create New Account</a>"
        );

        out.println("</div></section>");
    }

    private void printPasswordResetMessage(PrintWriter out,
                                           HttpServletRequest request) {

        if ("success".equals(request.getParameter("passwordReset"))) {
            out.println(
                "<div class='success'>Password reset successfully. " +
                "You can now log in using the new password.</div>"
            );
        }
    }

    private void printBulkImportSection(PrintWriter out, String contextPath) {

        out.println("<section class='panel'>");
        out.println("<h2>Bulk Import Accounts</h2>");

        out.println(
            "<p class='muted'>Create many accounts together by uploading " +
            "a CSV file instead of entering every account manually.</p>"
        );

        out.println("<div class='bulk-box'><strong>How it works</strong><ol>");
        out.println("<li>Download the CSV template.</li>");
        out.println(
            "<li>Fill in the account details in Excel or another " +
            "spreadsheet application.</li>"
        );
        out.println("<li>Upload the completed file to import multiple accounts together.</li>");
        out.println("</ol><div class='bulk-actions'>");

        out.println(
            "<a class='btn btn-secondary' href='" +
            contextPath + "/admin/csv-template'>Download CSV Template</a>"
        );

        out.println(
            "<a class='btn btn-green' href='" +
            contextPath + "/admin/bulk-import'>Upload Accounts CSV</a>"
        );

        out.println("</div></div></section>");
    }

    private void printSearchForm(PrintWriter out,
                                 String contextPath,
                                 String search,
                                 String role) {

        out.println("<section class='panel'>");
        out.println("<h2>Search and Filter Accounts</h2>");

        out.println(
            "<form class='search-form' method='get' action='" +
            contextPath + "/admin/users'>"
        );

        out.println("<div class='field'>");
        out.println("<label for='search'>Search by name or email</label>");

        out.println(
            "<input type='text' id='search' name='search' value='" +
            escapeHtml(search) +
            "' placeholder='Enter name or email'>"
        );

        out.println("</div><div class='field'>");
        out.println("<label for='role'>Filter by role</label>");
        out.println("<select id='role' name='role'>");

        option(out, "ALL", "All Roles", role);
        option(out, "COLLEGE_ADMIN", "College Administrator", role);
        option(out, "HOD", "HOD", role);
        option(out, "TEACHER", "Teacher", role);
        option(out, "STUDENT", "Student", role);
        option(out, "CLUB_LEADER", "Club Leader", role);

        out.println("</select></div>");

        out.println(
            "<button class='btn btn-primary' type='submit'>Search</button>"
        );

        out.println(
            "<a class='btn btn-secondary' href='" +
            contextPath + "/admin/users'>Reset Filters</a>"
        );

        out.println("</form></section>");
    }

    private void option(PrintWriter out,
                        String value,
                        String label,
                        String selectedRole) {

        out.println(
            "<option value='" + value + "'" +
            (value.equals(selectedRole) ? " selected" : "") +
            ">" + label + "</option>"
        );
    }

    private void printFooter(PrintWriter out) {

        out.println("</main>");
        out.println(
            "<footer>CampusSaathi &copy; 2026 | College Management Portal</footer>"
        );
        out.println("</body></html>");
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

