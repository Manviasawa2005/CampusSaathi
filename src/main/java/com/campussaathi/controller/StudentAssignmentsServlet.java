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

@WebServlet("/student/assignments")
public class StudentAssignmentsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null) {

            response.sendRedirect(
                request.getContextPath() + "/login.jsp"
            );
            return;
        }

        if (!"STUDENT".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int studentId;

        try {
            studentId = Integer.parseInt(
                String.valueOf(session.getAttribute("userId"))
            );

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection()) {

            String cp = request.getContextPath();

            String fullName =
                (String) session.getAttribute("fullName");

            out.println("<!DOCTYPE html><html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>My Assignments | CampusSaathi</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box}");
            out.println("body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#272343}");
            out.println("header{background:#5b3cc4;color:white;padding:20px 6%;display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap}");
            out.println("header h1{margin:0}");
            out.println("header a{background:white;color:#5b3cc4;padding:10px 14px;border-radius:7px;text-decoration:none;font-weight:bold}");
            out.println("main{max-width:1050px;margin:30px auto;padding:0 18px}");
            out.println(".panel{background:white;padding:22px;border-radius:13px;border:1px solid #e8e2fa;margin-bottom:24px}");
            out.println(".muted{color:#716c83;font-size:13px;line-height:1.7}");
            out.println(".status{display:inline-block;background:#eee9ff;color:#5b3cc4;padding:7px 11px;border-radius:16px;font-size:12px;font-weight:bold}");
            out.println(".submitted{background:#dcfce7;color:#166534}");
            out.println(".pending{background:#fff1c2;color:#854d0e}");
            out.println(".empty{color:#716c83;padding:15px 0}");
            out.println("table{width:100%;border-collapse:collapse}");
            out.println("th,td{padding:12px;text-align:left;border-bottom:1px solid #e8e2fa;vertical-align:top}");
            out.println("th{background:#f5f3ff}");
            out.println(".table-wrap{overflow-x:auto}");
            out.println(".pdf-btn{display:inline-block;background:#5b3cc4;color:white;padding:9px 12px;border-radius:7px;text-decoration:none;font-weight:bold;font-size:13px;margin:3px 3px 3px 0;white-space:nowrap}");
            out.println(".download-btn{background:#087f5b}");
            out.println(".no-pdf{color:#817b94;font-size:13px}");
            out.println("footer{text-align:center;color:#817b94;padding:25px;font-size:13px}");
            out.println("@media(max-width:600px){main{margin:20px auto}.panel{padding:16px}th,td{padding:9px;font-size:13px}}");
            out.println("</style></head><body>");

            out.println("<header><div><h1>CampusSaathi</h1>");
            out.println("<p>My Assignments</p></div>");
            out.println("<a href='" + cp + "/student/dashboard'>Back to Dashboard</a></header>");

            out.println("<main><section class='panel'>");
            out.println("<h2>Welcome, " + esc(fullName) + "!</h2>");
            out.println("<p class='muted'>Check your assignments, question papers, notebook submission status, and marks recorded by your teacher.</p>");

            String sql =
                "SELECT a.assignment_id, a.assignment_name, "
                + "a.subject, a.description, a.max_marks, "
                + "a.batch, a.pdf_filename, "
                + "s.status, s.marks_obtained, s.submitted_at "
                + "FROM assignments a "
                + "JOIN users u ON u.user_id=? "
                + "LEFT JOIN assignment_submissions s "
                + "ON s.assignment_id=a.assignment_id "
                + "AND s.student_id=u.user_id "
                + "WHERE u.role='STUDENT' "
                + "AND u.batch IS NOT NULL "
                + "AND LOWER(TRIM(u.batch))=LOWER(TRIM(a.batch)) "
                + "ORDER BY a.created_at DESC";

            try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

                ps.setInt(1, studentId);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    out.println("<div class='table-wrap'><table>");
                    out.println("<tr>");
                    out.println("<th>Assignment</th>");
                    out.println("<th>Subject</th>");
                    out.println("<th>Status</th>");
                    out.println("<th>Marks</th>");
                    out.println("<th>Question Paper</th>");
                    out.println("</tr>");

                    while (rs.next()) {

                        found = true;

                        int assignmentId =
                            rs.getInt("assignment_id");

                        String assignmentName =
                            rs.getString("assignment_name");

                        String subject =
                            rs.getString("subject");

                        String status =
                            rs.getString("status");

                        String description =
                            rs.getString("description");

                        String pdfFilename =
                            rs.getString("pdf_filename");

                        if (status == null) {
                            status = "PENDING";
                        }

                        Object marks =
                            rs.getObject("marks_obtained");

                        String marksText =
                            marks == null
                                ? "Not graded yet"
                                : marks.toString()
                                    + " / "
                                    + rs.getBigDecimal("max_marks");

                        out.println("<tr>");

                        // Assignment name and description
                        out.println("<td><strong>"
                            + esc(assignmentName)
                            + "</strong>");

                        if (description != null
                                && !description.trim().isEmpty()) {

                            out.println("<br><span class='muted'>"
                                + esc(description)
                                + "</span>");
                        }

                        out.println("</td>");

                        // Subject
                        out.println("<td>"
                            + esc(subject)
                            + "</td>");

                        // Submission status
                        String statusClass =
                            "SUBMITTED".equals(status)
                                ? "submitted"
                                : "pending";

                        String statusLabel =
                            "SUBMITTED".equals(status)
                                ? "Submitted"
                                : "Pending";

                        out.println("<td><span class='status "
                            + statusClass + "'>"
                            + statusLabel
                            + "</span></td>");

                        // Marks
                        out.println("<td>"
                            + esc(marksText)
                            + "</td>");

                        // PDF viewing and downloading
                        out.println("<td>");

                        if (pdfFilename != null
                                && !pdfFilename.trim().isEmpty()) {

                            String pdfUrl =
                                cp + "/assignment-pdf?assignmentId="
                                    + assignmentId;

                            out.println("<a class='pdf-btn' "
                                + "href='" + pdfUrl
                                + "' target='_blank' "
                                + "rel='noopener noreferrer'>"
                                + "View PDF</a>");

                            out.println("<a class='pdf-btn download-btn' "
                                + "href='" + pdfUrl
                                + "&mode=download'>"
                                + "Download PDF</a>");

                        } else {

                            out.println("<span class='no-pdf'>"
                                + "No PDF attached"
                                + "</span>");
                        }

                        out.println("</td>");
                        out.println("</tr>");
                    }

                    out.println("</table></div>");

                    if (!found) {
                        out.println("<p class='empty'>"
                            + "No assignments are available for your "
                            + "registered batch yet."
                            + "</p>");
                    }
                }
            }

            out.println("</section></main>");

            out.println("<footer>"
                + "CampusSaathi &copy; 2026 | Student Portal"
                + "</footer>");

            out.println("</body></html>");

        } catch (SQLException e) {

            throw new ServletException(
                "Unable to load student assignments.", e
            );
        }
    }

    private String esc(String value) {

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