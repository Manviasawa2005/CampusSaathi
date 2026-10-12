
package com.campussaathi.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/create-user")
public class AdminCreateUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Only the College Administrator can create accounts.");
            return;
        }

        String contextPath = request.getContextPath();
        String message = request.getParameter("message");
        String alert = "";

        if ("success".equals(message)) {
            alert = "<div class='success'>Account created successfully!</div>";
        } else if ("duplicate".equals(message)) {
            alert = "<div class='error'>An account with this email already exists.</div>";
        } else if ("invalid".equals(message)) {
            alert = "<div class='error'>Please enter valid details. Student accounts require a batch, and passwords must contain at least 12 characters.</div>";
        } else if ("error".equals(message)) {
            alert = "<div class='error'>Unable to create the account. Please try again.</div>";
        }

        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().println(
            "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "<title>Create User Account | CampusSaathi</title>"
            + "<style>"
            + "*{box-sizing:border-box;}"
            + "body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#272343;}"
            + "header{background:white;padding:22px 6%;border-bottom:1px solid #ece8f7;}"
            + ".brand{font-size:25px;font-weight:bold;color:#5b3cc4;}"
            + ".brand span{color:#292344;}"
            + "main{max-width:650px;margin:40px auto;padding:0 20px;}"
            + ".panel{background:white;padding:30px;border:1px solid #ebe6f8;"
            + "border-radius:16px;box-shadow:0 8px 25px rgba(50,30,100,.05);}"
            + "h1{margin-top:0;font-size:28px;}"
            + ".muted{color:#716c83;line-height:1.6;}"
            + "label{display:block;font-weight:bold;margin:18px 0 8px;}"
            + "input,select{width:100%;padding:13px;border:1px solid #dcd6ec;"
            + "border-radius:8px;font-size:15px;background:white;}"
            + "input:focus,select:focus{outline:2px solid #c7b8ff;border-color:#7252d5;}"
            + ".button{display:inline-block;margin-top:22px;padding:13px 18px;"
            + "background:#5b3cc4;color:white;border:none;border-radius:8px;"
            + "font-weight:bold;font-size:15px;cursor:pointer;text-decoration:none;}"
            + ".secondary{background:white;color:#5b3cc4;border:1px solid #dcd6ec;margin-left:8px;}"
            + ".error,.success{padding:12px;border-radius:8px;margin:15px 0;line-height:1.5;}"
            + ".error{background:#fff0f0;color:#a52626;}"
            + ".success{background:#e4f8eb;color:#16733b;}"
            + ".note{margin-top:20px;padding:14px;background:#f8f6ff;"
            + "border-radius:8px;color:#716c83;font-size:13px;line-height:1.6;}"
            + ".hidden{display:none;}"
            + "@media(max-width:500px){.panel{padding:22px;}h1{font-size:24px;}"
            + ".secondary{margin-left:0;}}"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<header><div class='brand'>Campus<span>Saathi</span></div></header>"
            + "<main><section class='panel'>"
            + "<h1>Create User Account</h1>"
            + "<p class='muted'>Register a new member of the CampusSaathi portal.</p>"
            + alert
            + "<form action='" + contextPath + "/admin/create-user' method='post'>"

            + "<label for='fullName'>Full Name</label>"
            + "<input type='text' id='fullName' name='fullName' "
            + "maxlength='100' required placeholder='Enter full name'>"

            + "<label for='email'>Email Address</label>"
            + "<input type='email' id='email' name='email' "
            + "maxlength='150' required placeholder='Enter unique email address'>"

            + "<label for='role'>Select Role</label>"
            + "<select id='role' name='role' required>"
            + "<option value=''>Choose a role</option>"
            + "<option value='HOD'>Head of Department (HOD)</option>"
            + "<option value='TEACHER'>Teacher</option>"
            + "<option value='STUDENT'>Student</option>"
            + "<option value='CLUB_LEADER'>Club Leader</option>"
            + "</select>"

            + "<div id='batchSection' class='hidden'>"
            + "<label for='batch'>Student Batch</label>"
            + "<input type='text' id='batch' name='batch' maxlength='50' "
            + "placeholder='e.g. AI&DS-2024'>"
            + "<p class='muted'>Use the same batch name for students in the same class.</p>"
            + "</div>"

            + "<label for='password'>Initial Password</label>"
            + "<input type='password' id='password' name='password' "
            + "minlength='12' maxlength='128' required "
            + "autocomplete='new-password' placeholder='Minimum 12 characters'>"

            + "<button class='button' type='submit'>Create Account</button>"
            + "<a class='button secondary' href='" + contextPath
            + "/admin/users'>Back to Accounts</a>"
            + "</form>"

            + "<div class='note'>"
            + "Passwords are stored as secure hashes. Give the initial password "
            + "to the intended account holder securely."
            + "</div>"

            + "</section></main>"

            + "<script>"
            + "const roleSelect=document.getElementById('role');"
            + "const batchSection=document.getElementById('batchSection');"
            + "const batchInput=document.getElementById('batch');"
            + "function updateBatchField(){"
            + "const isStudent=roleSelect.value==='STUDENT';"
            + "batchSection.classList.toggle('hidden',!isStudent);"
            + "batchInput.required=isStudent;"
            + "if(!isStudent){batchInput.value='';}"
            + "}"
            + "roleSelect.addEventListener('change',updateBatchField);"
            + "updateBatchField();"
            + "</script>"

            + "</body></html>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Only the College Administrator can create accounts.");
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        String password = request.getParameter("password");
        String batch = request.getParameter("batch");

        if (fullName == null || email == null || role == null || password == null
                || fullName.trim().isEmpty()
                || email.trim().isEmpty()
                || password.length() < 12
                || password.length() > 128
                || fullName.trim().length() > 100
                || email.trim().length() > 150
                || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            redirectWithMessage(request, response, "invalid");
            return;
        }

        if (!role.equals("HOD")
                && !role.equals("TEACHER")
                && !role.equals("STUDENT")
                && !role.equals("CLUB_LEADER")) {

            redirectWithMessage(request, response, "invalid");
            return;
        }

        if ("STUDENT".equals(role)) {
            if (batch == null || batch.trim().isEmpty()
                    || batch.trim().length() > 50) {
                redirectWithMessage(request, response, "invalid");
                return;
            }
            batch = batch.trim();
        } else {
            batch = null;
        }

        String sql = "INSERT INTO users "
                   + "(full_name, email, password_hash, role, status, batch) "
                   + "VALUES (?, ?, ?, ?, 'ACTIVE', ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String passwordHash = PasswordUtil.hashPassword(password);

            statement.setString(1, fullName.trim());
            statement.setString(2, email.trim());
            statement.setString(3, passwordHash);
            statement.setString(4, role);
            statement.setString(5, batch);

            statement.executeUpdate();

            redirectWithMessage(request, response, "success");

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {
                redirectWithMessage(request, response, "duplicate");
            } else {
                getServletContext().log(
                        "Unable to create CampusSaathi user account.", e);
                redirectWithMessage(request, response, "error");
            }

        } catch (Exception e) {
            getServletContext().log(
                    "Unexpected error while creating a CampusSaathi account.", e);
            redirectWithMessage(request, response, "error");
        }
    }

    private void redirectWithMessage(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String message)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/admin/create-user?message=" + message);
    }
}

