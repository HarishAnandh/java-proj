package com.satark.util;

import com.satark.model.Alert;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReportRegexExtractor - Parses incoming unstructured disaster situation reports
 * using Java Regular Expressions (Regex) to extract structured incident fields.
 */
public class ReportRegexExtractor {

    // Regex 1: Disaster Category / Type
    private static final Pattern DISASTER_PATTERN = Pattern.compile(
            "(?i)\\b(flash flood|flood|earthquake|seismic tremor|structural fire|wildfire|fire|cyclone|hurricane|tornado|tsunami|landslide|mudslide|chemical spill|chemical leak|gas leak|heatwave)\\b"
    );

    // Regex 2: Severity Level
    private static final Pattern SEVERITY_PATTERN = Pattern.compile(
            "(?i)(?:severity|priority|urgency|level)[:\\s]*\\b(CRITICAL|EXTREME|HIGH|MODERATE|MEDIUM|LOW)\\b|\\b(CRITICAL|EXTREME|HIGH|MODERATE|MEDIUM|LOW)\\b"
    );

    // Regex 3: PIN Code (6 digits)
    private static final Pattern PINCODE_PATTERN = Pattern.compile(
            "(?i)(?:pin(?:code)?|postal(?:\\s*code)?|zip)[:\\s]*(\\d{6})\\b|\\b(\\d{6})\\b"
    );

    // Regex 4: Phone / Emergency Contact
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+?91[\\-\\s]?)?[6-9]\\d{9}|(?:\\+?\\d{1,3}[-\\s]?)?\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}"
    );

    // Regex 5: Email Address
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    // Regex 6: Affected People / Casualties Count
    private static final Pattern AFFECTED_PATTERN = Pattern.compile(
            "(?i)(?:over|approx(?:imately)?|about|nearly)?\\s*(\\d+)\\s*(?:people|citizens|persons|victims|residents|villagers)?\\s*(?:stranded|affected|injured|trapped|displaced|rescued)"
    );

    // Regex 7: Location Extraction
    private static final Pattern LOCATION_PATTERN = Pattern.compile(
            "(?i)(?:in|near|at|around|location[:\\s]+|region[:\\s]+)\\s+([A-Z][a-zA-Z0-9\\s,.-]+?)(?=\\s*(?:\\(|\\.|\\n|,\\s*(?:pin|contact|severity|with|over|lat|phone)|$))"
    );

    /**
     * Extracts structured disaster information from raw text and creates an Alert entity.
     */
    public static Alert extractAndBuildAlert(String rawText, String reportedBy) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return null;
        }

        String caseId = "CASE-" + (System.currentTimeMillis() % 1000000);

        // 1. Extract Disaster Type
        String disasterType = "General Emergency";
        Matcher disasterMatcher = DISASTER_PATTERN.matcher(rawText);
        if (disasterMatcher.find()) {
            String match = disasterMatcher.group(1);
            disasterType = capitalize(match);
        }

        // 2. Extract Severity
        String severity = "MODERATE";
        Matcher severityMatcher = SEVERITY_PATTERN.matcher(rawText);
        if (severityMatcher.find()) {
            String match = severityMatcher.group(1) != null ? severityMatcher.group(1) : severityMatcher.group(2);
            severity = match.toUpperCase();
            if ("EXTREME".equals(severity)) severity = "CRITICAL";
            if ("MEDIUM".equals(severity)) severity = "MODERATE";
        } else {
            // Infer severity based on casualty count or keywords
            if (rawText.toLowerCase().contains("severe") || rawText.toLowerCase().contains("urgent") || rawText.toLowerCase().contains("immediate")) {
                severity = "HIGH";
            }
        }

        // 3. Extract PIN Code
        String pincode = "";
        Matcher pincodeMatcher = PINCODE_PATTERN.matcher(rawText);
        if (pincodeMatcher.find()) {
            pincode = pincodeMatcher.group(1) != null ? pincodeMatcher.group(1) : pincodeMatcher.group(2);
        }

        // 4. Extract Location
        String location = "Reported Geographic Zone";
        Matcher locationMatcher = LOCATION_PATTERN.matcher(rawText);
        if (locationMatcher.find()) {
            location = locationMatcher.group(1).trim();
            // clean up trailing punctuation
            location = location.replaceAll("[,.]+$", "").trim();
        }

        // 5. Extract Affected Count
        int affectedCount = 0;
        Matcher affectedMatcher = AFFECTED_PATTERN.matcher(rawText);
        if (affectedMatcher.find()) {
            try {
                affectedCount = Integer.parseInt(affectedMatcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }

        // 6. Extract Contact Phone
        String contactPhone = "";
        Matcher phoneMatcher = PHONE_PATTERN.matcher(rawText);
        if (phoneMatcher.find()) {
            contactPhone = phoneMatcher.group().trim();
        }

        // 7. Extract Contact Email
        String contactEmail = "";
        Matcher emailMatcher = EMAIL_PATTERN.matcher(rawText);
        if (emailMatcher.find()) {
            contactEmail = emailMatcher.group().trim();
        }

        String user = (reportedBy != null && !reportedBy.trim().isEmpty()) ? reportedBy.trim() : "Citizen Dispatcher";

        // Structured extracted summary
        String extractedSummary = String.format(
                "Regex Parser identified %s (%s Priority) at %s%s. %s people reported affected. Emergency Contact: %s.",
                disasterType,
                severity,
                location,
                pincode.isEmpty() ? "" : " [PIN: " + pincode + "]",
                affectedCount > 0 ? String.valueOf(affectedCount) : "Unknown",
                contactPhone.isEmpty() ? (contactEmail.isEmpty() ? "None listed" : contactEmail) : contactPhone
        );

        return new Alert(
                caseId,
                disasterType,
                severity,
                location,
                pincode,
                affectedCount,
                contactPhone,
                contactEmail,
                user,
                rawText.trim(),
                extractedSummary
        );
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.length() > 0) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
