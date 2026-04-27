<h1 align="center">InternConnect</h1>
<p align="center">
  <strong>Collaborative platform for sharing internship opportunities</strong><br>
  Master in Information Systems Engineering (ISI) | FSSM Marrakech | 2025/2026
</p>

<p align="center">
  <a href="#-context-and-objectives">Context</a> •
  <a href="#-requirements-analysis">Requirements</a> •
  <a href="#-technical-architecture">Architecture</a> •
  <a href="#-project-organization-scrum">Project Management</a> •
  <a href="#-uml-modeling">UML</a> •
  <a href="#-ui-mockups">Mockups</a> •
  <a href="#-life-cycles">Life Cycles</a>
</p>

---

## 📖 Context and Objectives

**InternConnect** is a **peer-to-peer** digital platform designed for Moroccan students and recent graduates. It addresses a major issue: information asymmetry in access to internships. It leverages collective intelligence by enabling the sharing, consultation, and application to opportunities arising from informal student networks.

### Main Objectives

- 🎯 **Democratize access** to internships by leveraging student networks
- 📂 **Centralize offers** from multiple sources
- 🤝 **Build trust** through community reputation and account verification
- 🌐 **Facilitate professional networking** among students from the start of their training
- 📊 **Provide anonymized indicators** on high-demand internship sectors

### Target Audience

- Students actively seeking internships
- Recent graduates looking for their first experience
- Offer posters (students or graduates aware of an opportunity)

---

## 📋 Requirements Analysis

### Functional Requirements (High-Level View)

| Module | Key Features |
|--------|---------------|
| **Account Management** | Registration with university email validation, JWT/OAuth authentication (Google, LinkedIn), profile management |
| **Internship Offers** | Structured posting, text search, advanced filters (sector, location, duration, compensation), favorites, configurable alerts |
| **Applications** | CV and cover letter submission, real-time status tracking, complete history |
| **Communication** | Internal messaging, notifications (email and in-app) |
| **Administration** | Moderation, role management, global platform statistics |
| **Artificial Intelligence** | Personalized offer recommendations, profile export to PDF (CV) |

### Non-Functional Requirements

| Requirement | Description |
|-------------|-------------|
| ⚡ Performance | Response time ≤ 2s for 95% of requests under normal load |
| 🔒 Security | Data encryption, SQL/XSS/CSRF protection |
| 📈 Scalability | Ability to handle peak periods (back-to-school, end of year) |
| 📱 Accessibility | Responsive interface, compatible with all modern browsers and mobile devices |
| 🔧 Maintainability | Modular code, well-documented, automated testing |

---

## 🏗️ Technical Architecture

Decoupled **three-tier** architecture:

| Layer | Technology | Role |
|-------|------------|------|
| **Presentation** | Thymeleaf (Spring Boot) | Server-side rendering, dynamic HTML templates |
| **Business** | Java / Spring Boot (MVC, Security, Data) | Business logic, secure REST APIs |
| **Data** | Oracle Database + JPA/Hibernate | Persistence, referential integrity |

### Complete Stack

- **Back-end**: Java 17+, Spring Boot 3.x
- **Front-end**: Thymeleaf, HTML5/CSS3, JavaScript
- **Database**: Oracle Database (19c+)
- **Authentication**: Spring Security + JWT, OAuth2
- **AI / Recommendations**: Integrated ML API
- **CI/CD**: GitHub + Jenkins
- **Containerization**: Docker
- **Code Quality**: SonarQube
- **Project Management**: Scrum (Jira Software)

---

## 👥 Project Organization (Scrum)

### Team Roles

| Role | Responsibilities |
|------|------------------|
| **Product Owner** | Product vision, backlog management |
| **Scrum Master** | Scrum ceremonies organization, velocity tracking |
| **Backend Developer** | REST API development, business logic, database design |
| **Frontend Developer** | UI/UX development, Thymeleaf integration |

### Sprint Planning (2 weeks each)

| Sprint | Period | Objective | Deliverables |
|--------|--------|-----------|---------------|
| **Sprint 0** | Week 0 – 1 | Design | UML diagrams, environment setup |
| **Sprint 1** | Week 1 – 2 | Foundations | Authentication (JWT/OAuth), user profiles |
| **Sprint 2** | Week 2 – 4 | Offer Management | Publishing, search, advanced filters |
| **Sprint 3** | Week 4 – 5 | Applications & Comm. | Application submission, messaging, notifications |
| **Sprint 4** | Week 5 – 6 | Admin & AI | Moderation, roles, AI recommendations, final tests |

> **Ceremonies**: Sprint Planning (2h) → Daily Stand-up (15min) → Review (1h) → Retrospective (45min)

---

## 📊 UML Modeling

Three levels of abstraction cover the design:

1. **Functional**: Use case diagrams (global view and breakdown)
2. **Static Structural**: Class diagram with enumerations (`ApplicationStatus`, `OfferStatus`)
3. **Persistence**: Entity-relationship diagram (Oracle physical schema, primary/foreign keys)

*(See files in the `Diagrams/` directory)*

---

## 🔄 Life Cycles

### Internship Offer

Draft → [Publish] → Active → [Close] → Closed → [Archive] → Archived
↘ [Report] → Rejected


### Application

Submitted → [Receive] → Under Review → [Positive Decision] → Accepted
↘ [Negative Decision] → Rejected
↘ [Withdraw] → Withdrawn


---

## 🎨 UI Mockups

*(Lo-fi wireframes – visual reference for the team)*

- **Home / Login Page**: Search engine, recent offers, registration access
- **Student Dashboard**: Ongoing applications, saved offers, AI recommendations, notifications
- **Offer Search & List**: Advanced filters, skill tags, paginated results, quick actions
- **Offer Publishing**: 4-step guided form with progressive validation before moderation
- **Admin Panel**: Consolidated view for offer moderation, report management, statistical tracking

*(See files in the `Diagrams/` directory)*


---

## ✅ Next Steps

1. Iterative development according to defined sprints
2. User testing with a panel of volunteer students
3. Deployment and final documentation

---

## 👥 Project Team

- Mohamed LAFROUH 
- Hafsa FARAH
- Meryem KACHANI
- Khadija ELBIARI

---

<p align="center">
  <i>Document prepared as part of the Master ISI – FSSM, Cadi Ayyad University, Marrakech.</i>
</p>