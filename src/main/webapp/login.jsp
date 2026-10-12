<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>

<!DOCTYPE html>

<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>CampusSaathi | Select Your Portal</title>

<style>
* {
    box-sizing: border-box;
}

body {
    margin: 0;
    font-family: Arial, sans-serif;
    background: linear-gradient(135deg, #f5f3ff, #ffffff);
    color: #272343;
    min-height: 100vh;
}

header {
    padding: 24px 7%;
    background: #ffffff;
    border-bottom: 1px solid #ece8f7;
}

.brand {
    color: #5b3cc4;
    font-size: 27px;
    font-weight: bold;
}

.brand span {
    color: #292344;
}

main {
    max-width: 1050px;
    margin: 45px auto;
    padding: 0 22px;
}

.intro {
    text-align: center;
    margin-bottom: 35px;
}

.intro h1 {
    font-size: 34px;
    margin-bottom: 12px;
}

.intro p {
    color: #716c83;
    font-size: 16px;
    line-height: 1.6;
}

.portal-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 22px;
}

.portal-card {
    display: block;
    text-align: left;
    width: 100%;
    padding: 27px;
    border: 2px solid #ebe6f8;
    border-radius: 17px;
    background: white;
    cursor: pointer;
    color: inherit;
    transition: 0.2s ease;
}

.portal-card:hover,
.portal-card:focus-visible {
    border-color: #7252d5;
    transform: translateY(-3px);
    box-shadow: 0 10px 28px rgba(91, 60, 196, 0.10);
    outline: none;
}

.portal-icon {
    font-size: 34px;
    margin-bottom: 15px;
}

.portal-card h2 {
    margin: 0 0 10px;
    font-size: 21px;
}

.portal-card p {
    color: #716c83;
    line-height: 1.6;
    margin: 0 0 18px;
    font-size: 14px;
}

.choose {
    color: #5b3cc4;
    font-weight: bold;
}

.login-box {
    display: none;
    max-width: 460px;
    margin: 32px auto;
    padding: 30px;
    background: white;
    border: 1px solid #ebe6f8;
    border-radius: 17px;
    box-shadow: 0 8px 28px rgba(50, 30, 100, 0.07);
}

.login-box h2 {
    margin-top: 0;
}

.selected-label {
    color: #5b3cc4;
    font-weight: bold;
    margin-bottom: 24px;
}

label {
    display: block;
    font-weight: bold;
    margin: 17px 0 8px;
}

input {
    width: 100%;
    padding: 13px;
    border: 1px solid #dcd6ec;
    border-radius: 8px;
    font-size: 15px;
}

input:focus {
    outline: 2px solid #c7b8ff;
    border-color: #7252d5;
}

.sign-in {
    width: 100%;
    margin-top: 23px;
    padding: 14px;
    border: none;
    border-radius: 9px;
    background: #5b3cc4;
    color: white;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
}

.sign-in:hover {
    background: #482eaa;
}

.back-button {
    margin-top: 20px;
    border: none;
    background: transparent;
    color: #5b3cc4;
    font-weight: bold;
    cursor: pointer;
    font-size: 14px;
}

.error-message {
    padding: 12px;
    border-radius: 8px;
    background: #fff0f0;
    color: #a52626;
    margin-bottom: 16px;
    font-size: 14px;
    line-height: 1.5;
}

footer {
    text-align: center;
    color: #817b94;
    padding: 28px 15px;
    font-size: 13px;
}

@media (max-width: 650px) {
    .portal-grid {
        grid-template-columns: 1fr;
    }

    .intro h1 {
        font-size: 28px;
    }

    main {
        margin-top: 30px;
    }
}
</style>

</head>

<body>

<header>
    <div class="brand">Campus<span>Saathi</span></div>
</header>

<main>

<section class="intro" id="portalIntro">
    <h1>Welcome to CampusSaathi</h1>
    <p>Select your portal to continue to your personalized college dashboard.</p>
</section>

<section class="portal-grid" id="portalGrid">

