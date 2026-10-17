package com.satark.servlet;

import com.satark.dao.UserDAO;
import com.satark.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * GameServlet - SATARK Disaster Preparedness Drills
 * Serves questions and persists drill scores using Hibernate ORM via UserDAO.
 */
public class GameServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    // Emergency Preparedness & Disaster Management Quiz Data
    private static final String QUIZ_DATA = "["
        + "{\"q\":\"During an earthquake, what should you do immediately?\","
        + "\"options\":[\"Run outside into the street\",\"Drop, Cover, and Hold On under sturdy furniture\",\"Stand next to large glass windows\",\"Take the elevator to evacuate\"],"
        + "\"answer\":1,\"explanation\":\"Drop to hands and knees, take Cover under a sturdy table/desk, and Hold On until shaking stops.\"},"

        + "{\"q\":\"What is the safest place inside a building during a severe tornado?\","
        + "\"options\":[\"Next to large exterior windows\",\"Inside your vehicle in the driveway\",\"An interior room or hallway on the lowest floor\",\"On the balcony or roof\"],"
        + "\"answer\":2,\"explanation\":\"Interior rooms (bathrooms, closets, hallways) with no windows on the lowest level offer the highest structural protection.\"},"

        + "{\"q\":\"During a flash flood, what is a critical rule you must NEVER break?\","
        + "\"options\":[\"Move to higher ground immediately\",\"Listen to disaster broadcast alerts\",\"Drive or walk through floodwaters\",\"Keep an emergency kit ready\"],"
        + "\"answer\":2,\"explanation\":\"Turn Around, Don't Drown! Just 6 inches of moving water can knock you down, and 12-24 inches can carry away vehicles.\"},"

        + "{\"q\":\"What does a Class B/C Dry Chemical fire extinguisher contain?\","
        + "\"options\":[\"Pressurized tap water\",\"Liquefied Carbon Dioxide\",\"Dry Chemical Powder (Monoammonium Phosphate / Sodium Bicarbonate)\",\"Aqueous film foaming agent\"],"
        + "\"answer\":2,\"explanation\":\"Dry chemical extinguishers extinguish flammable liquids and electrical fires without conducting electricity.\"},"

        + "{\"q\":\"In a burning building, how should you navigate through heavy smoke?\","
        + "\"options\":[\"Sprint standing upright\",\"Crawl low on hands and knees under smoke layer\",\"Open all doors and windows\",\"Use the elevator for fast exit\"],"
        + "\"answer\":1,\"explanation\":\"Toxic smoke and superheated gases rise to the ceiling; cleaner, cooler air remains 1-2 feet above the floor.\"},"

        + "{\"q\":\"A coastal Tsunami Early Warning siren sounds. What is the immediate action?\","
        + "\"options\":[\"Head to the beach to observe the receding water\",\"Evacuate inland to elevated ground immediately\",\"Seek shelter in your basement\",\"Board a small recreational boat at the pier\"],"
        + "\"answer\":1,\"explanation\":\"A rapidly receding shoreline precedes giant tsunami surges; immediately move inland and upward at least 30 meters above sea level.\"},"

        + "{\"q\":\"According to disaster authorities, what are essential contents of an Emergency Survival Go-Bag?\","
        + "\"options\":[\"Only perishable food\",\"Bottled water, non-perishable rations, first aid kit, flashlight, radio, multi-tool\",\"Only luxury electronics and heavy blankets\",\"A cell phone without spare power or cables\"],"
        + "\"answer\":1,\"explanation\":\"A 72-hour emergency kit must support basic survival with water (3L/person/day), food, first aid, illumination, and power.\"},"

        + "{\"q\":\"During a Category 4 Cyclone/Hurricane, what is the safest precaution?\","
        + "\"options\":[\"Tape giant 'X's on windows and leave them open\",\"Stay indoors in a windowless room, away from doors, securing all loose exterior objects beforehand\",\"Go outside into the eye to inspect property damage\",\"Use candles for lighting during power cuts\"],"
        + "\"answer\":1,\"explanation\":\"Flying debris causes majority of cyclone casualties. Stay inside an interior room away from exterior glass.\"},"

        + "{\"q\":\"The Richter and Moment Magnitude (Mw) scales quantify:\","
        + "\"options\":[\"Atmospheric wind velocity\",\"Tidal surge elevation\",\"Earthquake energy release and seismic magnitude\",\"Ambient flood temperature\"],"
        + "\"answer\":2,\"explanation\":\"Magnitude scales measure the total seismic energy released at the earthquake hypocenter.\"},"

        + "{\"q\":\"If you suspect a combustible gas leak (strong sulfur / rotten egg odor):\","
        + "\"options\":[\"Light a match or lighter to locate the leak\",\"Flip light switches on to illuminate the area\",\"Evacuate immediately without using electrical switches/phones, then call emergency from outside\",\"Turn on kitchen exhaust fans\"],"
        + "\"answer\":2,\"explanation\":\"Flipping electrical switches or using phones generates micro-sparks that can ignite an explosive gas cloud.\"},"

        + "{\"q\":\"What is the national emergency response helpline number in India?\","
        + "\"options\":[\"911\",\"100\",\"112\",\"999\"],"
        + "\"answer\":2,\"explanation\":\"112 is the Unified Emergency Response Support System (ERSS) across India for Police, Fire, and Ambulance.\"},"

        + "{\"q\":\"During a debris flow or hillside landslide, which direction should you run?\","
        + "\"options\":[\"Directly downhill along the gully\",\"Perpendicular/sideways away from the path of debris\",\"Directly towards the crest of the slope\",\"Climb into a nearby utility pole\"],"
        + "\"answer\":1,\"explanation\":\"Debris flows follow natural downhill gullies at extreme velocity; running sideways out of the flow path is life-saving.\"},"

        + "{\"q\":\"Which underwater geological event most commonly triggers destructive tsunamis?\","
        + "\"options\":[\"Atmospheric low pressure troughs\",\"Severe thunderstorms\",\"Submarine megathrust earthquakes causing vertical seabed displacement\",\"Oceanic heatwaves\"],"
        + "\"answer\":2,"
        + "\"explanation\":\"Vertical displacement of the seafloor during shallow subduction zone earthquakes displaces enormous water columns.\"},"

        + "{\"q\":\"If your clothing catches fire, what is the universal safety protocol?\","
        + "\"options\":[\"Run rapidly to extinguish flames with wind\",\"Stop, Drop to the ground, and Roll back and forth while covering your face\",\"Jump up and down vigorously\",\"Flog the flames with paper\"],"
        + "\"answer\":1,\"explanation\":\"Running fuels the fire with oxygen. Stopping, dropping, and rolling smothers the flame against the ground.\"},"

        + "{\"q\":\"What characterizes the 'Eye' of a tropical cyclone?\","
        + "\"options\":[\"The region with strongest destructive winds and torrential rain\",\"A calm, low-pressure center with light winds and clearing skies, surrounded by the destructive eyewall\",\"The outer band of moisture farthest from shore\",\"The cold water wake behind the storm\"],"
        + "\"answer\":1,\"explanation\":\"The eye is calm, but the eyewall surrounding it holds the fiercest winds. When the eye passes, winds reverse violently.\"} "
        + "]";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        if ("questions".equals(action)) {
            out.print("{\"status\":\"success\",\"questions\":" + QUIZ_DATA + "}");
        } else {
            out.print("{\"status\":\"error\",\"message\":\"Invalid action\"}");
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

        if ("updateScore".equals(action)) {
            String username = request.getParameter("username");
            String gameType = request.getParameter("gameType");
            String scoreStr = request.getParameter("score");

            if (username == null || gameType == null || scoreStr == null) {
                out.print("{\"status\":\"error\",\"message\":\"Missing parameters\"}");
                return;
            }

            try {
                int score = Integer.parseInt(scoreStr);
                User user = userDAO.updateScore(username, gameType, score);

                if (user != null) {
                    out.print(String.format(
                            "{\"status\":\"success\",\"message\":\"Readiness score updated successfully!\"," +
                            "\"user\":{\"username\":\"%s\",\"quizScore\":%d," +
                            "\"scrambleScore\":%d,\"pictureScore\":%d," +
                            "\"totalScore\":%d,\"gamesPlayed\":%d}}",
                            escapeJson(user.getUsername()),
                            user.getQuizScore(),
                            user.getScrambleScore(),
                            user.getPictureScore(),
                            user.getTotalScore(),
                            user.getGamesPlayed()
                    ));
                } else {
                    out.print("{\"status\":\"error\",\"message\":\"User not found or update failed\"}");
                }
            } catch (NumberFormatException e) {
                out.print("{\"status\":\"error\",\"message\":\"Invalid score format\"}");
            } catch (Exception e) {
                out.print("{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        } else {
            out.print("{\"status\":\"error\",\"message\":\"Invalid action\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
