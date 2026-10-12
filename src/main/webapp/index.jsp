
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="theme-color" content="#5145b5">
<title>CampusSaathi | Your Campus, Connected</title>

<style>
:root {
    --primary: #5145b5;
    --primary-dark: #3e348f;
    --primary-light: #efedff;
    --text: #211e3a;
    --muted: #77748e;
    --background: #f8f8fd;
    --border: #e9e7f3;
    --white: #ffffff;
    --green: #159b70;
}

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html {
    scroll-behavior: smooth;
}

body {
    font-family: "Segoe UI", Arial, sans-serif;
    background: var(--background);
    color: var(--text);
    line-height: 1.6;
}

button, input {
    font: inherit;
}

button {
    cursor: pointer;
}

a {
    -webkit-tap-highlight-color: transparent;
}

.navbar {
    position: sticky;
    top: 0;
    z-index: 100;
    padding: 17px 7%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
    background: rgba(255, 255, 255, 0.94);
    border-bottom: 1px solid var(--border);
    backdrop-filter: blur(14px);
}

.logo {
    font-size: 27px;
    font-weight: 850;
    letter-spacing: -1.2px;
    color: var(--text);
    text-decoration: none;
}

.logo span {
    color: var(--primary);
}

.nav-tag {
    color: var(--muted);
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 1.5px;
}

.nav-tag span {
    color: var(--primary);
}

.nav-login {
    padding: 10px 17px;
    background: var(--primary-light);
    color: var(--primary-dark);
    text-decoration: none;
    border-radius: 9px;
    font-size: 13px;
    font-weight: 750;
    transition: .2s ease;
}

.nav-login:hover {
    background: #e3dfff;
    transform: translateY(-1px);
}

