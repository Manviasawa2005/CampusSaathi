package com.campussaathi.controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {


private static final long serialVersionUID = 1L;

@Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws IOException {

    HttpSession session = request.getSession(false);

    if (session != null) {
        session.invalidate();
    }

    response.sendRedirect(request.getContextPath() + "/login.jsp");
}


}

