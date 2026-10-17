package com.satark.servlet;

import com.satark.dao.UserDAO;
import com.satark.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * UserServlet - SATARK Disaster Management System
 * Handles Authentication, User Profiles, and Readiness Leaderboard.
 * Uses Hibernate ORM via UserDAO.
 */
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if ("leaderboard".equals(action)) {
                List<User> list = userDAO.getLeaderboard(10);
                StringBuilder json = new StringBuilder("{\"status\":\"success\",\"leaderboard\":[");
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) json.append(",");
                    User u = list.get(i);
                    json.append(String.format(
                            "{\"username\":\"%s\",\"quizScore\":%d,\"scrambleScore\":%d," +
                            "\"pictureScore\":%d,\"totalScore\":%d,\"gamesPlayed\":%d}",
                            escapeJson(u.getUsername()),
                            u.getQuizScore(),
                            u.getScrambleScore(),
                            u.getPictureScore(),
                            u.getTotalScore(),
                            u.getGamesPlayed()
                    ));
                }
                json.append("]}");
                out.print(json.toString());

            } else if ("profile".equals(action)) {
                String username = request.getParameter("username");
                if (username == null || username.trim().isEmpty()) {
                    out.print("{\"status\":\"error\",\"message\":\"Username required\"}");
                    return;
                }
                User u = userDAO.getUserProfile(username);
                if (u != null) {
                    out.print(String.format(
                            "{\"status\":\"success\",\"user\":{" +
                            "\"username\":\"%s\",\"quizScore\":%d,\"scrambleScore\":%d," +
                            "\"pictureScore\":%d,\"totalScore\":%d,\"gamesPlayed\":%d}}",
                            escapeJson(u.getUsername()),
                            u.getQuizScore(),
                            u.getScrambleScore(),
                            u.getPictureScore(),
                            u.getTotalScore(),
                            u.getGamesPlayed()
                    ));
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"User not found\"}");
                }

            } else {
                out.print("{\"status\":\"error\",\"message\":\"Invalid action\"}");
            }
        } catch (Exception e) {
            out.print("{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if ("register".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                String error = userDAO.registerUser(username, password);
                if (error == null) {
                    out.print("{\"status\":\"success\",\"message\":\"Registration successful! Welcome to SATARK.\"}");
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"" + escapeJson(error) + "\"}");
                }

            } else if ("login".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                User u = userDAO.loginUser(username, password);
                if (u != null) {
                    out.print(String.format(
                            "{\"status\":\"success\",\"message\":\"Welcome back to SATARK!\"," +
                            "\"user\":{" +
                            "\"id\":%d,\"username\":\"%s\"," +
                            "\"quizScore\":%d,\"scrambleScore\":%d,\"pictureScore\":%d," +
                            "\"totalScore\":%d,\"gamesPlayed\":%d}}",
                            u.getId(),
                            escapeJson(u.getUsername()),
                            u.getQuizScore(),
                            u.getScrambleScore(),
                            u.getPictureScore(),
                            u.getTotalScore(),
                            u.getGamesPlayed()
                    ));
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"Invalid username or password\"}");
                }

            } else {
                out.print("{\"status\":\"error\",\"message\":\"Invalid action\"}");
            }
        } catch (Exception e) {
            out.print("{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
