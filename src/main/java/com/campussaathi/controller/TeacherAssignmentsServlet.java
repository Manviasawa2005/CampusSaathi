package com.campussaathi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/teacher/assignments")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 11 * 1024 * 1024
)
public class TeacherAssignmentsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final long MAX_PDF_SIZE = 10L * 1024 * 1024;

    private static final Path UPLOAD_DIR =
        Paths.get("C:\\CampusSaathiUploads");

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

        if (!"TEACHER".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection()) {

            String cp = request.getContextPath();

            int teacherId = Integer.parseInt(
                String.valueOf(session.getAttribute("userId"))
            );

            out.println("<!DOCTYPE html><html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>Teacher Assignments | CampusSaathi</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box}");
            out.println("html{scroll-behavior:smooth;scroll-padding-top:20px}");
            out.println("body{margin:0;font-family:Arial,sans-serif;background:#f5f3ff;color:#272343}");
            out.println("header{background:#5b3cc4;color:white;padding:20px 6%;display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap}");
            out.println("header h1{margin:0}");
            out.println("header a{background:white;color:#5b3cc4;padding:10px 14px;border-radius:7px;text-decoration:none;font-weight:bold}");
            out.println("main{max-width:1100px;margin:30px auto;padding:0 18px}");
            out.println(".panel{background:white;padding:22px;border-radius:13px;border:1px solid #e8e2fa;margin-bottom:24px}");
            out.println("label{display:block;margin:14px 0 6px;font-weight:bold}");
            out.println("input,select,textarea{width:100%;padding:11px;border:1px solid #d9d1ef;border-radius:7px;font:inherit}");
            out.println("textarea{min-height:75px}");
            out.println("button,.btn{display:inline-block;background:#5b3cc4;color:white;border:0;padding:10px 14px;border-radius:7px;font-weight:bold;cursor:pointer;text-decoration:none;margin-top:12px}");
            out.println(".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:16px}");
            out.println(".card{border:1px solid #e8e2fa;border-radius:10px;padding:16px;margin:12px 0;overflow-wrap:anywhere}");
            out.println(".muted{color:#716c83;font-size:13px;line-height:1.6}");
            out.println(".message{background:#eee9ff;padding:12px;border-radius:7px;margin-bottom:15px}");
            out.println("table{width:100%;border-collapse:collapse;margin-top:15px}");
            out.println("th,td{padding:10px;border-bottom:1px solid #e8e2fa;text-align:left}");
            out.println("th{background:#f5f3ff}");
            out.println("td form{min-width:90px}");
            out.println(".table-wrap{overflow-x:auto}");
            out.println(".inline{display:flex;gap:8px;align-items:center;flex-wrap:wrap}");
            out.println(".inline input{min-width:85px}");
            out.println("#student-search{scroll-margin-top:20px}");
            out.println("</style></head><body>");

            out.println("<header><div><h1>CampusSaathi</h1>");
            out.println("<p>Teacher Assignment Management</p></div>");
            out.println("<a href='" + cp + "/teacher/dashboard'>Back to Dashboard</a></header>");
            out.println("<main>");

            String msg = request.getParameter("message");

            if ("created".equals(msg)) {
                out.println("<div class='message'>Assignment created successfully.</div>");
            } else if ("updated".equals(msg)) {
                out.println("<div class='message'>Student submission record updated.</div>");
            } else if ("invalid".equals(msg)) {
                out.println("<div class='message'>Please check your input and PDF file, then try again.</div>");
            } else if ("duplicate".equals(msg)) {
                out.println("<div class='message'>This assignment already exists for that subject and batch.</div>");
            } else if ("error".equals(msg)) {
                out.println("<div class='message'>The operation failed. Please check the input and try again.</div>");
            }

            // Create assignment form
            out.println("<section class='panel'><h2>Create Assignment</h2>");

            out.println("<form method='post' action='"
                + cp + "/teacher/assignments' enctype='multipart/form-data'>");

            out.println("<input type='hidden' name='action' value='create'>");

            out.println("<div class='grid'>");

            out.println("<div><label>Assignment Name</label>");
            out.println("<input name='assignmentName' maxlength='100' placeholder='ASS1' required></div>");

            out.println("<div><label>Subject</label>");
            out.println("<input name='subject' maxlength='100' placeholder='Python' required></div>");

            out.println("<div><label>Student Batch</label>");
            out.println("<input name='batch' maxlength='50' placeholder='AI&DS B' required></div>");

            out.println("<div><label>Maximum Marks</label>");
            out.println("<input type='number' name='maxMarks' min='1' max='999.99' step='0.01' value='10' required></div>");

            out.println("</div>");

            out.println("<label>Description (optional)</label>");
            out.println("<textarea name='description' maxlength='2000' placeholder='Assignment instructions'></textarea>");

            out.println("<label>Question Paper PDF (optional, maximum 10 MB)</label>");
            out.println("<input type='file' name='pdfFile' accept='.pdf,application/pdf'>");
            out.println("<p class='muted'>Upload a PDF question paper. You can also create an assignment without a PDF.</p>");

            out.println("<button type='submit'>Create Assignment</button>");
            out.println("</form></section>");

            // Existing assignments
            out.println("<section class='panel'><h2>Assignments and Student Records</h2>");
            out.println("<p class='muted'>Select an assignment to view its registered students. Only students whose batch matches the assignment are listed.</p>");

            String selectedId = request.getParameter("assignmentId");
            int assignmentId = -1;

            String assignmentSql =
                "SELECT assignment_id, assignment_name, subject, batch, "
                + "max_marks, pdf_filename "
                + "FROM assignments WHERE created_by=? "
                + "ORDER BY created_at DESC";

            try (PreparedStatement ps =
                     con.prepareStatement(assignmentSql)) {

                ps.setInt(1, teacherId);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {

                        found = true;

                        int id = rs.getInt("assignment_id");

                        out.println("<div class='card'><strong>"
                            + esc(rs.getString("assignment_name"))
                            + " — " + esc(rs.getString("subject"))
                            + "</strong>");

                        out.println("<p class='muted'>Batch: "
                            + esc(rs.getString("batch"))
                            + " | Maximum marks: "
                            + rs.getBigDecimal("max_marks")
                            + "</p>");

                        String pdfFilename =
                            rs.getString("pdf_filename");

                        if (pdfFilename != null
                                && !pdfFilename.trim().isEmpty()) {
                            out.println("<p class='muted'>Question paper: PDF attached</p>");
                        } else {
                            out.println("<p class='muted'>Question paper: No PDF uploaded</p>");
                        }

                        out.println("<a class='btn' href='" + cp
                            + "/teacher/assignments?assignmentId=" + id
                            + "#student-search'>Search Students / Mark Submission</a>");

                        out.println("</div>");

                        if (selectedId != null
                                && selectedId.equals(String.valueOf(id))) {
                            assignmentId = id;
                        }
                    }

                    if (!found) {
                        out.println("<p class='muted'>No assignments created yet.</p>");
                    }
                }
            }

            if (assignmentId != -1) {
                displayStudents(
                    out,
                    con,
                    cp,
                    assignmentId,
                    teacherId,
                    request.getParameter("search")
                );
            }

            out.println("</section></main></body></html>");

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException(
                "Unable to load assignments.", e
            );
        }
    }

    private void displayStudents(PrintWriter out,
                                 Connection con,
                                 String cp,
                                 int assignmentId,
                                 int teacherId,
                                 String search)
            throws SQLException {

        String verifySql =
            "SELECT max_marks, batch FROM assignments "
            + "WHERE assignment_id=? AND created_by=?";

        BigDecimal maxMarks;
        String batch;

        try (PreparedStatement ps =
                 con.prepareStatement(verifySql)) {

            ps.setInt(1, assignmentId);
            ps.setInt(2, teacherId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    out.println("<p class='muted'>Assignment not found.</p>");
                    return;
                }

                maxMarks = rs.getBigDecimal("max_marks");
                batch = rs.getString("batch");
            }
        }

        out.println("<div id='student-search'>");
        out.println("<h3>Registered Students</h3>");

        out.println("<form method='get' action='"
            + cp + "/teacher/assignments#student-search'>");

        out.println("<input type='hidden' name='assignmentId' value='"
            + assignmentId + "'>");

        out.println("<label>Search Student by Name</label>");
        out.println("<input name='search' maxlength='100' value='"
            + esc(search == null ? "" : search)
            + "' placeholder='Enter student name'>");

        out.println("<button type='submit'>Search</button>");
        out.println("</form>");
        out.println("</div>");

        String studentSql =
            "SELECT u.user_id, u.full_name, s.status, s.marks_obtained "
            + "FROM users u "
            + "LEFT JOIN assignment_submissions s "
            + "ON s.student_id=u.user_id AND s.assignment_id=? "
            + "WHERE u.role='STUDENT' AND u.batch IS NOT NULL "
            + "AND LOWER(TRIM(u.batch))=LOWER(TRIM(?)) "
            + "AND (? IS NULL OR u.full_name LIKE ?) "
            + "ORDER BY u.full_name";

        try (PreparedStatement ps =
                 con.prepareStatement(studentSql)) {

            ps.setInt(1, assignmentId);
            ps.setString(2, batch);

            String searchTerm =
                (search == null || search.trim().isEmpty())
                    ? null
                    : search.trim();

            ps.setString(3, searchTerm);
            ps.setString(
                4,
                "%" + (searchTerm == null ? "" : searchTerm) + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {

                out.println("<div class='table-wrap'><table>");
                out.println("<tr><th>Student</th><th>Status</th><th>Marks</th><th>Update</th></tr>");

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    int studentId = rs.getInt("user_id");
                    String studentName = rs.getString("full_name");
                    String status = rs.getString("status");
                    BigDecimal marks =
                        rs.getBigDecimal("marks_obtained");

                    if (status == null) {
                        status = "PENDING";
                    }

                    out.println("<tr><td>" + esc(studentName)
                        + "</td><td>" + esc(status)
                        + "</td><td>"
                        + (marks == null ? "—" : marks.toPlainString())
                        + "</td><td>");

                    out.println("<form method='post' action='"
                        + cp + "/teacher/assignments'>");

                    out.println("<input type='hidden' name='action' value='mark'>");

                    out.println("<input type='hidden' name='assignmentId' value='"
                        + assignmentId + "'>");

                    out.println("<input type='hidden' name='studentId' value='"
                        + studentId + "'>");

                    out.println("<label>Status</label><select name='status'>");

                    out.println("<option value='PENDING'"
                        + ("PENDING".equals(status) ? " selected" : "")
                        + ">Pending</option>");

                    out.println("<option value='SUBMITTED'"
                        + ("SUBMITTED".equals(status) ? " selected" : "")
                        + ">Submitted</option>");

                    out.println("</select>");

                    out.println("<label>Marks (optional)</label>");

                    out.println("<input type='number' name='marks' min='0' max='"
                        + maxMarks.toPlainString()
                        + "' step='0.01' value='"
                        + (marks == null ? "" : marks.toPlainString())
                        + "'>");

                    out.println("<button type='submit'>Save</button>");
                    out.println("</form></td></tr>");
                }

                out.println("</table></div>");

                if (!found) {
                    out.println("<p class='muted'>No students found for batch '"
                        + esc(batch) + "'"
                        + (search == null || search.trim().isEmpty()
                            ? "."
                            : " matching your search.")
                        + " Check the registered students' batch values.</p>");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null) {
            response.sendRedirect(
                request.getContextPath() + "/login.jsp"
            );
            return;
        }

        if (!"TEACHER".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int teacherId;

        try {
            teacherId = Integer.parseInt(
                String.valueOf(session.getAttribute("userId"))
            );
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = request.getParameter("action");

        try (Connection con = DBConnection.getConnection()) {

            if ("create".equals(action)) {

                String name = trim(request.getParameter("assignmentName"));
                String subject = trim(request.getParameter("subject"));
                String batch = trim(request.getParameter("batch"));
                String description = trim(request.getParameter("description"));
                String marksText = trim(request.getParameter("maxMarks"));

                if (name.isEmpty() || subject.isEmpty() || batch.isEmpty()
                        || name.length() > 100
                        || subject.length() > 100
                        || batch.length() > 50
                        || description.length() > 2000) {

                    redirect(request, response, "invalid");
                    return;
                }

                BigDecimal maxMarks;

                try {
                    maxMarks = new BigDecimal(marksText);

                    if (maxMarks.compareTo(BigDecimal.ZERO) <= 0
                            || maxMarks.compareTo(
                                new BigDecimal("999.99")) > 0) {

                        redirect(request, response, "invalid");
                        return;
                    }

                } catch (NumberFormatException e) {
                    redirect(request, response, "invalid");
                    return;
                }

                // PDF is optional.
                Part pdfPart = request.getPart("pdfFile");

                boolean hasPdf =
                    pdfPart != null
                    && pdfPart.getSubmittedFileName() != null
                    && !pdfPart.getSubmittedFileName().trim().isEmpty();

                Path savedPdfPath = null;
                String pdfFilename = null;

                if (hasPdf) {

                    if (pdfPart.getSize() <= 0
                            || pdfPart.getSize() > MAX_PDF_SIZE
                            || !isPdf(pdfPart)) {

                        redirect(request, response, "invalid");
                        return;
                    }

                    pdfFilename = UUID.randomUUID() + ".pdf";

                    try {
                        Files.createDirectories(UPLOAD_DIR);

                        savedPdfPath = UPLOAD_DIR.resolve(pdfFilename);

                        try (InputStream input =
                                 pdfPart.getInputStream()) {

                            Files.copy(input, savedPdfPath);
                        }

                    } catch (IOException e) {

                        deleteQuietly(savedPdfPath);

                        throw new ServletException(
                            "Unable to save the uploaded PDF.", e
                        );
                    }
                }

                String insert =
                    "INSERT INTO assignments "
                    + "(assignment_name, subject, batch, description, "
                    + "max_marks, created_by, pdf_filename) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps =
                         con.prepareStatement(insert)) {

                    ps.setString(1, name);
                    ps.setString(2, subject);
                    ps.setString(3, batch);

                    ps.setString(
                        4,
                        description.isEmpty() ? null : description
                    );

                    ps.setBigDecimal(5, maxMarks);
                    ps.setInt(6, teacherId);
                    ps.setString(7, pdfFilename);

                    ps.executeUpdate();

                } catch (SQLException e) {

                    // Remove the newly uploaded file if insertion fails.
                    deleteQuietly(savedPdfPath);

                    throw e;
                }

                redirect(request, response, "created");

            } else if ("mark".equals(action)) {

                int assignmentId;
                int studentId;

                try {
                    assignmentId = Integer.parseInt(
                        request.getParameter("assignmentId")
                    );

                    studentId = Integer.parseInt(
                        request.getParameter("studentId")
                    );

                } catch (NumberFormatException e) {
                    redirect(request, response, "invalid");
                    return;
                }

                String status = request.getParameter("status");
                String marksText = trim(request.getParameter("marks"));

                if (!"PENDING".equals(status)
                        && !"SUBMITTED".equals(status)) {

                    redirect(request, response, "invalid");
                    return;
                }

                BigDecimal marks = null;

                if (!marksText.isEmpty()) {

                    try {
                        marks = new BigDecimal(marksText);

                        if (marks.compareTo(BigDecimal.ZERO) < 0) {
                            redirect(request, response, "invalid");
                            return;
                        }

                    } catch (NumberFormatException e) {
                        redirect(request, response, "invalid");
                        return;
                    }
                }

                // Check ownership, student role, batch, and maximum marks.
                String check =
                    "SELECT a.max_marks, a.batch AS assignment_batch, "
                    + "u.batch AS student_batch "
                    + "FROM assignments a "
                    + "JOIN users u ON u.user_id=? "
                    + "WHERE a.assignment_id=? AND a.created_by=? "
                    + "AND u.role='STUDENT'";

                BigDecimal maxMarks;
                String assignmentBatch;
                String studentBatch;

                try (PreparedStatement ps =
                         con.prepareStatement(check)) {

                    ps.setInt(1, studentId);
                    ps.setInt(2, assignmentId);
                    ps.setInt(3, teacherId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            redirect(request, response, "error");
                            return;
                        }

                        maxMarks = rs.getBigDecimal("max_marks");
                        assignmentBatch =
                            rs.getString("assignment_batch");
                        studentBatch = rs.getString("student_batch");
                    }
                }

                if (studentBatch == null
                        || assignmentBatch == null
                        || !assignmentBatch.trim()
                            .equalsIgnoreCase(studentBatch.trim())
                        || (marks != null
                            && marks.compareTo(maxMarks) > 0)
                        || (marks != null
                            && !"SUBMITTED".equals(status))) {

                    redirect(request, response, "invalid");
                    return;
                }

                String save =
                    "INSERT INTO assignment_submissions "
                    + "(assignment_id, student_id, status, "
                    + "marks_obtained, marked_by, submitted_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "status=VALUES(status), "
                    + "marks_obtained=VALUES(marks_obtained), "
                    + "marked_by=VALUES(marked_by), "
                    + "submitted_at=IF(VALUES(status)='SUBMITTED', "
                    + "COALESCE(submitted_at, CURRENT_TIMESTAMP), NULL)";

                try (PreparedStatement ps =
                         con.prepareStatement(save)) {

                    ps.setInt(1, assignmentId);
                    ps.setInt(2, studentId);
                    ps.setString(3, status);

                    if (marks == null) {
                        ps.setNull(4, java.sql.Types.DECIMAL);
                    } else {
                        ps.setBigDecimal(4, marks);
                    }

                    ps.setInt(5, teacherId);

                    if ("SUBMITTED".equals(status)) {
                        ps.setTimestamp(
                            6,
                            new java.sql.Timestamp(
                                System.currentTimeMillis()
                            )
                        );
                    } else {
                        ps.setNull(6, java.sql.Types.TIMESTAMP);
                    }

                    ps.executeUpdate();
                }

                redirect(request, response, "updated");

            } else {
                redirect(request, response, "invalid");
            }

        } catch (SQLException e) {

            if ("create".equals(action)
                    && e.getErrorCode() == 1062) {

                redirect(request, response, "duplicate");
                return;
            }

            throw new ServletException(
                "Unable to update assignment records.", e
            );
        }
    }

    private boolean isPdf(Part part) throws IOException {

        byte[] signature = "%PDF-".getBytes(
            StandardCharsets.US_ASCII
        );

        try (InputStream input = part.getInputStream()) {

            byte[] header = new byte[signature.length];

            int bytesRead = input.read(header);

            if (bytesRead != signature.length) {
                return false;
            }

            for (int i = 0; i < signature.length; i++) {
                if (header[i] != signature[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    private void deleteQuietly(Path path) {

        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            getServletContext().log(
                "Unable to remove an unused assignment PDF.", e
            );
        }
    }

    private void redirect(HttpServletRequest request,
                          HttpServletResponse response,
                          String message)
            throws IOException {

        response.sendRedirect(
            request.getContextPath()
                + "/teacher/assignments?message=" + message
        );
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
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