.hero {
    position: relative;
    overflow: hidden;
    text-align: center;
    padding: 83px 20px 75px;
    background:
        radial-gradient(ellipse at 12% 25%, #e8e4ff 0, transparent 33%),
        radial-gradient(ellipse at 88% 25%, #e1f4ff 0, transparent 31%),
        linear-gradient(180deg, #fbfaff 0%, #f5f5ff 100%);
}

.hero::before,
.hero::after {
    content: "";
    position: absolute;
    width: 180px;
    height: 180px;
    border: 1px solid rgba(81, 69, 181, .10);
    border-radius: 50%;
    pointer-events: none;
}

.hero::before {
    top: 35px;
    left: -100px;
}

.hero::after {
    right: -95px;
    bottom: 5px;
}

.hero-content {
    position: relative;
    z-index: 1;
    max-width: 850px;
    margin: auto;
}

.hero-label {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: #ece9ff;
    color: var(--primary-dark);
    border: 1px solid #ded9ff;
    border-radius: 40px;
    padding: 8px 16px;
    font-size: 11px;
    font-weight: 800;
    letter-spacing: 1px;
    margin-bottom: 25px;
}

.hero-label .sparkle {
    font-size: 15px;
}

.hero h1 {
    font-size: clamp(38px, 5.5vw, 66px);
    line-height: 1.12;
    letter-spacing: -2.7px;
    font-weight: 850;
    margin: 0 auto 22px;
}

.hero h1 span {
    color: var(--primary);
}

.hero p {
    max-width: 650px;
    margin: auto;
    color: var(--muted);
    font-size: 16px;
    line-height: 1.9;
}

.hero-actions {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 30px;
}

.hero-button {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    padding: 13px 21px;
    border-radius: 10px;
    text-decoration: none;
    font-size: 13px;
    font-weight: 800;
    transition: transform .2s, box-shadow .2s, background .2s;
}

.hero-button:hover {
    transform: translateY(-2px);
}

.hero-button.primary {
    color: white;
    background: var(--primary);
    box-shadow: 0 8px 20px rgba(81, 69, 181, .20);
}

.hero-button.primary:hover {
    background: var(--primary-dark);
    box-shadow: 0 12px 25px rgba(81, 69, 181, .25);
}

.hero-button.secondary {
    color: var(--primary-dark);
    background: white;
    border: 1px solid var(--border);
}

.hero-button.secondary:hover {
    border-color: #c9c1ff;
}

.hero-note {
    margin-top: 23px;
    color: #88849d;
    font-size: 12px;
}

.hero-note span {
    color: var(--green);
    font-weight: 800;
}

.portals-section {
    max-width: 1120px;
    margin: 0 auto;
    padding: 66px 24px 75px;
}

.section-heading {
    text-align: center;
    margin-bottom: 34px;
}

.section-eyebrow {
    color: var(--primary);
    font-size: 10px;
    letter-spacing: 2px;
    font-weight: 850;
    margin-bottom: 9px;
}

.section-heading h2 {
    font-size: clamp(25px, 3vw, 34px);
    letter-spacing: -.8px;
    margin-bottom: 9px;
}

.section-heading p {
    color: var(--muted);
    font-size: 14px;
}

.portal-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 20px;
}

.portal-card {
    position: relative;
    overflow: hidden;
    background: var(--white);
    border: 1px solid var(--border);
    border-radius: 17px;
    padding: 27px;
    transition: transform .23s ease, box-shadow .23s ease, border-color .23s ease;
}

.portal-card::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background: linear-gradient(90deg, var(--primary), #9a8fff);
    opacity: 0;
    transition: opacity .23s;
}

.portal-card:hover {
    transform: translateY(-5px);
    border-color: #d8d2ff;
    box-shadow: 0 17px 38px rgba(47, 39, 111, .09);
}

.portal-card:hover::before {
    opacity: 1;
}

.portal-top {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 17px;
}

.portal-icon {
    width: 53px;
    height: 53px;
    flex-shrink: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 15px;
    font-size: 26px;
    background: #f0edff;
}

.portal-card:nth-child(2) .portal-icon {
    background: #e7f5ff;
}

.portal-card:nth-child(3) .portal-icon {
    background: #e4f8ef;
}

.portal-card:nth-child(4) .portal-icon {
    background: #fff0e4;
}

.portal-card h3 {
    font-size: 18px;
    letter-spacing: -.3px;
}

.portal-subtitle {
    color: var(--muted);
    font-size: 11px;
    font-weight: 700;
    margin-top: 2px;
}

.portal-card > p {
    color: var(--muted);
    font-size: 13px;
    line-height: 1.8;
    min-height: 47px;
}

.portal-button {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: 20px;
    padding: 11px 15px;
    border-radius: 9px;
    background: var(--primary-light);
    color: var(--primary-dark);
    text-decoration: none;
    font-size: 12px;
    font-weight: 800;
    transition: .2s ease;
}

.portal-button:hover {
    background: var(--primary);
    color: white;
}

.portal-button .arrow {
    transition: transform .2s ease;
}

.portal-button:hover .arrow {
    transform: translateX(3px);
}

.chat-section {
    max-width: 960px;
    margin: 0 auto;
    padding: 0 24px 80px;
    scroll-margin-top: 100px;
}

.chat-card {
    position: relative;
    overflow: hidden;
    background: white;
    border: 1px solid var(--border);
    border-radius: 22px;
    padding: 32px;
    box-shadow: 0 15px 50px rgba(46, 42, 115, .065);
}

.chat-card::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4px;
    background: linear-gradient(90deg, #5145b5, #8e83ff, #77c9ee);
}

.chat-heading {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 25px;
}

.bot-icon {
    width: 55px;
    height: 55px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #eeebff;
    border: 1px solid #e2ddff;
    border-radius: 16px;
    font-size: 28px;
}

.chat-heading h2 {
    font-size: 20px;
    letter-spacing: -.4px;
}

.chat-heading p {
    font-size: 12px;
    color: var(--muted);
    margin-top: 3px;
}

.status-dot {
    display: inline-block;
    width: 8px;
    height: 8px;
    background: #20bb7a;
    border-radius: 50%;
    margin-right: 6px;
    box-shadow: 0 0 0 3px #e4f9ef;
}

.suggestion-label {
    font-size: 10px;
    letter-spacing: 1.3px;
    font-weight: 850;
    color: var(--muted);
    margin-bottom: 12px;
}

.suggestions {
    display: flex;
    flex-wrap: wrap;
    gap: 9px;
    margin-bottom: 23px;
}

.suggestion {
    border: 1px solid var(--border);
    background: #fff;
    border-radius: 9px;
    padding: 10px 13px;
    font-size: 12px;
    color: #42435d;
    transition: .2s ease;
}

.suggestion:hover {
    border-color: #c8c0ff;
    background: #f6f4ff;
    color: var(--primary-dark);
    transform: translateY(-1px);
}

.chat-form {
    display: flex;
    gap: 10px;
    padding: 7px;
    border: 1px solid #deddf0;
    border-radius: 13px;
    transition: border-color .2s, box-shadow .2s;
}

.chat-form:focus-within {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(81, 69, 181, .08);
}

.chat-form input {
    flex: 1;
    min-width: 0;
    border: none;
    outline: none;
    background: transparent;
    padding: 11px 13px;
    color: var(--text);
    font-size: 13px;
}

.chat-form input::placeholder {
    color: #aaa7bb;
}

.ask-button {
    flex-shrink: 0;
    border: none;
    border-radius: 9px;
    padding: 11px 23px;
    background: var(--primary);
    color: white;
    font-weight: 800;
    font-size: 13px;
    transition: background .2s;
}

.ask-button:hover {
    background: var(--primary-dark);
}

.ask-button:disabled {
    opacity: .65;
    cursor: wait;
}

.response-area {
    display: none;
    margin-top: 22px;
    padding: 22px;
    background: #f8f7ff;
    border: 1px solid #e7e3ff;
    border-radius: 13px;
}

.response-title {
    font-size: 13px;
    font-weight: 850;
    color: var(--primary-dark);
    margin-bottom: 10px;
}

.response-text {
    font-size: 13px;
    color: #42435b;
    white-space: pre-wrap;
    overflow-wrap: anywhere;
    line-height: 1.9;
}

.features-section {
    background: #f0effb;
    padding: 67px 24px 75px;
}

.features-inner {
    max-width: 1080px;
    margin: auto;
}

.feature-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 18px;
}

.feature-card {
    background: rgba(255, 255, 255, .9);
    border: 1px solid #e5e2f3;
    border-radius: 15px;
    padding: 24px;
    transition: transform .2s, box-shadow .2s;
}

.feature-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 25px rgba(46, 42, 115, .06);
}

