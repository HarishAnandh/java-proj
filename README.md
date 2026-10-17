# 🛡️ SATARK (सतर्क) — Disaster Management & Emergency Safety System

**SATARK** (स सतर्क — *Alert / Vigilant*) is a robust Java Web Application designed for disaster management, early hazard awareness, and citizen emergency safety drills.

The system evaluates and trains citizens, responders, and students across verified life-safety directives covering **Earthquakes**, **Tsunamis**, **Cyclones**, **Structural Fires**, **Flash Floods**, and **Hazardous Material Incidents**.

Data persistence is managed using **Hibernate 6 ORM** with **Jakarta Persistence (JPA)** on **PostgreSQL**, with automatic schema validation and resilient JDBC failover.

---

## 🏛️ System Features

1. **Mission-Ready Disaster Management UI**:
   - Clean, purposeful, high-contrast, uncluttered design (no distracting arcade effects or neon glows).
   - Real-time **National Disaster Status Bar** & **24x7 Emergency Helpline Directory** (`112`, `1078`, `1070`, `108`).
   - Quick **Immediate Action Protocol Cheat Sheet** (Drop-Cover-Hold, Evacuate High Ground, Crawl Under Smoke, Evacuate Upwind).
   - Official **72-Hour Survival Go-Bag** reference checklist.

2. **3 Certified Safety Drill Modules**:
   - 🧠 **Disaster Safety Protocol Assessment (Quiz)**: 10 timed real-world crisis questions with instant life-safety rationale feedback.
   - 🔤 **Emergency Codes & Terminology Drill (Scramble)**: Decipher high-priority disaster codes (Epicenter, Evacuation, Barometer, Seismograph) with definitions.
   - 🖼️ **Critical Incident Decision Matrix (Scenario Challenge)**: Visual multi-choice decision scenarios under acute crisis conditions.

3. **Enterprise Data Layer (Hibernate ORM)**:
   - Full Hibernate ORM 6.6 with JPA annotations (`@Entity`, `@Table`, `@Id`, `@Column`).
   - Managed `SessionFactory` and transactional CRUD operations via `UserDAO`.
   - Automatic database table creation/updates (`hibernate.hbm2ddl.auto=update`).
   - Resilient JDBC fallback mechanism ensures zero downtime even during database configuration shifts.

---

## 📁 Project Architecture

```
proj/
├── sql/
│   └── setup.sql                         ← PostgreSQL database setup & starter seed
├── lib/                                  ← Hibernate & PostgreSQL runtime JARs
│   ├── hibernate-core-6.6.1.Final.jar
│   ├── jakarta.persistence-api-3.2.0.jar
│   ├── postgresql-42.7.1.jar
│   └── [supporting Hibernate jars...]
├── src/
│   ├── hibernate.cfg.xml                 ← Hibernate database configuration
│   └── com/
│       ├── satark/
│       │   ├── model/
│       │   │   └── User.java             ← JPA / Hibernate User Entity
│       │   ├── util/
│       │   │   ├── HibernateUtil.java    ← Hibernate SessionFactory Manager
│       │   │   └── DBUtil.java           ← JDBC Connection Utility (Fallback)
│       │   ├── dao/
│       │   │   └── UserDAO.java          ← Hibernate DAO (Register, Login, Scores, Leaderboard)
│       │   └── servlet/
│       │       ├── UserServlet.java      ← Serves auth, profile, and leaderboard
│       │       └── GameServlet.java      ← Serves questions and persists scores
│       └── disaster/                     ← Backward compatibility wrappers
│           ├── UserServlet.java
│           ├── GameServlet.java
│           └── DBUtil.java
└── webapp/
    ├── WEB-INF/
    │   ├── web.xml                       ← Servlet URL mappings (/user, /game)
    │   ├── classes/                      ← Compiled bytecode & runtime hibernate.cfg.xml
    │   └── lib/                          ← Tomcat deployment JAR libraries
    ├── index.html                        ← SATARK portal landing & authentication
    └── game.html                         ← SATARK disaster drill & assessment console
```