```
<!-- College Administrator Portal -->
<button type="button" class="portal-card"
    onclick="selectPortal('COLLEGE_ADMIN', 'College Administrator Portal')">

    <div class="portal-icon">&#128100;</div>
    <h2>College Administrator</h2>
    <p>Manage authorized user accounts, account status, and portal access.</p>
    <span class="choose">Continue as Administrator &rarr;</span>
</button>

<!-- HOD Portal -->
<button type="button" class="portal-card"
    onclick="selectPortal('HOD', 'Head of Department Portal')">

    <div class="portal-icon">&#127963;</div>
    <h2>HOD Portal</h2>
    <p>Manage department information, academic activities, and teacher announcements.</p>
    <span class="choose">Continue as HOD &rarr;</span>
</button>

<!-- Teacher Portal -->
<button type="button" class="portal-card"
    onclick="selectPortal('TEACHER', 'Teacher Portal')">

    <div class="portal-icon">&#128105;&#8205;&#127979;</div>
    <h2>Teacher Portal</h2>
    <p>Manage assigned classes, assignments, academic information, and announcements.</p>
    <span class="choose">Continue as Teacher &rarr;</span>
</button>

<!-- Student Portal -->
<button type="button" class="portal-card"
    onclick="selectPortal('STUDENT', 'Student Portal')">

    <div class="portal-icon">&#127891;</div>
    <h2>Student Portal</h2>
    <p>Access assignments, announcements, academic resources, and college information.</p>
    <span class="choose">Continue as Student &rarr;</span>
</button>

<!-- Club Leader Portal -->
<button type="button" class="portal-card"
    onclick="selectPortal('CLUB_LEADER', 'Club Leader Portal')">

    <div class="portal-icon">&#127881;</div>
    <h2>Club Leader Portal</h2>
    <p>Manage authorized club information, publish events, and share club announcements.</p>
    <span class="choose">Continue as Club Leader &rarr;</span>
</button>
```

</section>

<section class="login-box" id="loginBox">

```
<h2>Sign In</h2>
<p class="selected-label" id="selectedPortal"></p>

<%
    String errorMessage =
        (String) request.getAttribute("errorMessage");

    String selectedRole =
        (String) request.getAttribute("selectedRole");

    if (errorMessage != null) {
        String safeError = errorMessage
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
%>

    <div class="error-message"><%= safeError %></div>

<%
    }
%>

<form action="<%= request.getContextPath() %>/login"
      method="post">

    <input type="hidden"
           id="portalRole"
           name="portalRole"
           value="<%= selectedRole == null ? "" : selectedRole %>">

    <label for="email">Email Address</label>
    <input type="email"
           id="email"
           name="email"
           placeholder="Enter your email address"
           autocomplete="username"
           required>

    <label for="password">Password</label>
    <input type="password"
           id="password"
           name="password"
           placeholder="Enter your password"
           autocomplete="current-password"
           required>

    <button type="submit" class="sign-in">
        Sign In &rarr;
    </button>

</form>

<button type="button"
        class="back-button"
        onclick="backToPortals()">
    &larr; Back to portal selection
</button>
```

</section>

</main>

<footer>
    CampusSaathi &copy; 2026 | College Management &amp; Information Portal
</footer>

<script>
function selectPortal(role, title) {

    document.getElementById("portalRole").value = role;
    document.getElementById("selectedPortal").textContent = title;

    document.getElementById("portalGrid").style.display = "none";
    document.getElementById("portalIntro").style.display = "none";
    document.getElementById("loginBox").style.display = "block";

    document.getElementById("email").focus();
}

function backToPortals() {

    document.getElementById("loginBox").style.display = "none";
    document.getElementById("portalGrid").style.display = "grid";
    document.getElementById("portalIntro").style.display = "block";
}

window.addEventListener("DOMContentLoaded", function () {

    const selectedRole =
        document.getElementById("portalRole").value;

    const portalTitles = {
        COLLEGE_ADMIN: "College Administrator Portal",
        HOD: "Head of Department Portal",
        TEACHER: "Teacher Portal",
        STUDENT: "Student Portal",
        CLUB_LEADER: "Club Leader Portal"
    };

    if (selectedRole && portalTitles[selectedRole]) {

        document.getElementById("portalGrid").style.display = "none";
        document.getElementById("portalIntro").style.display = "none";
        document.getElementById("loginBox").style.display = "block";

        document.getElementById("selectedPortal").textContent =
            portalTitles[selectedRole];
    }
});
</script>

</body>
</html>
