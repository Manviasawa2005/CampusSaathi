
package com.campussaathi.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/hod/dashboard")
public class HODDashboardServlet extends HttpServlet {

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
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You are not authorized to access the HOD Dashboard.");
            return;
        }

        String fullName = (String) session.getAttribute("fullName");

        if (fullName == null) {
            fullName = "HOD";
        }

        String safeName = escapeHtml(fullName);
        String contextPath = request.getContextPath();

        response.setContentType("text/html;charset=UTF-8");

        String html =
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>CampusSaathi | HOD Dashboard</title>" +

            "<style>" +
            "*{box-sizing:border-box;}" +
            "body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#272343;}" +
            ".header{background:#5b3cc4;color:white;padding:22px 6%;display:flex;" +
            "justify-content:space-between;align-items:center;flex-wrap:wrap;gap:15px;}" +
            ".header h1{margin:0;font-size:28px;}" +
            ".header p{margin:7px 0 0;color:#e6deff;}" +
            ".logout{background:white;color:#5b3cc4;padding:11px 17px;" +
            "border-radius:8px;text-decoration:none;font-weight:bold;}" +
            ".container{max-width:1100px;margin:35px auto;padding:0 22px;}" +
            ".welcome{margin-bottom:30px;}" +
            ".welcome h2{font-size:29px;margin-bottom:10px;}" +
            ".welcome p{color:#716c83;line-height:1.7;}" +
            ".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:22px;}" +
            ".card{background:white;border:1px solid #e8e2fa;border-radius:15px;padding:25px;" +
            "box-shadow:0 5px 18px rgba(50,30,100,.05);}" +
            ".icon{font-size:34px;}" +
            ".card h3{margin:15px 0 10px;font-size:19px;}" +
            ".card p{color:#716c83;font-size:14px;line-height:1.7;}" +
            ".action-btn{display:inline-block;margin-top:12px;padding:11px 16px;" +
            "background:#5b3cc4;color:white;text-decoration:none;border-radius:8px;font-weight:bold;}" +
            ".action-btn:hover{background:#43289e;}" +
            ".badge{display:inline-block;margin-top:15px;background:#eee9ff;color:#5b3cc4;" +
            "padding:7px 11px;border-radius:20px;font-size:12px;font-weight:bold;}" +
            "footer{text-align:center;padding:30px;color:#817b94;font-size:13px;}" +
            "@media(max-width:600px){.header h1{font-size:23px;}.welcome h2{font-size:24px;}}" +
            "</style>" +

            "</head><body>" +

            "<header class='header'>" +
            "<div><h1>CampusSaathi</h1><p>Head of Department Portal</p></div>" +
            "<a class='logout' href='" + contextPath + "/logout'>Log Out</a>" +
            "</header>" +

            "<main class='container'>" +
            "<section class='welcome'>" +
            "<h2>Welcome, " + safeName + "!</h2>" +
            "<p>Manage department announcements, review complaints, and access college information from one place.</p>" +
            "</section>" +

            "<section class='grid'>" +

            "<article class='card'>" +
            "<div class='icon'>&#128226;</div>" +
            "<h3>Announcements</h3>" +
            "<p>Publish important announcements for teachers, students, and club leaders.</p>" +
            "<a class='action-btn' href='" + contextPath + "/hod/announcements'>Manage Announcements &rarr;</a>" +
            "<br><span class='badge'>Working</span>" +
            "</article>" +

            "<article class='card'>" +
            "<div class='icon'>&#128221;</div>" +
            "<h3>Complaints</h3>" +
            "<p>Review complaints submitted by teachers and club leaders.</p>" +
            "<a class='action-btn' href='" + contextPath + "/hod/complaints'>View Complaints &rarr;</a>" +
            "<br><span class='badge'>View Complaints</span>" +
            "</article>" +

            "<article class='card'>" +
            "<div class='icon'>&#128218;</div>" +
            "<h3>College Information</h3>" +
            "<p>View college guidelines, placement information, events, and technical interview topics.</p>" +
            "<a class='action-btn' href='" + contextPath + "/hod/college-info'>View Information &rarr;</a>" +
            "<br><span class='badge'>Available</span>" +
            "</article>" +

            "</section></main>" +

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

