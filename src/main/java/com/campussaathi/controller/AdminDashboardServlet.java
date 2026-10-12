package com.campussaathi.controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {


private static final long serialVersionUID = 1L;

@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws IOException {

    response.setContentType("text/html;charset=UTF-8");

    HttpSession session = request.getSession(false);

    // The user must be logged in.
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Only the College Administrator can access this dashboard.
    String role = (String) session.getAttribute("role");

    if (!"COLLEGE_ADMIN".equals(role)) {
        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Access denied. This dashboard is only for the College Administrator."
        );
        return;
    }

    String fullName = (String) session.getAttribute("fullName");

    if (fullName == null) {
        fullName = "Administrator";
    }

    String safeName = fullName
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String contextPath = request.getContextPath();

    response.getWriter().println(
        "<!DOCTYPE html>"
        + "<html lang='en'>"
        + "<head>"
        + "<meta charset='UTF-8'>"
        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
        + "<title>College Administrator | CampusSaathi</title>"

        + "<style>"
        + "*{box-sizing:border-box;}"
        + "body{margin:0;font-family:Arial,sans-serif;"
        + "background:#f5f3ff;color:#272343;}"
        + "header{background:#fff;padding:22px 6%;"
        + "display:flex;justify-content:space-between;align-items:center;"
        + "gap:15px;flex-wrap:wrap;border-bottom:1px solid #ece8f7;}"
        + ".brand{font-size:25px;font-weight:bold;color:#5b3cc4;}"
        + ".brand span{color:#292344;}"
        + ".logout{background:#5b3cc4;color:#fff;padding:11px 18px;"
        + "border-radius:8px;text-decoration:none;font-weight:bold;}"
        + "main{max-width:1050px;margin:45px auto;padding:0 22px;}"
        + ".welcome{margin-bottom:30px;}"
        + ".welcome h1{margin-bottom:10px;font-size:30px;}"
        + ".welcome p{color:#716c83;line-height:1.6;}"
        + ".badge{display:inline-block;background:#e9e2ff;color:#5b3cc4;"
        + "padding:7px 12px;border-radius:20px;font-size:13px;"
        + "font-weight:bold;margin-top:8px;}"
        + ".grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));"
        + "gap:22px;}"
        + ".card{background:#fff;padding:28px;border:1px solid #ebe6f8;"
        + "border-radius:16px;box-shadow:0 5px 20px rgba(50,30,100,.04);}"
        + ".icon{font-size:35px;margin-bottom:15px;}"
        + ".card h2{font-size:21px;margin:0 0 12px;}"
        + ".card p{color:#716c83;line-height:1.7;font-size:14px;}"
        + ".button{display:inline-block;margin-top:12px;background:#5b3cc4;"
        + "color:#fff;text-decoration:none;padding:12px 17px;"
        + "border-radius:8px;font-weight:bold;}"
        + ".notice{margin-top:25px;background:#fff;padding:20px;"
        + "border-left:4px solid #7252d5;border-radius:8px;"
        + "color:#716c83;line-height:1.7;font-size:14px;}"
        + "footer{text-align:center;color:#817b94;padding:25px;font-size:13px;}"
        + "@media(max-width:650px){.grid{grid-template-columns:1fr;}"
        + ".welcome h1{font-size:25px;}main{margin-top:30px;}}"
        + "</style>"
        + "</head>"

        + "<body>"

        + "<header>"
        + "<div class='brand'>Campus<span>Saathi</span></div>"
        + "<a class='logout' href='" + contextPath + "/logout'>Logout</a>"
        + "</header>"

        + "<main>"

        + "<section class='welcome'>"
        + "<h1>Welcome, " + safeName + "!</h1>"
        + "<p>Manage CampusSaathi user accounts from one place.</p>"
        + "<span class='badge'>COLLEGE ADMINISTRATOR</span>"
        + "</section>"

        + "<section class='grid'>"

        + "<div class='card'>"
        + "<div class='icon'>&#128101;</div>"
        + "<h2>User Account Management</h2>"
        + "<p>Create accounts for HODs, teachers, students, and club leaders. "
        + "View existing accounts and manage their active status.</p>"
        + "<a class='button' href='" + contextPath
        + "/admin/users'>Manage Accounts &rarr;</a>"
        + "</div>"

        + "<div class='card'>"
        + "<div class='icon'>&#128274;</div>"
        + "<h2>Account Access</h2>"
        + "<p>Keep account access organized by role. "
        + "Each user signs in through the correct CampusSaathi portal.</p>"
        + "<span class='badge'>Role-Based Access</span>"
        + "</div>"

        + "</section>"

        + "<div class='notice'>"
        + "<strong>Administrator responsibility:</strong> "
        + "This portal is limited to user-account management. "
        + "Department information and academic announcements are handled "
        + "through the appropriate HOD and Teacher portals."
        + "</div>"

        + "</main>"

        + "<footer>"
        + "CampusSaathi &copy; 2026 | College Management &amp; Information Portal"
        + "</footer>"

        + "</body>"
        + "</html>"
    );
}


}
