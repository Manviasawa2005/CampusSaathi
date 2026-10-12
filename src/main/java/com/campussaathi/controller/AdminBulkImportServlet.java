
package com.campussaathi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/admin/bulk-import")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 6 * 1024 * 1024
)
public class AdminBulkImportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final String[] ALLOWED_ROLES = {
        "HOD", "TEACHER", "STUDENT", "CLUB_LEADER"
    };

    private static final String PASSWORD_CHARACTERS =
        "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$!";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        if (!isAdministrator(request, response)) {
            return;
        }

        String contextPath = request.getContextPath();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>Bulk Import | CampusSaathi</title>");

        out.println("<style>");
        out.println("body{margin:0;background:#f5f3ff;font-family:Arial,sans-serif;color:#292442}");
        out.println(".container{max-width:800px;margin:45px auto;padding:20px}");
        out.println(".card{background:white;padding:30px;border-radius:14px;box-shadow:0 4px 18px #ddd8f5}");
        out.println("h1{color:#5145b5}");
        out.println("input{display:block;margin:15px 0;padding:12px;border:1px solid #ccc;border-radius:7px;width:100%;box-sizing:border-box}");
        out.println("button,a{display:inline-block;padding:12px 17px;border:0;border-radius:7px;text-decoration:none;font-weight:bold;cursor:pointer;margin:8px 5px 0 0}");
        out.println("button{background:#5145b5;color:white}");
        out.println("a{background:#ede9fe;color:#5145b5}");
        out.println(".note{background:#f5f3ff;padding:15px;border-radius:8px;line-height:1.7}");
        out.println("</style></head><body>");

        out.println("<div class='container'><div class='card'>");
        out.println("<h1>Bulk Import Accounts</h1>");
        out.println("<p>Upload a CSV file to create multiple CampusSaathi accounts.</p>");

        out.println("<div class='note'>");
        out.println("<strong>Before uploading:</strong><br>");
        out.println("1. Download the updated CSV template.<br>");
        out.println("2. Enter Full Name, Email, Role, Initial Password, and Batch.<br>");
        out.println("3. Student accounts require a batch name, such as AI&amp;DS-2024.<br>");
        out.println("4. For non-student accounts, Batch can be left blank.<br>");
        out.println("5. Leave Initial Password blank to generate a random initial password automatically.<br>");
        out.println("6. Alternatively, provide a password containing 12–128 characters.");
        out.println("</div>");

        out.println("<form method='post' enctype='multipart/form-data' action='"
                + contextPath + "/admin/bulk-import'>");
        out.println("<label for='csvFile'><strong>Select CSV file (maximum 5 MB)</strong></label>");
        out.println("<input type='file' id='csvFile' name='csvFile' accept='.csv,text/csv' required>");
        out.println("<button type='submit'>Import Accounts</button>");
        out.println("</form>");

        out.println("<a href='" + contextPath + "/admin/csv-template'>Download CSV Template</a>");
        out.println("<a href='" + contextPath + "/admin/users'>Back to Account Management</a>");

        out.println("</div></div></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdministrator(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        List<String[]> results = new ArrayList<>();

        int created = 0;
        int failed = 0;
        int skipped = 0;

        Part filePart;

        try {
            filePart = request.getPart("csvFile");
        } catch (IllegalStateException e) {
            showError(response, "The uploaded file is too large. Maximum size is 5 MB.");
            return;
        }

        if (filePart == null
                || filePart.getSize() == 0
                || filePart.getSubmittedFileName() == null
                || !filePart.getSubmittedFileName().toLowerCase().endsWith(".csv")) {

            showError(response, "Please select a non-empty CSV file.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    filePart.getInputStream(),
                    StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();

            if (headerLine == null) {
                showError(response, "The CSV file is empty.");
                return;
            }

            List<String> headers =
                parseCsvLine(headerLine.replace("\uFEFF", ""));

            int nameIndex = findHeader(headers, "Full Name");
            int emailIndex = findHeader(headers, "Email");
            int roleIndex = findHeader(headers, "Role");
            int passwordIndex = findHeader(headers, "Initial Password");
            int batchIndex = findHeader(headers, "Batch");

            if (nameIndex < 0 || emailIndex < 0
                    || roleIndex < 0 || passwordIndex < 0
                    || batchIndex < 0) {

                showError(response,
                    "Invalid CSV headings. Download the updated CampusSaathi CSV template containing Full Name, Email, Role, Initial Password, and Batch.");
                return;
            }

            String line;
            int lineNumber = 1;

            try (Connection con = DBConnection.getConnection()) {

                con.setAutoCommit(false);

                String sql =
                    "INSERT INTO users "
                    + "(full_name, email, password_hash, role, status, batch) "
                    + "VALUES (?, ?, ?, ?, 'ACTIVE', ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    while ((line = reader.readLine()) != null) {

                        lineNumber++;

                        if (line.isBlank()) {
                            continue;
                        }

                        List<String> columns = parseCsvLine(line);

                        String name = getColumn(columns, nameIndex).trim();
                        String email = getColumn(columns, emailIndex).trim();
                        String role = getColumn(columns, roleIndex).trim().toUpperCase();
                        String password = getColumn(columns, passwordIndex).trim();
                        String batch = getColumn(columns, batchIndex).trim();

                        if (name.isEmpty()
                                || name.length() > 100
                                || email.length() > 150
                                || !EMAIL_PATTERN.matcher(email).matches()) {

                            failed++;
                            results.add(new String[] {
                                String.valueOf(lineNumber),
                                email,
                                "Failed: invalid name or email"
                            });
                            continue;
                        }

                        if (!isAllowedRole(role)) {

                            failed++;
                            results.add(new String[] {
                                String.valueOf(lineNumber),
                                email,
                                "Failed: invalid role"
                            });
                            continue;
                        }

                        if ("STUDENT".equals(role)) {

                            if (batch.isEmpty() || batch.length() > 50) {
                                failed++;
                                results.add(new String[] {
                                    String.valueOf(lineNumber),
                                    email,
                                    "Failed: students require a batch of 1–50 characters"
                                });
                                continue;
                            }

                        } else {
                            batch = null;
                        }

                        if (!password.isEmpty()
                                && (password.length() < 12 || password.length() > 128)) {

                            failed++;
                            results.add(new String[] {
                                String.valueOf(lineNumber),
                                email,
                                "Failed: password must contain 12–128 characters"
                            });
                            continue;
                        }

                        boolean generatedPassword = password.isEmpty();

                        if (generatedPassword) {
                            password = generatePassword();
                        }

                        String passwordHash;

                        try {
                            passwordHash = PasswordUtil.hashPassword(password);
                        } catch (Exception e) {
                            getServletContext().log(
                                "Could not hash password for CSV row " + lineNumber, e);
                            failed++;
                            results.add(new String[] {
                                String.valueOf(lineNumber),
                                email,
                                "Failed: password processing error"
                            });
                            continue;
                        }

                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, passwordHash);
                        ps.setString(4, role);
                        ps.setString(5, batch);

                        try {

                            ps.executeUpdate();

                            created++;

                            results.add(new String[] {
                                String.valueOf(lineNumber),
                                email,
                                generatedPassword
                                    ? "Created — generated password: " + password
                                    : "Created — supplied password"
                            });

                        } catch (SQLException e) {

                            if ("23000".equals(e.getSQLState())) {

                                skipped++;

                                results.add(new String[] {
                                    String.valueOf(lineNumber),
                                    email,
                                    "Skipped: email already exists"
                                });

                            } else {

                                failed++;

                                results.add(new String[] {
                                    String.valueOf(lineNumber),
                                    email,
                                    "Failed: database error"
                                });

                                getServletContext().log(
                                    "Bulk import database error on CSV row "
                                    + lineNumber, e);
                            }
                        }
                    }
                }

                con.commit();

            } catch (Exception e) {

                getServletContext().log("Bulk account import failed.", e);

                showError(response,
                    "The import could not be completed because of a database or server error. Check the server log.");
                return;
            }

        } catch (IOException e) {

            getServletContext().log("Could not read uploaded CSV.", e);
            showError(response, "Unable to read the CSV file.");
            return;
        }

        printResults(response.getWriter(), request.getContextPath(),
                     created, skipped, failed, results);
    }

    private boolean isAdministrator(HttpServletRequest request,
                                    HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }

        if (!"COLLEGE_ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Only the College Administrator can import accounts.");
            return false;
        }

        return true;
    }

    private boolean isAllowedRole(String role) {

        for (String allowed : ALLOWED_ROLES) {
            if (allowed.equals(role)) {
                return true;
            }
        }

        return false;
    }

    private String generatePassword() {

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 18; i++) {
            password.append(
                PASSWORD_CHARACTERS.charAt(
                    RANDOM.nextInt(PASSWORD_CHARACTERS.length())
                )
            );
        }

        return password.toString();
    }

    private int findHeader(List<String> headers, String expected) {

        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).trim().equalsIgnoreCase(expected)) {
                return i;
            }
        }

        return -1;
    }

    private String getColumn(List<String> columns, int index) {

        if (index < 0 || index >= columns.size()) {
            return "";
        }

        return columns.get(index);
    }

    private List<String> parseCsvLine(String line) {

        List<String> columns = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char current = line.charAt(i);

            if (current == '"') {

                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    value.append('"');
                    i++;

                } else {
                    insideQuotes = !insideQuotes;
                }

            } else if (current == ',' && !insideQuotes) {

                columns.add(value.toString());
                value.setLength(0);

            } else {
                value.append(current);
            }
        }

        columns.add(value.toString());

        return columns;
    }

    private void showError(HttpServletResponse response, String message)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'><title>Import Error</title>");
        out.println("</head><body style='font-family:Arial;padding:35px'>");
        out.println("<h2>Bulk Import</h2>");
        out.println("<p style='color:#b91c1c'>" + escapeHtml(message) + "</p>");
        out.println("<a href='" + getServletContext().getContextPath()
                + "/admin/bulk-import'>Try Again</a>");
        out.println("</body></html>");
    }

    private void printResults(PrintWriter out,
                              String contextPath,
                              int created,
                              int skipped,
                              int failed,
                              List<String[]> results) {

        out.println("<!DOCTYPE html><html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>Import Results | CampusSaathi</title>");

        out.println("<style>");
        out.println("body{font-family:Arial,sans-serif;background:#f5f3ff;padding:25px;color:#292442}");
        out.println(".card{max-width:1000px;margin:20px auto;background:white;padding:25px;border-radius:12px;overflow-x:auto}");
        out.println(".stats{display:flex;gap:12px;flex-wrap:wrap;margin:20px 0}");
        out.println(".stat{background:#f5f3ff;padding:15px;border-radius:8px;min-width:130px}");
        out.println("table{width:100%;border-collapse:collapse;min-width:600px}");
        out.println("th,td{padding:12px;border-bottom:1px solid #eee;text-align:left;font-size:13px}");
        out.println("th{background:#ede9fe}");
        out.println("a{display:inline-block;margin:12px 12px 0 0;background:#5145b5;color:white;padding:11px 15px;border-radius:7px;text-decoration:none}");
        out.println(".warning{background:#fff7ed;padding:12px;border-radius:8px;line-height:1.6}");
        out.println("</style></head><body>");

        out.println("<div class='card'><h1>Bulk Import Results</h1>");
        out.println("<div class='stats'>");
        out.println("<div class='stat'>Created: <strong>" + created + "</strong></div>");
        out.println("<div class='stat'>Skipped: <strong>" + skipped + "</strong></div>");
        out.println("<div class='stat'>Failed: <strong>" + failed + "</strong></div>");
        out.println("</div>");

        out.println("<p class='warning'><strong>Security:</strong> Generated initial passwords are shown below only for accounts created during this import. Distribute them securely and require users to change them after their first login. Do not share this results page or its credentials publicly.</p>");

        out.println("<h2>Row Details</h2>");
        out.println("<table><thead><tr><th>CSV Row</th><th>Email</th><th>Result</th></tr></thead><tbody>");

        for (String[] result : results) {
            out.println("<tr>");
            out.println("<td>" + escapeHtml(result[0]) + "</td>");
            out.println("<td>" + escapeHtml(result[1]) + "</td>");
            out.println("<td>" + escapeHtml(result[2]) + "</td>");
            out.println("</tr>");
        }

        out.println("</tbody></table>");
        out.println("<a href='" + contextPath + "/admin/bulk-import'>Import Another File</a>");
        out.println("<a href='" + contextPath + "/admin/users'>View Accounts</a>");
        out.println("</div></body></html>");
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

