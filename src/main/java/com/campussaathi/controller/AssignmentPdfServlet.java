package com.campussaathi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/assignment-pdf")
public class AssignmentPdfServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Path UPLOAD_DIR =
        Paths.get("C:\\CampusSaathiUploads").toAbsolutePath().normalize();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Check login session.
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                request.getContextPath() + "/login.jsp"
            );
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));

        // Only teachers and students can access assignment PDFs.
        if (!"TEACHER".equals(role) && !"STUDENT".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int userId;
        int assignmentId;

        try {
            userId = Integer.parseInt(
                String.valueOf(session.getAttribute("userId"))
            );

            assignmentId = Integer.parseInt(
                request.getParameter("assignmentId")
            );

            if (assignmentId <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String pdfFilename = null;
        String assignmentBatch = null;
        String studentBatch = null;
        int createdBy = -1;

        // Retrieve the PDF and assignment ownership information.
        String sql =
            "SELECT a.pdf_filename, a.batch, a.created_by, "
            + "u.batch AS student_batch "
            + "FROM assignments a "
            + "LEFT JOIN users u ON u.user_id=? "
            + "WHERE a.assignment_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, assignmentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Assignment not found."
                    );
                    return;
                }

                pdfFilename = rs.getString("pdf_filename");
                assignmentBatch = rs.getString("batch");
                studentBatch = rs.getString("student_batch");
                createdBy = rs.getInt("created_by");
            }

        } catch (Exception e) {
            throw new ServletException(
                "Unable to retrieve assignment PDF information.", e
            );
        }

        // A teacher can access only assignments they created.
        if ("TEACHER".equals(role) && createdBy != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // A student can access only assignments for their own batch.
        if ("STUDENT".equals(role)) {

            if (studentBatch == null
                    || assignmentBatch == null
                    || !studentBatch.trim().equalsIgnoreCase(
                        assignmentBatch.trim())) {

                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        if (pdfFilename == null || pdfFilename.trim().isEmpty()) {
            response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "No PDF is attached to this assignment."
            );
            return;
        }

        // Prevent directory traversal by restricting the resolved path.
        Path pdfPath = UPLOAD_DIR.resolve(pdfFilename)
                                 .normalize();

        if (!pdfPath.startsWith(UPLOAD_DIR)
                || !Files.isRegularFile(pdfPath)) {

            response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "PDF file not found."
            );
            return;
        }

        String mode = request.getParameter("mode");

        boolean download = "download".equalsIgnoreCase(mode);

        response.setContentType("application/pdf");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader(
            "Content-Disposition",
            (download ? "attachment" : "inline")
                + "; filename=\"" + pdfPath.getFileName() + "\""
        );
        response.setContentLengthLong(Files.size(pdfPath));

        // Send the PDF to the browser.
        try (InputStream input = Files.newInputStream(pdfPath);
             OutputStream output = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            throw new ServletException(
                "Unable to serve assignment PDF.", e
            );
        }
    }
}