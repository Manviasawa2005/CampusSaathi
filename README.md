# 🎓 CampusSaathi
### Your Campus. Connected. Simplified.

**CampusSaathi** is a Java-based academic web application project designed to organize campus-related academic activities and simplify assignment workflows for teachers and students.

This repository contains the application's source code, project documentation, weekly development progress, and supporting materials for the academic project.

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Objectives](#-objectives)
- [Technology Stack](#-technology-stack)
- [Project Modules](#-project-modules)
- [Project Structure](#-project-structure)
- [Team Responsibilities](#-team-responsibilities)
- [Weekly Development Progress](#-weekly-development-progress)
- [Documentation and Deliverables](#-documentation-and-deliverables)
- [Testing Strategy](#-testing-strategy)
- [Setup and Execution](#-setup-and-execution)
- [Current Project Status](#-current-project-status)
- [Team Information](#-team-information)

---

## 📖 Project Overview

CampusSaathi is an academic software development project intended to provide a common platform for campus-related academic workflows.

The project focuses on teacher and student interactions, assignment management, PDF document handling, and related academic information. Development is organized into weekly milestones covering project planning, environment setup, database design, implementation, integration, testing, and documentation.

## 🎯 Objectives

- Develop a structured academic web application using Java technologies.
- Support teacher and student workflows.
- Organize assignment creation and student access to assignments.
- Support assignment document upload and download.
- Apply authentication, session management, and role-based access controls.
- Use MySQL for relational data storage.
- Maintain clear technical documentation and weekly progress records.
- Organize source code and project deliverables through GitHub.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application logic |
| Jakarta Servlets | Server-side request handling |
| JSP | Dynamic web pages, where used |
| JDBC | Database connectivity |
| MySQL | Relational database |
| Apache Tomcat 11 | Web application server |
| Eclipse IDE | Development environment |
| Git | Version control |
| GitHub | Source code hosting and project documentation |

---

## ✨ Project Modules

The following modules describe the intended application areas. Their implementation status will be updated against the actual source code and verified tests.

### 1. Authentication and Session Management
- Teacher and student login workflows.
- Session handling.
- Role-based access checks.

### 2. Teacher Assignment Management
- Assignment creation.
- Assignment details and related data handling.
- PDF attachment upload workflow.

### 3. Student Assignment Portal
- Student assignment listing.
- Batch-based filtering, where configured.
- Assignment document viewing and downloading.

### 4. PDF Upload and Storage
- PDF file handling.
- UUID-based filenames, as reported in the development progress.
- File-size validation and storage configuration.

### 5. PDF Access Control
- Role checks for assignment document access.
- Teacher/student access restrictions, subject to continued verification.

### 6. Marks-Related Functionality
- Integration of existing marks information into the student portal.
- Association of assignment status with marks-related information.

---

## 📂 Project Structure

The repository currently contains the Java web application source and Eclipse project files.

```text
CampusSaathi/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── campussaathi/
│       │           └── controller/
│       └── webapp/
├── .classpath
├── .gitignore
├── .project
└── README.md
```

Additional documentation directories and supporting files will be added as the corresponding materials are prepared and verified.

---

## 👥 Team Responsibilities

The team has divided responsibilities across project planning, backend development, database design, authentication, and documentation.

| Team Member | Reported Responsibility |
|---|---|
| Manvi Asawa | Backend development, teacher assignment functionality, README and repository organization |
| Mausam | Database schema design and updates |
| Nikita | Authentication, session handling, student portal and PDF access control |
| Nikita Rathore | Student assignment view |
| Kusum | Marks-related integration, SRS, abstract and report preparation |

*Note: Responsibilities are based on the team's current progress notes and may overlap. Final ownership will be confirmed with the team.*

---

## 📅 Weekly Development Progress

This section tracks the project across 12 academic weeks. Statuses reflect the progress information currently available and will be updated as deliverables are verified.

### Progress Summary

| Week | Objective | Status |
|---|---|---|
| 1 | Project scope and team responsibilities | Completed |
| 2 | Development environment setup | Completed |
| 3 | Database schema design | Completed |
| 4 | Authentication module | In Progress |
| 5 | Teacher assignment creation | In Progress |
| 6 | PDF upload and storage | Completed |
| 7 | Student assignment portal | In Progress |
| 8 | PDF access control | In Progress |
| 9 | Marks-related functionality | In Progress |
| 10 | Integration testing | In Progress |
| 11 | Documentation and presentation preparation | In Progress |
| 12 | Final demonstration and GitHub organization | Pending |

### Week 1 — Project Scope and Team Responsibilities

## Week 1: Project Guide, Team Formation & Project Abstract

### 1. Project Guide

- **Guide Name:** Er. Ram Babu Buri

### 2. Department & Academic Specification

- **Department:** Artificial Intelligence and Data Science (AI & DS)
- **Academic Context:** RTU, 5th Semester
- **Project Title:** CampusSaathi — Academic Assignment Management System
- **Project Tagline:** Your Campus. Connected. Simplified.

### 3. Project Team & Responsibilities

| Team Member | Role | Module / Responsibility | Enrollment / Roll No. | Email |
|---|---|---|---|---|
| Manvi Asawa | Backend Development | Teacher assignment management, backend development, README and repository organization | Pending | Pending |
| Mausam | Database Developer | Database design and schema | Pending | Pending |
| Nikita | Authentication & Portal Developer | Authentication, session handling, student portal and PDF access | Pending | Pending |
| Nikita Rathore | Student Portal Developer | Student assignment viewing | Pending | Pending |
| Kusum | Documentation & Integration | Marks integration, SRS, abstract and project report | Pending | Pending |

### 4. Project Abstract

CampusSaathi — Academic Assignment Management System is a centralized Java-based web application designed to streamline assignment handling for college students and teachers. It provides a structured, accessible platform for managing assignment information and associated PDF question papers, replacing fragmented communication channels with a unified workflow.

The application uses Java 21, Jakarta Servlets, JSP, JDBC, Apache Tomcat 11, MySQL, HTML and CSS. Its core features include role-based authentication, session tracking, teacher-side assignment creation, optional PDF uploads, batch-based student assignment retrieval and servlet-based authorization for secure document access.

The project follows the Model-View-Controller (MVC) design pattern and integrates MySQL for persistent storage. It also brings together assignment management, document access and marks-related functionality in a centralized academic platform.

The project is developed under the academic framework of Rajasthan Technical University (RTU) and serves as an academic prototype demonstrating Java web technologies and software development practices.

### 5. Week 1 Deliverables

- Project title and scope identification
- Project guide and department details
- Initial team responsibility allocation
- Project abstract

### 6. Week 1 Status

**Status:** Partially documented — project and team details recorded; enrollment numbers and email addresses are yet to be added.

### 7. Supporting Evidence

- Project abstract document: `ABSTRACTCAMPUSSAATHI.docx`
- Team responsibility chart: Add the actual file or image link when available.
  📄 **Full Abstract:** [View / Download CampusSaathi Abstract](ABSTRACTCAMPUSSAATHI.docx)

### Week 2 — Development Environment Setup

**Objective:** Prepare the development environment.

**Work completed:**
- Installed Java 21, Eclipse IDE, Apache Tomcat 11, and MySQL.
- Created the initial Eclipse Dynamic Web Project.

**Deliverables:** Base project folder and Tomcat configuration notes.

**Evidence:** Local CampusSaathi project and Eclipse setup screenshots, where available.

**Status:** Completed

### Week 3 — Database Schema Design

**Objective:** Design the database structure for assignment management.

**Work completed:**
- Mausam designed the `assignments` table schema.
- Manvi reviewed the backend feasibility of the proposed schema.

**Deliverables:** Draft `schema.sql` and ER diagram.

**Evidence:** The database schema draft is reported in `database/schema.sql`; the file will be linked after it is added to and verified in the repository.

**Status:** Completed

### Week 4 — Authentication Module

**Objective:** Implement login, session handling, and role-based access checks.

**Work completed:**
- Nikita worked on login JSP pages.
- Session handling and role-based checks were developed.

**Deliverables:** Login JSP pages and `AuthenticationServlet.java`.

**Evidence:** Login screenshots and relevant source code or commit links, once available.

**Status:** In Progress

### Week 5 — Teacher Assignment Creation

**Objective:** Enable teachers to create assignments and attach PDFs.

**Work completed:**
- Manvi developed the teacher assignment servlet.
- Integrated multipart PDF upload logic into the assignment workflow.

**Deliverables:** Assignment servlet and assignment creation JSP form.

**Evidence:** Relevant Java source code and form screenshots, after repository paths are verified.

**Status:** In Progress

### Week 6 — PDF Upload and Storage

**Objective:** Support PDF uploads and organize uploaded files.

**Work completed:**
- Implemented UUID-based filenames.
- Added file-size validation.
- Updated the database schema with a `pdf_filename` field.
- Configured the intended upload storage directory.

**Deliverables:** Updated upload logic, schema changes, and storage configuration.

**Evidence:** Relevant source changes and database schema update.

**Status:** Completed

### Week 7 — Student Assignment Portal

**Objective:** Allow students to view and access available assignments.

**Work completed:**
- Nikita Rathore developed the student assignment view.
- Integrated batch filtering and View/Download PDF links.

**Deliverables:** Student assignment servlet and JSP page.

**Evidence:** Student portal screenshots and relevant source code, once verified and available.

**Status:** In Progress

### Week 8 — PDF Access Control

**Objective:** Restrict PDF access according to user roles.

**Work completed:**
- Nikita added role checks to `AssignmentPdfServlet.java`.
- The team reported verifying teacher/student access restrictions.

**Deliverables:** Updated PDF access servlet.

**Evidence:** Servlet source code and test records, when available.

**Status:** In Progress

### Week 9 — Marks-Related Functionality

**Objective:** Integrate marks information into the student portal.

**Work completed:**
- Kusum integrated the existing marks display into the student portal.
- Linked assignment status with marks-related information.

**Deliverables:** Updated JSP pages and backend queries.

**Evidence:** Marks page screenshots and related source code, when available.

**Status:** In Progress

### Week 10 — Integration Testing

**Objective:** Test the integrated assignment workflows.

**Work completed:**
- The team tested assignment creation, PDF upload, student assignment viewing, and access control together.

**Deliverables:** Test cases and a bug list.

**Evidence:** Testing notes reported in `docs/testing.md`; the document will be linked once added and verified.

**Status:** In Progress

### Week 11 — Documentation and Presentation Preparation

**Objective:** Prepare academic documentation and presentation materials.

**Work completed:**
- Kusum prepared drafts of the abstract, SRS, and project report.
- Manvi drafted the README.
- The team prepared presentation slides.

**Deliverables:** Abstract, SRS, report drafts, README, and presentation slides.

**Evidence:** Actual document and slide files will be linked after they are added to the repository.

**Status:** In Progress

### Week 12 — Final Demonstration and GitHub Organization

**Objective:** Organize the repository and prepare final submission materials.

**Work completed:**
- Set up the CampusSaathi GitHub repository under `Manviasawa2005`.
- Uploaded the Java project source code and README.

**Deliverables:** Organized GitHub repository, final demonstration video, and presentation slides.

**Evidence:** [CampusSaathi GitHub Repository](https://github.com/Manviasawa2005/CampusSaathi). Final video and remaining submission materials are pending.

**Status:** Pending

---

## 📑 Documentation and Deliverables

The following checklist will be updated as files are prepared and committed to the repository.

- [ ] Project abstract
- [ ] Software Requirements Specification (SRS)
- [ ] Team responsibility chart
- [ ] Use-case diagram
- [ ] Class diagram
- [ ] Entity Relationship (ER) diagram
- [ ] Database schema and design
- [ ] UI mock-ups and screenshots
- [ ] Weekly progress tracking sheet
- [ ] Module documentation
- [ ] Test cases and test results
- [ ] Bug and issue log
- [ ] Final project report
- [ ] Presentation slides
- [ ] Demonstration video

---

## 🧪 Testing Strategy

Testing will cover the main application workflows, including:

- Login and session handling.
- Teacher assignment creation.
- PDF upload validation and storage.
- Student assignment listing and filtering.
- PDF viewing and downloading.
- Role-based access restrictions.
- Marks-related information, where implemented.
- Database connectivity and integration.

Test cases will record the scenario, expected result, actual result, and outcome. A test will be marked as passed only after execution and verification.

---

## 🚀 Setup and Execution

### Prerequisites

- Java Development Kit (JDK) 21
- Apache Tomcat 11
- MySQL
- Eclipse IDE or another compatible Java IDE

### Setup Notes

1. Clone or download this repository.
2. Import the project into Eclipse using the appropriate existing project configuration.
3. Configure Apache Tomcat 11 in the development environment.
4. Configure the local MySQL database according to the verified project schema.
5. Set local database credentials securely.
6. Deploy the application to Tomcat and test the required workflows.

*The exact database setup, required tables, and deployment instructions will be added after the project configuration and schema are verified.*

**Security note:** Do not commit database passwords, API keys, private configuration files, real student data, or sensitive uploaded documents.

---

## 📊 Current Project Status

- Java source code: Uploaded to GitHub.
- Project structure: Present in the repository.
- Weekly progress tracker: Documented in this README.
- Supporting documentation: Being organized and verified.
- Integration and testing evidence: To be added as available.
- Final presentation and demonstration video: Pending.

## 🔗 Repository

**GitHub:** [Manviasawa2005/CampusSaathi](https://github.com/Manviasawa2005/CampusSaathi)

## 🎓 Academic Information

| Field | Details |
|---|---|
| Project Name | CampusSaathi |
| Tagline | Your Campus. Connected. Simplified. |
| Project Type | Academic Software Development Project |
| Project Guide | To be added |
| Institution / Department | To be added |
| Academic Session | To be added |

---

## 📜 License

## License

This project is developed for academic purposes as part of the B.Tech project work. The licensing and reuse terms have not yet been formally decided.

---

*CampusSaathi — Your Campus. Connected. Simplified.*
