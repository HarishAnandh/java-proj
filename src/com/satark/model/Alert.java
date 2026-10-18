package com.satark.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Alert Entity - Represents a filed disaster case parsed via Regex.
 * Maps to 'alerts' table in PostgreSQL database.
 */
@Entity
@Table(name = "alerts")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "case_id", unique = true, nullable = false, length = 50)
    private String caseId;

    @Column(name = "disaster_type", nullable = false, length = 50)
    private String disasterType;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "affected_count")
    private int affectedCount = 0;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "reported_by", length = 50)
    private String reportedBy;

    @Column(name = "raw_report", columnDefinition = "TEXT")
    private String rawReport;

    @Column(name = "extracted_summary", columnDefinition = "TEXT")
    private String extractedSummary;

    @Column(name = "status", length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    public Alert() {
    }

    public Alert(String caseId, String disasterType, String severity, String location,
                 String pincode, int affectedCount, String contactPhone, String contactEmail,
                 String reportedBy, String rawReport, String extractedSummary) {
        this.caseId = caseId;
        this.disasterType = disasterType;
        this.severity = severity;
        this.location = location;
        this.pincode = pincode;
        this.affectedCount = affectedCount;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.reportedBy = reportedBy;
        this.rawReport = rawReport;
        this.extractedSummary = extractedSummary;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(String disasterType) {
        this.disasterType = disasterType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getAffectedCount() {
        return affectedCount;
    }

    public void setAffectedCount(int affectedCount) {
        this.affectedCount = affectedCount;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getRawReport() {
        return rawReport;
    }

    public void setRawReport(String rawReport) {
        this.rawReport = rawReport;
    }

    public String getExtractedSummary() {
        return extractedSummary;
    }

    public void setExtractedSummary(String extractedSummary) {
        this.extractedSummary = extractedSummary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