.feature-icon {
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #eeebff;
    border-radius: 12px;
    font-size: 23px;
    margin-bottom: 17px;
}

.feature-card:nth-child(2) .feature-icon {
    background: #e4f8ef;
}

.feature-card:nth-child(3) .feature-icon {
    background: #fff0e4;
}

.feature-card h3 {
    font-size: 15px;
    margin-bottom: 8px;
}

.feature-card p {
    color: var(--muted);
    font-size: 12px;
    line-height: 1.9;
}

.footer {
    background: white;
    border-top: 1px solid var(--border);
    padding: 30px 20px;
    text-align: center;
}

.footer-logo {
    font-size: 19px;
    font-weight: 850;
    letter-spacing: -.6px;
}

.footer-logo span {
    color: var(--primary);
}

.footer p {
    margin-top: 7px;
    color: var(--muted);
    font-size: 11px;
}

.admin-link {
    display: inline-block;
    margin-top: 13px;
    color: var(--primary-dark);
    font-size: 12px;
    font-weight: 750;
    text-decoration: none;
}

.admin-link:hover {
    text-decoration: underline;
}

@media (max-width: 760px) {
    .navbar {
        padding: 15px 5%;
    }

    .logo {
        font-size: 23px;
    }

    .nav-tag {
        display: none;
    }

    .hero {
        padding: 62px 20px 55px;
    }

    .hero h1 {
        letter-spacing: -1.5px;
    }

    .portals-section {
        padding: 48px 18px 55px;
    }

    .portal-grid {
        gap: 14px;
    }

    .portal-card {
        padding: 21px;
    }

    .feature-grid {
        grid-template-columns: 1fr;
    }

    .features-section {
        padding: 50px 20px;
    }

    .chat-section {
        padding: 0 17px 55px;
    }

    .chat-card {
        padding: 25px 19px;
    }
}

@media (max-width: 520px) {
    .nav-login {
        padding: 9px 11px;
        font-size: 11px;
    }

    .hero h1 {
        font-size: 39px;
    }

    .hero p {
        font-size: 14px;
    }

    .portal-grid {
        grid-template-columns: 1fr;
    }

    .portal-card > p {
        min-height: auto;
    }

    .chat-form {
        gap: 4px;
    }

    .chat-form input {
        padding: 9px 7px;
        font-size: 12px;
    }

    .ask-button {
        padding: 10px 13px;
    }

    .suggestions {
        display: grid;
        grid-template-columns: 1fr;
    }

    .suggestion {
        text-align: left;
    }

    .hero-actions {
        flex-direction: column;
    }

    .hero-button {
        width: 100%;
        max-width: 270px;
    }
}

