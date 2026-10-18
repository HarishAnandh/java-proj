package com.satark.servlet;

import com.satark.dao.AlertDAO;
import com.satark.model.Alert;
import com.satark.util.ReportRegexExtractor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * AlertServlet - SATARK Incident Reporting & Regex Auto-Extraction Controller.
 * Handles:
 *   POST /alert?action=report  → Parses disaster report text via Regex and files a case in 'alerts' table
 *   GET  /alert?action=list    → Retrieves all filed alerts from database
 */
public class AlertServlet extends HttpServlet {

    private final AlertDAO alertDAO = new AlertDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if ("list".equals(action)) {
                List<Alert> alerts = alertDAO.getAllAlerts();
                StringBuilder json = new StringBuilder("{\"status\":\"success\",\"alerts\":[");
                for (int i = 0; i < alerts.size(); i++) {
                    if (i > 0) json.append(",");
                    Alert a = alerts.get(i);
                    json.append(toJson(a));
                }
                json.append("]}");
                out.print(json.toString());
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
            if ("report".equals(action)) {
                String rawReport = request.getParameter("rawReport");
                String reportedBy = request.getParameter("reportedBy");

                if (rawReport == null || rawReport.trim().isEmpty()) {
                    out.print("{\"status\":\"error\",\"message\":\"Disaster report text cannot be empty\"}");
                    return;
                }

                // 1. Run Java Regular Expression (Regex) Extraction
                Alert alert = ReportRegexExtractor.extractAndBuildAlert(rawReport, reportedBy);

                if (alert == null) {
                    out.print("{\"status\":\"error\",\"message\":\"Failed to parse report\"}");
                    return;
                }

                // 2. Persist to PostgreSQL 'alerts' table via Hibernate ORM
                boolean saved = alertDAO.saveAlert(alert);

                if (saved) {
                    out.print("{\"status\":\"success\",\"message\":\"Incident case successfully filed in alerts database!\",\"alert\":" + toJson(alert) + "}");
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"Failed to persist case to alerts table\"}");
                }
            } else {
                out.print("{\"status\":\"error\",\"message\":\"Invalid action\"}");
            }
        } catch (Exception e) {
            out.print("{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String toJson(Alert a) {
        return String.format(
                "{\"id\":%d,\"caseId\":\"%s\",\"disasterType\":\"%s\",\"severity\":\"%s\"," +
                "\"location\":\"%s\",\"pincode\":\"%s\",\"affectedCount\":%d," +
                "\"contactPhone\":\"%s\",\"contactEmail\":\"%s\",\"reportedBy\":\"%s\"," +
                "\"rawReport\":\"%s\",\"extractedSummary\":\"%s\",\"status\":\"%s\",\"createdAt\":\"%s\"}",
                a.getId(),
                escapeJson(a.getCaseId()),
                escapeJson(a.getDisasterType()),
                escapeJson(a.getSeverity()),
                escapeJson(a.getLocation()),
                escapeJson(a.getPincode()),
                a.getAffectedCount(),
                escapeJson(a.getContactPhone()),
                escapeJson(a.getContactEmail()),
                escapeJson(a.getReportedBy()),
                escapeJson(a.getRawReport()),
                escapeJson(a.getExtractedSummary()),
                escapeJson(a.getStatus()),
                a.getCreatedAt() != null ? a.getCreatedAt().toString() : ""
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
