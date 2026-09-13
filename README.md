# Production-Quality Coding Assessment Platform (90-Minute Technical Assessment)

An enterprise-grade, end-to-end web platform designed to conduct comprehensive **90-minute technical assessments** for software engineering candidates.

The assessment consists of:
* **3 Java Coding Questions**: Medium LeetCode-style problems restricted strictly to **Arrays**, **String manipulation**, and **Data structures**, executed in an isolated process/container sandbox with timeouts and memory caps.
* **3 SQL Questions**: Intermediate database problems covering **JOINs**, **Aggregation**, and **Window Functions**, evaluated on ephemeral sandboxed databases with deep result-set matching.
* **1 English Speaking Question**: Audio recorded in-browser with speech-to-text transcription and structured AI rubric evaluation across 6 linguistic criteria.
* **Global 90-Minute Persistent Timer**: Synchronized across sections with automatic submission when the timer reaches `00:00`.
* **Fullscreen & Anti-Cheat Enforcement**: Detects fullscreen exits, tab switches, and window blur events with immediate violation tracking and audit logging.
* **Full Administrator Portal**: Candidate attempt monitoring, question bank CRUD, code review, audio playback, AI rubric inspection, and CSV exports.

---

## Architecture Overview

```
                          ┌────────────────────────┐
                          │   Candidate / Admin    │
                          │        Browser         │
                          └───────────┬────────────┘
                                      │
                         HTTP / REST  │  MediaRecorder / Web Speech
                                      ▼
             ┌──────────────────────────────────────────────────┐
             │       Frontend (React 19 + TypeScript + Vite)     │
             │   - Monaco Editor for Java & SQL                 │
             │   - Fullscreen API & Tab Visibility Guard        │
             │   - Live 90-Min Persistent Countdown Timer       │
             │   - Audio Waveform & Speech-to-Text Capture      │
             └────────────────────────┬─────────────────────────┘
                                      │
                                      ▼
             ┌──────────────────────────────────────────────────┐
             │    Backend (Spring Boot 3.3.x / Java 21)         │
             │   - Spring Security & JWT Authentication         │
             │   - Deterministic Question Randomization Engine  │
             │   - Scoring Engine (Java: 30, SQL: 30, ENG: 40)  │
             └──────┬──────────────────────┬────────────────────┘
                    │                      │
       ┌────────────▼─────────┐ ┌──────────▼──────────┐ ┌────────────────────┐
       │ Java Execution       │ │ SQL Evaluation      │ │ AI English Engine  │
       │ Sandbox (Watched OS  │ │ Sandbox (Ephemeral  │ │ (Audio Vault + NLP │
       │ Process / Container) │ │ Schema & Matcher)   │ │ Rubric Evaluator)  │
       └──────────────────────┘ └─────────────────────┘ └────────────────────┘
```

---

## 1. Prerequisites

* **Java 21 JDK** or newer (`java -version`, `javac -version`)
* **Node.js 18+** and **npm** (`node -v`, `npm -v`)
* **Maven 3.9+** (Included wrapper `mvnw.cmd` / `mvnw` uses local or IntelliJ Maven automatically)
* (Optional) **Docker** for containerized deployments

---

## 2. Quick Start & Local Setup

### Step 1: Run Backend (Spring Boot 3)

```powershell
cd z:\Exam\backend

# Run automated tests to verify sandbox & database engines
.\mvnw.cmd test

# Launch the Spring Boot backend
.\mvnw.cmd spring-boot:run
```

The backend starts on `http://localhost:8080`.
* Pre-seeds admin user: `admin` / `admin123`
* Pre-seeds 90-minute assessment code: `DEMO90`
* Pre-seeds medium Java problems (Arrays, Strings, Data Structures)
* Pre-seeds intermediate SQL problems (JOINs, Aggregation, Window Functions)
* Pre-seeds English technical speaking prompts
* H2 Database Console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/assessmentdb`, User: `sa`, Password: empty)

---

### Step 2: Run Frontend (React + TypeScript)

In a new terminal window:

```powershell
cd z:\Exam\frontend

# Install dependencies (if not already installed)
npm.cmd install

# Start Vite development server
npm.cmd run dev
```

The frontend will be live at `http://localhost:5173`.

---

## 3. End-to-End Test Walkthrough

### 3.1 Candidate Examination Flow

1. Open `http://localhost:5173/test/DEMO90` in your web browser.
2. Enter Candidate Information:
   * **Full Name**: e.g., `Jane Candidate`
   * **Email**: e.g., `jane@example.com`
3. Click **Proceed to Fullscreen & Start Exam**.
4. A warning modal appears stating:
   > *"This examination must be completed in fullscreen mode. Leaving fullscreen or switching away from the examination may terminate your attempt."*
5. Click **Agree & Enter Fullscreen**.
   * Browser requests and enters fullscreen mode.
   * Persistent 90-minute countdown starts (`89:59 ... 00:00`).
   * Exactly 3 Java, 3 SQL, and 1 English question are deterministically assigned.
