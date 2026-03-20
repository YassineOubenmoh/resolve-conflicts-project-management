<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Project Management & Conflict Resolution Platform</title>
<style>
    body {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji";
        line-height: 1.6;
        color: #24292e;
        padding: 20px;
        max-width: 900px;
        margin: auto;
        background-color: #f9f9f9;
    }
    h1, h2, h3, h4 {
        color: #0366d6;
    }
    code {
        background-color: #f1f8ff;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 90%;
    }
    pre {
        background-color: #f1f8ff;
        padding: 12px;
        border-radius: 6px;
        overflow-x: auto;
    }
    ul {
        list-style-type: disc;
        margin-left: 20px;
    }
    li {
        margin: 5px 0;
    }
    .badge {
        display: inline-block;
        padding: 2px 6px;
        background-color: #28a745;
        color: white;
        border-radius: 4px;
        font-size: 0.85em;
    }
    .warning {
        color: #d73a49;
        font-weight: bold;
    }
</style>
</head>
<body>

<h1>📌 Project Management & Conflict Resolution Platform</h1>

<h2>🧾 Overview</h2>
<p>This project is a <strong>Project Management and Conflict Resolution Platform</strong> designed to coordinate activities across multiple departments and ensure smooth project execution.</p>
<p>It enables teams to:</p>
<ul>
    <li>Define and manage project workflows through <strong>phases (gates)</strong></li>
    <li>Assign responsibilities to different departments</li>
    <li>Detect and resolve <strong>conflicts (impacts)</strong> between projects</li>
    <li>Ensure controlled project progression with validation mechanisms</li>
</ul>
<p>The system is built using a <strong>microservices architecture</strong> with:</p>
<ul>
    <li><strong>Spring Boot</strong> (backend services)</li>
    <li><strong>React</strong> (frontend)</li>
    <li><strong>RabbitMQ</strong> (asynchronous communication)</li>
    <li><strong>MinIO</strong> (document management)</li>
</ul>

<h2>🏗️ Architecture</h2>
<pre>
Frontend (React)
       ↓
API Gateway / Backend Services (Spring Boot)
       ↓
Message Broker (RabbitMQ)
       ↓
Storage (MinIO)
</pre>

<h3>🔧 Components</h3>
<ul>
    <li><strong>Frontend</strong>: React interface for project creation, tracking, and validation</li>
    <li><strong>Backend</strong>: Spring Boot microservices handling business logic and workflows</li>
    <li><strong>Message Broker</strong>: RabbitMQ for async communication</li>
    <li><strong>Document Storage</strong>: MinIO for project files</li>
</ul>

<h2>👥 Roles</h2>
<h4>🧑‍💼 OWNER</h4>
<ul>
    <li>Creates and manages projects</li>
    <li>Assigns SPOCs for each department</li>
</ul>

<h4>📌 SPOC (Single Point of Contact)</h4>
<ul>
    <li>Represents a department</li>
    <li>Assigns users to add and review impacts</li>
</ul>

<h2>🔄 Project Creation Process</h2>
<ol>
    <li>The <strong>OWNER</strong> creates a project</li>
    <li>SPOCs from all departments are assigned</li>
    <li>Each SPOC defines responsible users for impacts and validations</li>
    <li>Each department receives required actions for each project phase</li>
</ol>

<h2>⚙️ Key Concepts</h2>
<h4>📌 Required Actions</h4>
<p>Tasks assigned to each department during a specific <strong>project phase (gate)</strong>.</p>

<h4>⚠️ Impacts</h4>
<p>An <strong>impact</strong> represents a conflict caused by a required action. Conflicts may involve:</p>
<ul>
    <li>Resources</li>
    <li>Deadlines</li>
    <li>Dependencies</li>
    <li>Other project constraints</li>
</ul>

<h2>🧩 Impact Management</h2>
<ul>
    <li><span class="badge">✅ Accepted</span>: Conflict is valid and must be resolved</li>
    <li><span class="badge">❌ Refused</span>: No real conflict exists</li>
    <li><span class="badge">✏️ To Be Modified</span>: Impact needs revision</li>
</ul>

<h2>🚦 Project Progression Process</h2>
<p>A project can only move to the next <strong>gate (phase)</strong> when:</p>
<ul>
    <li>All departments have validated their required actions</li>
    <li>No impacts are pending resolution</li>
</ul>
<p>This ensures controlled progression and coordination.</p>

<h2>📂 Document Management</h2>
<ul>
    <li>Files are stored using <strong>MinIO</strong></li>
    <li>Supports uploading, secure storage, and easy retrieval of project documents</li>
</ul>

<h2>🔁 Asynchronous Processing</h2>
<ul>
    <li>RabbitMQ handles event-driven communication</li>
    <li>Decouples microservices and background tasks</li>
</ul>

<h2>🚀 Getting Started</h2>
<h4>🔧 Prerequisites</h4>
<ul>
    <li>Java 17+</li>
    <li>Node.js & npm</li>
    <li>Docker (optional)</li>
    <li>RabbitMQ</li>
    <li>MinIO</li>
</ul>

<h4>▶️ Backend (Spring Boot)</h4>
<pre>
cd backend
mvn clean install
mvn spring-boot:run
</pre>

<h4>💻 Frontend (React)</h4>
<pre>
cd frontend
npm install
npm start
</pre>

<h4>🐳 Optional: Docker</h4>
<pre>
docker-compose up -d
</pre>

<h2>📡 Key Features</h2>
<ul>
    <li>Project lifecycle management</li>
    <li>Multi-department collaboration</li>
    <li>Conflict detection and resolution</li>
    <li>Role-based access (OWNER, SPOC)</li>
    <li>Gate-based workflow validation</li>
    <li>Document management with MinIO</li>
    <li>Event-driven architecture with RabbitMQ</li>
</ul>

<h2>📌 Future Improvements</h2>
<ul>
    <li>Notifications system (email / real-time)</li>
    <li>Advanced reporting & dashboards</li>
    <li>Enhanced role-based permissions</li>
    <li>Audit logs & tracking</li>
    <li>Integration with external systems</li>
</ul>

<h2>👨‍💻 Author</h2>
<p>Your Name</p>

<h2>📄 License</h2>
<p>MIT License</p>

</body>
</html>