@media (prefers-reduced-motion: reduce) {
    html {
        scroll-behavior: auto;
    }

    *, *::before, *::after {
        transition-duration: .01ms !important;
    }
}
</style>
</head>

<body>

<header class="navbar">
    <a class="logo" href="<%= request.getContextPath() %>/">
        Campus<span>Saathi.</span>
    </a>

    <div class="nav-tag">
        <span>✦</span> YOUR CAMPUS, CONNECTED
    </div>

    <a class="nav-login"
       href="<%= request.getContextPath() %>/login.jsp">
        Sign In &rarr;
    </a>
</header>

<main>

<section class="hero">
    <div class="hero-content">

        <div class="hero-label">
            <span class="sparkle">✦</span>
            YOUR CAMPUS, SIMPLIFIED
        </div>

        <h1>
            College Life,<br>
            <span>All Connected.</span>
        </h1>

        <p>
            Meet CampusSaathi, your college companion for academic
            information, announcements, placement preparation, events,
            and everyday campus updates — all in one place.
        </p>

        <div class="hero-actions">
            <a class="hero-button primary"
               href="#portals">
                Explore Your Portal <span>&rarr;</span>
            </a>

            <a class="hero-button secondary"
               href="#campus-chat">
                <span>🤖</span> Ask CampusSaathi
            </a>
        </div>

        <div class="hero-note">
            <span>●</span> One campus. Five portals. One connected experience.
        </div>

    </div>
</section>

<section class="portals-section" id="portals">

    <div class="section-heading">
        <div class="section-eyebrow">YOUR PERSONALIZED SPACE</div>
        <h2>Welcome to Your Campus</h2>
        <p>Select your role to access your personalized dashboard.</p>
    </div>

    <div class="portal-grid">

        <article class="portal-card">
            <div class="portal-top">
                <div class="portal-icon">🏛️</div>
                <div>
                    <h3>HOD Portal</h3>
                    <div class="portal-subtitle">Department Management</div>
                </div>
            </div>

            <p>
                Manage department information and publish announcements
                for teachers, students, and club leaders.
            </p>

            <a class="portal-button"
               href="<%= request.getContextPath() %>/login.jsp?role=HOD">
                Continue as HOD <span class="arrow">&rarr;</span>
            </a>
        </article>

        <article class="portal-card">
            <div class="portal-top">
                <div class="portal-icon">👩‍🏫</div>
                <div>
                    <h3>Teacher Portal</h3>
                    <div class="portal-subtitle">Teaching & Academics</div>
                </div>
            </div>

            <p>
                Access your teaching dashboard, academic information,
                and announcements relevant to teachers.
            </p>

            <a class="portal-button"
               href="<%= request.getContextPath() %>/login.jsp?role=TEACHER">
                Continue as Teacher <span class="arrow">&rarr;</span>
            </a>
        </article>

        <article class="portal-card">
            <div class="portal-top">
                <div class="portal-icon">🎓</div>
                <div>
                    <h3>Student Portal</h3>
                    <div class="portal-subtitle">Your Learning Space</div>
                </div>
            </div>

            <p>
                Stay updated with student announcements, assignments,
                academic resources, and college information.
            </p>

            <a class="portal-button"
               href="<%= request.getContextPath() %>/login.jsp?role=STUDENT">
                Continue as Student <span class="arrow">&rarr;</span>
            </a>
        </article>

        <article class="portal-card">
            <div class="portal-top">
                <div class="portal-icon">🎉</div>
                <div>
                    <h3>Club Leader Portal</h3>
                    <div class="portal-subtitle">Clubs & Activities</div>
                </div>
            </div>

            <p>
                Access your club dashboard and share announcements
                and updates with your campus community.
            </p>

            <a class="portal-button"
               href="<%= request.getContextPath() %>/login.jsp?role=CLUB_LEADER">
                Continue as Club Leader <span class="arrow">&rarr;</span>
            </a>
        </article>

    </div>
</section>

