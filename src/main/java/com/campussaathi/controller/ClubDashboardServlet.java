
package com.campussaathi.controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/club/dashboard")
public class ClubDashboardServlet extends HttpServlet {

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

        if (!"CLUB_LEADER".equals(role)) {
            response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "You are not authorized to access the Club Leader Dashboard."
            );
            return;
        }

        String fullName = (String) session.getAttribute("fullName");

        if (fullName == null) {
            fullName = "Club Leader";
        }

        String contextPath = request.getContextPath();

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>CampusSaathi | Club Leader Dashboard</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box;}");
            out.println("body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#272343;}");
            out.println(".header{background:#5b3cc4;color:white;padding:22px 6%;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:15px;}");
            out.println(".header h1{margin:0;font-size:27px;}");
            out.println(".header p{margin:7px 0 0;color:#e6deff;}");
            out.println(".logout{background:white;color:#5b3cc4;padding:11px 17px;border-radius:8px;text-decoration:none;font-weight:bold;}");
            out.println(".container{max-width:900px;margin:35px auto;padding:0 22px;}");
            out.println(".welcome{margin-bottom:30px;}");
            out.println(".welcome h2{font-size:28px;margin-bottom:10px;}");
            out.println(".welcome p{color:#716c83;line-height:1.7;}");
            out.println(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:22px;}");
            out.println(".card{background:white;border:1px solid #e8e2fa;border-radius:15px;padding:25px;box-shadow:0 5px 18px rgba(50,30,100,.05);}");
            out.println(".icon{font-size:34px;}");
            out.println(".card h3{margin:15px 0 10px;}");
            out.println(".card p{color:#716c83;font-size:14px;line-height:1.7;}");
            out.println(".action-btn{display:inline-block;margin-top:12px;background:#5b3cc4;color:white;padding:11px 15px;border-radius:8px;text-decoration:none;font-size:14px;font-weight:bold;}");
            out.println(".action-btn:hover{background:#4528a5;}");
            out.println(".badge{display:inline-block;margin-top:12px;background:#eee9ff;color:#5b3cc4;padding:7px 11px;border-radius:20px;font-size:12px;font-weight:bold;}");
            out.println(".notice{margin-top:28px;padding:20px;background:#eee9ff;border-radius:12px;line-height:1.8;}");
            out.println("footer{text-align:center;padding:30px;color:#817b94;font-size:13px;}");
            out.println("@media(max-width:600px){.container{margin:22px auto;padding:0 15px;}.header{padding:20px;}.welcome h2{font-size:23px;}}");
            out.println("</style>");

            out.println("</head>");
            out.println("<body>");

            // Header
            out.println("<header class='header'>");
            out.println("<div><h1>CampusSaathi</h1><p>Club Leader Portal</p></div>");
            out.println("<a class='logout' href='" + contextPath + "/logout'>Log Out</a>");
            out.println("</header>");

            // Main dashboard
            out.println("<main class='container'>");

            out.println("<section class='welcome'>");
            out.println("<h2>Welcome, " + escapeHtml(fullName) + "!</h2>");
            out.println("<p>View club announcements and report issues to the HOD.</p>");
            out.println("</section>");

            out.println("<section class='grid'>");

            // Complaints
            out.println("<article class='card'>");
            out.println("<div class='icon'>&#128221;</div>");
            out.println("<h3>Complaints</h3>");
            out.println("<p>Submit a complaint about a club-related or college-related issue to the HOD.</p>");
            out.println("<a class='action-btn' href='" + contextPath + "/club/complaints'>Submit a Complaint &rarr;</a>");
            out.println("<br><span class='badge'>To Be Connected</span>");
            out.println("</article>");

            // Announcements
            out.println("<article class='card'>");
            out.println("<div class='icon'>&#128226;</div>");
            out.println("<h3>Announcements</h3>");
            out.println("<p>View important announcements intended for club leaders.</p>");
            out.println("<a class='action-btn' href='" + contextPath + "/my-announcements'>View Announcements &rarr;</a>");
            out.println("<br><span class='badge'>Working</span>");
            out.println("</article>");

            out.println("</section>");

            out.println("<div class='notice'>");
            out.println("<strong>Your Club Workspace</strong><br>");
            out.println("Use Announcements to view notices from the HOD. ");
            out.println("The complaint form will be connected next.");
            out.println("</div>");

            out.println("</main>");

            out.println("<footer>CampusSaathi &copy; 2026 | Club Leader Portal</footer>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "Club Leader";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}

