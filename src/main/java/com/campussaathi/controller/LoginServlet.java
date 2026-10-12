package com.campussaathi.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {


private static final long serialVersionUID = 1L;

@Override
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");

    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String selectedRole = request.getParameter("portalRole");

    if (email == null || password == null
            || email.trim().isEmpty() || password.isEmpty()
            || selectedRole == null || selectedRole.trim().isEmpty()) {

        showLoginError(request, response,
                "Please select a portal and enter your email and password.",
                selectedRole);
        return;
    }

    // Validate all five supported portal roles.
    if (!selectedRole.equals("COLLEGE_ADMIN")
            && !selectedRole.equals("HOD")
            && !selectedRole.equals("TEACHER")
            && !selectedRole.equals("STUDENT")
            && !selectedRole.equals("CLUB_LEADER")) {

        showLoginError(request, response,
                "Please select a valid portal.", null);
        return;
    }

    String sql = "SELECT user_id, full_name, email, password_hash, role, status "
               + "FROM users WHERE email = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, email.trim());

        try (ResultSet resultSet = statement.executeQuery()) {

            if (!resultSet.next()) {
                showLoginError(request, response,
                        "Invalid email or password.", selectedRole);
                return;
            }

            int userId = resultSet.getInt("user_id");
            String fullName = resultSet.getString("full_name");
            String storedHash = resultSet.getString("password_hash");
            String actualRole = resultSet.getString("role");
            String status = resultSet.getString("status");

            boolean passwordMatches =
                    PasswordUtil.verifyPassword(password, storedHash);

            if (!passwordMatches || !"ACTIVE".equals(status)) {
                showLoginError(request, response,
                        "Invalid email or password, or your account is inactive.",
                        selectedRole);
                return;
            }

            // The selected portal must match the account's actual role.
            if (!selectedRole.equals(actualRole)) {
                showLoginError(request, response,
                        "This account does not belong to the selected portal. "
                        + "Please choose the correct portal.",
                        selectedRole);
                return;
            }

            // Invalidate any existing session before creating a new one.
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);

            session.setAttribute("userId", userId);
            session.setAttribute("fullName", fullName);
            session.setAttribute("email", email.trim());
            session.setAttribute("role", actualRole);
            session.setMaxInactiveInterval(30 * 60);

            // Redirect each role to its own dashboard.
            switch (actualRole) {

                case "COLLEGE_ADMIN":
                    response.sendRedirect(
                            request.getContextPath() + "/admin/dashboard");
                    break;

                case "HOD":
                    response.sendRedirect(
                            request.getContextPath() + "/hod/dashboard");
                    break;

                case "TEACHER":
                    response.sendRedirect(
                            request.getContextPath() + "/teacher/dashboard");
                    break;

                case "STUDENT":
                    response.sendRedirect(
                            request.getContextPath() + "/student/dashboard");
                    break;

                case "CLUB_LEADER":
                    response.sendRedirect(
                            request.getContextPath() + "/club/dashboard");
                    break;

                default:
                    session.invalidate();

                    showLoginError(request, response,
                            "Invalid account role.", null);
                    break;
            }
        }

    } catch (Exception e) {

        getServletContext().log(
                "CampusSaathi login failed because of a server error.", e);

        response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println(
                "<h2>Unable to process login</h2>"
                + "<p>Please try again later.</p>"
                + "<a href=\"" + request.getContextPath()
                + "/login.jsp\">Return to Login</a>");
    }
}

private void showLoginError(HttpServletRequest request,
                            HttpServletResponse response,
                            String message,
                            String selectedRole)
        throws ServletException, IOException {

    request.setAttribute("errorMessage", message);
    request.setAttribute("selectedRole", selectedRole);

    request.getRequestDispatcher("/login.jsp")
           .forward(request, response);
}


}