<section class="chat-section" id="campus-chat">

    <div class="chat-card">

        <div class="chat-heading">
            <div class="bot-icon">🤖</div>

            <div>
                <h2>Meet Your Campus Assistant</h2>
                <p>
                    <span class="status-dot"></span>
                    CampusSaathi is ready to help
                </p>
            </div>
        </div>

        <div class="suggestion-label">
            QUICK QUESTIONS TO GET YOU STARTED
        </div>

        <div class="suggestions">

            <button type="button" class="suggestion"
                onclick="fillQuestion('Tell me about placement preparation')">
                💼 Placement preparation
            </button>

            <button type="button" class="suggestion"
                onclick="fillQuestion('How should I prepare for technical interviews?')">
                🎯 Interview preparation
            </button>

            <button type="button" class="suggestion"
                onclick="fillQuestion('Tell me about assignment deadlines')">
                📚 Assignment guidelines
            </button>

            <button type="button" class="suggestion"
                onclick="fillQuestion('Tell me about college events')">
                📅 College events
            </button>

            <button type="button" class="suggestion"
                onclick="fillQuestion('What are the attendance guidelines?')">
                🏫 Attendance guidelines
            </button>

        </div>

        <form id="chatForm" class="chat-form">

            <input type="text"
                   id="question"
                   name="message"
                   placeholder="Ask something about your college..."
                   autocomplete="off"
                   maxlength="1000"
                   required>

            <button type="submit"
                    id="askButton"
                    class="ask-button">
                Ask &uarr;
            </button>

        </form>

        <div id="response"
             class="response-area"
             aria-live="polite"
             aria-atomic="true">

            <div class="response-title">
                ✦ CampusSaathi's Response
            </div>

            <div id="responseText" class="response-text"></div>

        </div>

    </div>
</section>

<section class="features-section">

    <div class="features-inner">

        <div class="section-heading">
            <div class="section-eyebrow">MADE FOR CAMPUS LIFE</div>
            <h2>Everything in One Place</h2>
            <p>A simpler, more connected college experience.</p>
        </div>

        <div class="feature-grid">

            <article class="feature-card">
                <div class="feature-icon">💼</div>
                <h3>Placement Preparation</h3>
                <p>
                    Explore programming fundamentals, aptitude,
                    technical skills, and interview preparation.
                </p>
            </article>

            <article class="feature-card">
                <div class="feature-icon">📚</div>
                <h3>Academic Assistance</h3>
                <p>
                    Find assignment guidelines and academic information
                    available in the college database.
                </p>
            </article>

            <article class="feature-card">
                <div class="feature-icon">🎉</div>
                <h3>Campus Activities</h3>
                <p>
                    Get information about college events, workshops,
                    competitions, and hackathons.
                </p>
            </article>

        </div>
    </div>
</section>

</main>

<footer class="footer">

    <div class="footer-logo">
        Campus<span>Saathi.</span>
    </div>

    <p>Bringing your campus community closer, one update at a time.</p>
    <p>&copy; 2026 CampusSaathi | Built with Java Technologies</p>

    <a class="admin-link"
       href="<%= request.getContextPath() %>/login.jsp?role=COLLEGE_ADMIN">
        College Administrator Login
    </a>

</footer>

<script>
const contextPath = "<%= request.getContextPath() %>";

function fillQuestion(question) {
    const input = document.getElementById("question");

    input.value = question;
    input.focus();

    document.getElementById("campus-chat").scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}

document.getElementById("chatForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const input = document.getElementById("question");
    const responseBox = document.getElementById("response");
    const responseText = document.getElementById("responseText");
    const askButton = document.getElementById("askButton");

    const question = input.value.trim();

    if (!question) {
        input.focus();
        return;
    }

    responseBox.style.display = "block";
    responseText.textContent = "CampusSaathi is thinking...";
    askButton.disabled = true;
    askButton.textContent = "Thinking...";

    try {
        const result = await fetch(contextPath + "/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
            },
            body: new URLSearchParams({
                message: question
            })
        });

        const answer = await result.text();

        if (!result.ok) {
            throw new Error("Server returned status " + result.status);
        }

        responseText.textContent = answer;

    } catch (error) {
        console.error("CampusSaathi chat error:", error);

        responseText.textContent =
            "Sorry, I couldn't retrieve your answer. Please check the server connection and try again.";

    } finally {
        askButton.disabled = false;
        askButton.textContent = "Ask ↑";
    }
});
</script>

</body>
</html>

