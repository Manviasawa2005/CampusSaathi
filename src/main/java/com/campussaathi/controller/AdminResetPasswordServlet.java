
package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

@WebServlet("/admin/reset-password")
public class AdminResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
            !"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Only the College Administrator can reset passwords."
            );
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid account ID."
            );
            return;
        }

        String fullName;
        String email;
        String role;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT full_name, email, role FROM users WHERE user_id = ?")) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Account not found."
                    );
                    return;
                }

                fullName = rs.getString("full_name");
                email = rs.getString("email");
                role = rs.getString("role");
            }

        } catch (SQLException e) {
            throw new ServletException("Unable to load account.", e);
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        String contextPath = request.getContextPath();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Reset Password | CampusSaathi</title>");

        out.println("<style>");
        out.println("*{box-sizing:border-box}");
        out.println("body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#24213a}");
        out.println("header{background:#4f46a5;color:white;padding:22px 7%}");
        out.println("header h1{margin:0;font-size:25px}");
        out.println("header p{margin:6px 0 0;color:#e8e6ff}");
        out.println(".container{width:90%;max-width:650px;margin:40px auto}");
        out.println(".panel{background:white;border-radius:14px;padding:28px;box-shadow:0 4px 18px rgba(45,35,100,.08)}");
        out.println("h2{margin-top:0;color:#39317f}");
        out.println(".account{background:#f8f7ff;border:1px solid #e4dfff;padding:16px;border-radius:9px;margin:20px 0;line-height:1.9}");
        out.println(".field{margin-bottom:18px}");
        out.println("label{display:block;margin-bottom:8px;font-size:14px;font-weight:bold}");
        out.println("input{width:100%;padding:12px;border:1px solid #d8d4e8;border-radius:8px;font-size:15px}");
        out.println(".hint{font-size:12px;color:#77718b;margin-top:6px}");
        out.println(".actions{display:flex;gap:12px;flex-wrap:wrap;margin-top:24px}");
        out.println(".btn{display:inline-block;padding:12px 17px;border:0;border-radius:8px;text-decoration:none;font-size:14px;font-weight:bold;cursor:pointer}");
        out.println(".primary{background:#5145b5;color:white}");
        out.println(".secondary{background:#ede9fe;color:#4338a8}");
        out.println("footer{text-align:center;color:#89839c;font-size:12px;margin:30px 0}");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>CampusSaathi</h1>");
        out.println("<p>College Administrator Portal</p>");
        out.println("</header>");

        out.println("<main class='container'>");
        out.println("<section class='panel'>");

        out.println("<h2>Reset Account Password</h2>");
        out.println("<p>Set a new password for this account.</p>");

        out.println("<div class='account'>");
        out.println("<strong>Name:</strong> " + escapeHtml(fullName) + "<br>");
        out.println("<strong>Email:</strong> " + escapeHtml(email) + "<br>");
        out.println("<strong>Role:</strong> " + escapeHtml(role));
        out.println("</div>");

        out.println("<form method='post' action='"
            + contextPath + "/admin/reset-password'>");

        out.println("<input type='hidden' name='userId' value='"
            + userId + "'>");

        out.println("<div class='field'>");
        out.println("<label for='newPassword'>New Password</label>");
        out.println("<input type='password' id='newPassword' name='newPassword' "
            + "minlength='12' maxlength='128' required autocomplete='new-password'>");
        out.println("<div class='hint'>Use between 12 and 128 characters.</div>");
        out.println("</div>");

        out.println("<div class='field'>");
        out.println("<label for='confirmPassword'>Confirm New Password</label>");
        out.println("<input type='password' id='confirmPassword' "
            + "name='confirmPassword' minlength='12' maxlength='128' "
            + "required autocomplete='new-password'>");
        out.println("</div>");

        out.println("<div class='actions'>");
        out.println("<button class='btn primary' type='submit'>Reset Password</button>");
        out.println("<a class='btn secondary' href='"
            + contextPath + "/admin/users'>Cancel</a>");
        out.println("</div>");

        out.println("</form>");
        out.println("</section>");
        out.println("</main>");

        out.println("<footer>CampusSaathi &copy; 2026</footer>");

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
            !"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {

            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Only the College Administrator can reset passwords."
            );
            return;
        }

        request.setCharacterEncoding("UTF-8");

        int userId;

        try {
            userId = Integer.parseInt(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid account ID."
            );
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null ||
            confirmPassword == null ||
            newPassword.length() < 12 ||
            newPassword.length() > 128) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Password must contain between 12 and 128 characters."
            );
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Passwords do not match. Please go back and try again."
            );
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Confirm that the account exists.
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT user_id FROM users WHERE user_id = ?")) {

                check.setInt(1, userId);

                try (ResultSet rs = check.executeQuery()) {

                    if (!rs.next()) {
                        response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "Account not found."
                        );
                        return;
                    }
                }
            }

            // Generate the secure password hash.
            String passwordHash;

            try {
                passwordHash = PasswordUtil.hashPassword(newPassword);

            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new ServletException(
                    "Unable to securely hash the password.", e
                );
            }

            // Update the password hash in the database.
            try (PreparedStatement update = con.prepareStatement(
                    "UPDATE users SET password_hash = ? WHERE user_id = ?")) {

                update.setString(1, passwordHash);
                update.setInt(2, userId);

                if (update.executeUpdate() != 1) {
                    response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Password could not be updated."
                    );
                    return;
                }
            }

            response.sendRedirect(
                request.getContextPath()
                    + "/admin/users?passwordReset=success"
            );

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to reset account password.", e
            );
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

