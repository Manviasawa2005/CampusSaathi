package com.campussaathi.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/hod/create-user")
public class HODCreateUserServlet extends HttpServlet {


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

    if (!"HOD".equals(role)) {
        response.sendError(
            HttpServletResponse.SC_FORBIDDEN,
            "Only the HOD can create user accounts."
        );
        return;
    }

    response.setContentType("text/html;charset=UTF-8");

    String message = request.getParameter("message");
    String safeMessage = "";

    if ("success".equals(message)) {
        safeMessage =
            "<div class='success'>Account created successfully!</div>";
    } else if ("exists".equals(message)) {
        safeMessage =
            "<div class='error'>An account with this email already exists.</div>";
    } else if ("invalid".equals(message)) {
        safeMessage =
            "<div class='error'>Please enter valid details. Password must contain at least 12 characters.</div>";
    } else if ("error".equals(message)) {
        safeMessage =
            "<div class='error'>Unable to create the account. Please try again.</div>";
    }

    String contextPath = request.getContextPath();

    String html =
        "<!DOCTYPE html>" +
        "<html lang='en'>" +
        "<head>" +
        "<meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
        "<title>CampusSaathi | Create User</title>" +

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

        ".container{max-width:750px;margin:35px auto;padding:0 22px;}" +
        ".back{color:#5b3cc4;text-decoration:none;font-weight:bold;}" +

        ".form-box{margin-top:25px;background:white;padding:30px;" +
        "border-radius:16px;border:1px solid #e8e2fa;" +
        "box-shadow:0 5px 18px rgba(50,30,100,.05);}" +

        "h2{margin-top:0;}" +
        ".description{color:#716c83;line-height:1.7;margin-bottom:25px;}" +

        "label{display:block;font-weight:bold;margin:18px 0 8px;}" +

        "input,select{width:100%;padding:13px;" +
        "border:1px solid #d9d2ee;border-radius:8px;" +
        "font-size:15px;background:white;}" +

        "input:focus,select:focus{outline:2px solid #b8a8f5;" +
        "border-color:#5b3cc4;}" +

        ".hint{font-size:12px;color:#716c83;margin-top:7px;}" +

        ".submit-btn{width:100%;margin-top:25px;padding:14px;" +
        "background:#5b3cc4;color:white;border:none;" +
        "border-radius:8px;font-size:16px;font-weight:bold;cursor:pointer;}" +

        ".submit-btn:hover{background:#43289e;}" +

        ".success{background:#e4f8e9;color:#176b32;padding:13px;" +
        "border-radius:8px;margin-bottom:18px;}" +

        ".error{background:#ffe7e7;color:#a32222;padding:13px;" +
        "border-radius:8px;margin-bottom:18px;}" +

        ".note{margin-top:20px;padding:17px;background:#eee9ff;" +
        "border-radius:10px;line-height:1.7;font-size:14px;}" +

        "footer{text-align:center;padding:30px;color:#817b94;font-size:13px;}" +

        "</style>" +
        "</head>" +

        "<body>" +

        "<header>" +
        "<div><h1>CampusSaathi</h1>" +
        "<p>HOD | Create User Account</p></div>" +

        "<a class='logout' href='" +
        contextPath + "/logout'>Log Out</a>" +
        "</header>" +

        "<main class='container'>" +

        "<a class='back' href='" +
        contextPath + "/hod/users'>&larr; Back to User Management</a>" +

        "<section class='form-box'>" +

        "<h2>Create a User Account</h2>" +

        "<p class='description'>" +
        "Register a teacher, student, or club leader in CampusSaathi. " +
        "Their selected role determines which portal they can access." +
        "</p>" +

        safeMessage +

        "<form method='post' action='" +
        contextPath + "/hod/create-user'>" +

        "<label for='fullName'>Full Name</label>" +
        "<input type='text' id='fullName' name='fullName' " +
        "maxlength='100' required autocomplete='name'>" +

        "<label for='email'>Email Address</label>" +
        "<input type='email' id='email' name='email' " +
        "maxlength='150' required autocomplete='email'>" +

        "<label for='role'>Select Role</label>" +
        "<select id='role' name='role' required>" +
        "<option value=''>-- Select a Role --</option>" +
        "<option value='TEACHER'>Teacher</option>" +
        "<option value='STUDENT'>Student</option>" +
        "<option value='CLUB_LEADER'>Club Leader</option>" +
        "</select>" +

        "<label for='password'>Initial Password</label>" +
        "<input type='password' id='password' name='password' " +
        "minlength='12' maxlength='128' required " +
        "autocomplete='new-password'>" +

        "<p class='hint'>" +
        "Use at least 12 characters. Share initial credentials securely." +
        "</p>" +

        "<button class='submit-btn' type='submit'>" +
        "Create Account" +
        "</button>" +

        "</form>" +

        "<div class='note'>" +
        "<strong>Security note:</strong><br>" +
        "Passwords will be stored as password hashes, not plain text. " +
        "The HOD account cannot be created from this form." +
        "</div>" +

        "</section>" +
        "</main>" +

        "<footer>CampusSaathi &copy; 2026 | HOD Portal</footer>" +

        "</body></html>";

    response.getWriter().write(html);
}

@Override
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("userId") == null) {
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Please log in first."
        );
        return;
    }

    String role = (String) session.getAttribute("role");

    if (!"HOD".equals(role)) {
        response.sendError(
            HttpServletResponse.SC_FORBIDDEN,
            "Only the HOD can create user accounts."
        );
        return;
    }

    // The account-creation logic will be added in the next step.
    response.sendRedirect(
        request.getContextPath() + "/hod/create-user?message=invalid"
    );
}


}