---

## 🔧 Prerequisites

1. **JDK 17+ or JDK 21+** (Adoptium Temurin recommended)
2. **Apache Tomcat 9+** (e.g. `apache-tomcat-9.0.93`)
3. **PostgreSQL 12+**

---

## 🚀 Setup & Execution Instructions

### Step 1: Database Setup

In PostgreSQL (`psql` or pgAdmin):

```sql
CREATE DATABASE disaster_safety;
```

Run the schema script:
```bash
psql -U postgres -d disaster_safety -f sql/setup.sql
```

*(Note: Hibernate will also automatically create or update the `users` table on startup if configured).*

### Step 2: Database Configuration

Ensure your PostgreSQL credentials in `src/hibernate.cfg.xml` and `src/com/satark/util/DBUtil.java` match your local PostgreSQL instance:

```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/disaster_safety</property>
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">harini10</property>
```

### Step 3: Compile the Project

Compile the servlets and Hibernate entities:

```bash
# Mac / Linux:
javac -cp "/path/to/tomcat/lib/servlet-api.jar:lib/*" \
      -d webapp/WEB-INF/classes \
      src/com/satark/model/*.java \
      src/com/satark/util/*.java \
      src/com/satark/dao/*.java \
      src/com/satark/servlet/*.java \
      src/com/disaster/*.java

# Copy Hibernate configuration to classes classpath:
cp src/hibernate.cfg.xml webapp/WEB-INF/classes/
```

*(Or simply execute `./deploy.sh` for 1-step compilation and Tomcat deployment).*

### Step 4: Deploy to Apache Tomcat

**Option A — Copy into Tomcat webapps:**
```bash
cp -r webapp /path/to/tomcat/webapps/satark
```

**Option B — Symlink (Recommended for rapid development):**
```bash
ln -s /path/to/proj/webapp /path/to/tomcat/webapps/satark
```

### Step 5: Start Tomcat & Open SATARK

```bash
/path/to/tomcat/bin/startup.sh
```

Navigate to:
```
http://localhost:8080/satark/
```

*(You can also open `webapp/index.html` directly in any web browser to explore the UI and launch Guest Demo mode even prior to launching Tomcat).*

---

## 📡 API Reference

### User Operations (`/user`)

| Method | Query / Body | Function | Hibernate Method |
|---|---|---|---|
| `POST` | `action=register&username=X&password=Y` | Registers a new user | `session.persist(user)` |
| `POST` | `action=login&username=X&password=Y` | Authenticates citizen/officer | `session.createQuery(...)` |
| `GET` | `action=profile&username=X` | Fetches preparedness profile | `session.createQuery(...)` |
| `GET` | `action=leaderboard` | Fetches Top 10 Responders | `session.createQuery("FROM User ORDER BY totalScore DESC")` |

### Drill Operations (`/game`)

| Method | Query / Body | Function |
|---|---|---|
| `GET` | `action=questions` | Fetches verified incident questions & rationales |
| `POST` | `action=updateScore&username=X&gameType=quiz&score=50` | Persists score and updates community readiness level |

---

## 🛡️ National Disaster Emergency Helplines

| Agency | Helpline Number | Description |
|---|---|---|
| **Unified Emergency (ERSS)** | **112** | Nationwide single emergency response (Police, Fire, Ambulance) |
| **NDMA Control Room** | **1078** | National Disaster Management Authority early warnings |
| **State Emergency Operations (SEOC)** | **1070** | State-level disaster evacuation & relief coordination |
| **District Control Room** | **1077** | District relief centers & local shelter logistics |
| **Fire & Chemical Emergency** | **101** | Structural fire and industrial hazmat rescue |
| **Medical Ambulance** | **108** | Emergency triage & medical transit |