6. **Solve Section A — Java Coding (Q1, Q2, Q3)**:
   * Select Q1, Q2, or Q3. Review problem description, input/output formats, and constraints.
   * Write solution in the Monaco Java Editor.
   * Click **Run Code** to execute against sample test cases or custom input.
   * Click **Submit Code** to evaluate against hidden test cases. View passed tests, execution time, and stdout.
7. **Solve Section B — SQL Database (Q4, Q5, Q6)**:
   * Select Q4, Q5, or Q6. Inspect the schema, tables, and sample data.
   * Write query in the Monaco SQL Editor.
   * Click **Run Query** to execute against the isolated test database.
   * Click **Submit Query** to evaluate results against the golden reference query.
8. **Complete Section C — English Speaking (Q7)**:
   * Select Q7. Read the technical project speaking prompt.
   * Click **Start Recording** (grant microphone permission when prompted).
   * Speak your response. Notice the live audio waveform and automatic speech-to-text transcript preview.
   * Click **Stop Recording**, review audio playback, and click **Submit Recording**.
   * The AI Evaluation engine calculates and displays scores across Grammar, Vocabulary, Fluency, Pronunciation, Relevance, and Structure along with detailed narrative feedback.
9. **Anti-Cheat Verification**:
   * Try exiting fullscreen (press `ESC`) or switching tabs (`Alt+Tab`).
   * The anti-cheat detector logs the violation with a timestamp and displays the security alert.
10. **Final Submission**:
    * Click **Final Submit** in the header (or wait for the 90-minute timer to reach `00:00`).
    * The **Assessment Completed** result page displays the 100-point total score breakdown:
      * Java Coding: `/30`
      * SQL Database: `/30`
      * English Speaking: `/40`
      * Total: `/100`

---

### 3.2 Administrator Management Flow

1. Navigate to `http://localhost:5173/admin`.
2. Login with credentials:
   * **Username**: `admin`
   * **Password**: `admin123`
3. **Assessments & Links**:
   * Create new assessments with custom codes (e.g., `8F42KD`) and durations.
   * Copy unique candidate links (`/test/8F42KD`).
4. **Question Bank**:
   * View pre-seeded questions across Java (Arrays, Strings, Data Structures), SQL (JOINs, Aggregation, Window Functions), and English.
   * Add new custom questions with test cases or SQL schemas.
5. **Candidate Attempts & Audit**:
   * View live and completed attempts with status, scores, and violation counts.
   * Click **Review Attempt** to open the candidate report:
     * Full source code submitted for every question.
     * Audio recording player to listen to candidate's spoken response.
     * Captured speech transcript and AI rubric score breakdown.
     * Anti-cheat violation logs with exact timestamps.
   * Click **Export CSV** to download results for offline review.

---

## 4. Security & Sandboxing Design

### Java Execution Sandbox
* **Static Analysis Pre-Filter**: Banned operations (`java.lang.reflect`, `ProcessBuilder`, `Runtime.getRuntime`, `System.exit`, `sun.misc.Unsafe`, file/socket modifications) are rejected immediately with a `SECURITY_VIOLATION` status.
* **Isolated Temporary Workspaces**: Each compilation and execution runs inside a unique, randomized ephemeral directory.
* **Process Watchdog & Resource Caps**: Sub-processes execute with bounded heap (`-Xmx128m -XX:+UseSerialGC`) and a strict 3-second watchdog timer. If an infinite loop is detected, the entire process tree is forcibly killed (`destroyForcibly()`), returning `TIME_LIMIT_EXCEEDED`.
* **Buffer Overflow Protection**: Output streams are capped at 64KB to prevent memory exhaustion from runaway print statements.
* **Automatic Cleanup**: In-memory and on-disk execution directories are recursively purged in a `finally` block.

### SQL Evaluation Engine
* **Strict SQL Filter**: Queries containing `DROP`, `ALTER`, `TRUNCATE`, `INSERT`, `UPDATE`, `DELETE`, `GRANT`, or file system routines are blocked.
* **Isolated Ephemeral Databases**: Every query evaluation runs on an isolated in-memory database instance (`jdbc:h2:mem:sql_sandbox_...;MODE=PostgreSQL`) initialized with fresh schema DDL and test data.
* **Result-Set Set Comparison**: Rather than brittle string comparisons, the engine normalizes column labels and compares result sets (accounting for order neutrality when appropriate).

### Spoken English AI Evaluation
* **Browser MediaRecorder**: Records high-fidelity audio blobs (`audio/webm`).
* **Web Speech API Integration**: Continuously transcribes spoken audio directly in the browser; candidate cannot edit the transcript before submission.
* **Linguistic NLP Evaluator**: Analyzes vocabulary richness (Type-Token Ratio, technical word lexicon), grammar completeness, fluency (WPM, pause/filler detection), relevance against prompt keywords, and structural transitions, returning calibrated scores across 6 rubrics scaled to 40 points.

---

## 5. Docker Deployment

To launch both backend and frontend in Docker containers:

```bash
docker-compose up --build -d
```

* Frontend: `http://localhost:80`
* Backend: `http://localhost:8080`
