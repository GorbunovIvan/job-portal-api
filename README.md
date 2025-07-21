## 📦 Job portal API

A full-featured **Job Portal REST API** built with Spring Boot, designed to support job listings, applications,
user authentication (with JWT), role-based access control, and Kafka-based application event processing.

---

### 🧰 Tech Stack

* **Java 21**
* **Spring Boot 3.5**

    * Spring Web
    * Spring Data JPA
    * Spring Security (with JWT)
    * Spring Validation
* **PostgreSQL** (Database)
* **Flyway** (DB schema versioning)
* **Kafka** (Messaging)
* **Swagger UI (OpenAPI)** (Docs)
* **Docker + Docker Compose + Kubernetes manifests + Jenkinsfile** (Environment setup)
* **Gradle** (Build tool)
* **JUnit + Mockito + H2 database** (Testing)

---

### 🚀 Getting Started

#### 1. Clone the project

```bash
git clone https://github.com/GorbunovIvan/job-portal-api
cd job-portal-api
```

#### 2. Run with Docker Compose

Make sure you have Docker installed.

```bash
docker-compose up --build
```

This starts:

* Spring Boot app
* PostgreSQL database
* Kafka broker + Zookeeper

#### 3. Run Locally (without Docker)

Update your `application.yml` to point to a local PostgreSQL + Kafka instance.

Then:

```bash
./gradlew bootRun
```

---

### 🔑 API Endpoints

#### Authentication

| Method | Endpoint          | Description                   |
| ------ | ----------------- | ----------------------------- |
| POST   | `/api/auth/login` | Authenticate and retrieve JWT |

**predefined users** are:<br/>
_{ "email": "applicant@example.com", "password": "applicant" }_
<br/>and<br/>
_{ "email": "employer@example.com", "password": "employer" }_

#### Jobs (Employer only)

| Method | Endpoint                          | Description             |
| ------ |-----------------------------------|-------------------------|
| GET    | `/api/jobs`                       | List all jobs           |
| GET    | `/api/jobs/{id}`                  | Get job by id           |
| GET    | `/api/jobs/employer/{employerId}` | Get jobs by employer id |
| POST   | `/api/jobs`                       | Create a job posting    |
| PUT    | `/api/jobs/{id}`                  | Update a job            |
| DELETE | `/api/jobs/{id}`                  | Delete a job            |

#### Applications (Applicant only)

| Method | Endpoint                    | Description           |
|--------| --------------------------- |-----------------------|
| GET    | `/api/applications`         | View my applications  |
| POST   | `/api/applications/{jobId}` | Apply to a job        |
| DELETE | `/api/applications/{jobId}` | Delete my application |

---

### 🔐 Security

* **Login** returns a JWT token.
* **Protected endpoints** require `Authorization: Bearer <token>` header.
* Role-based access ensures isolation between applicants and employers.

---

### 🧪 Running Tests

```bash
./gradlew test
```

Includes:

* Unit tests for services and security
* Integration tests for controllers

---
