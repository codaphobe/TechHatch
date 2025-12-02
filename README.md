# TechHatch - Job Portal for Tech Talent

A full-stack job portal connecting tech professionals with companies. Built with Spring Boot, React, and MySQL, deployed with Docker, GitHub Actions CI/CD, Railway (backend), and Vercel (frontend).

---

## 📋 Overview

TechHatch is a monorepo containing both backend and frontend applications:

- **Backend**: Java Spring Boot REST API with JWT authentication, job management, and application tracking
- **Frontend**: React 18 single-page application with advanced job search and dual-role dashboards
- **Database**: MySQL 8.0 hosted on Railway
- **Deployment**: GitHub Actions CI/CD pipeline with Docker containerization

### Key Features

- **Authentication**: JWT-based login with role-based access (Candidate, Recruiter)
- **Job Management**: Create, search, filter, and manage job postings
- **Advanced Search**: Filter by location, job type, experience level, and salary range with pagination
- **Application Tracking**: Multi-stage workflow (Applied → Under Review → Interview → Offered/Rejected)
- **Recruiter Dashboard**: Manage posted jobs, review applications, update candidate status
- **Candidate Dashboard**: Track applications, view job recommendations, manage profile

---

## 🛠️ Tech Stack

**Backend:**
- Java 21
- Spring Boot 3.2
- Spring Security (JWT)
- Spring Data JPA
- MySQL 8.0
- Docker
- Maven

**Frontend:**
- React 18
- React Router v6
- Tailwind CSS
- Axios
- React Hook Form
- Context API

**DevOps:**
- GitHub Actions (CI/CD)
- Docker & Docker Compose
- Railway (Backend + MySQL)
- Vercel (Frontend)
- GHCR (Docker Registry)

---

## 📚 Project Structure

```
techhatch/
├── backend/
│   ├── src/main/java/com/techhatch/
│   │   ├── config/           # Security & CORS config
│   │   ├── controller/       # REST endpoints
│   │   ├── service/          # Business logic
│   │   ├── repository/       # Data access
│   │   ├── model/            # JPA entities
│   │   ├── dto/              # Request/response DTOs
│   │   └── exception/        # Error handling
│   ├── src/main/resources/
│   │   ├── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── pages/            # Page components
│   │   ├── context/          # Auth context
│   │   ├── services/         # API calls
│   │   ├── hooks/            # Custom hooks
│   │   └── utils/            # Helpers
│   ├── .env.local
│   └── package.json
│
├── docker-compose.yml
├── .github/workflows/
│   └── deploy.yml            # CI/CD pipeline
└── README.md
```

---

## 🔌 API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user (Candidate/Recruiter)
- `POST /api/v1/auth/login` - Login and get JWT token
- `GET /api/v1/auth/me` - Get current user info

### Jobs
- `GET /api/v1/jobs` - List all jobs with filters (public)
- `GET /api/v1/jobs/{id}` - Get job details
- `POST /api/v1/jobs` - Create job (Recruiter only)
- `PUT /api/v1/jobs/{id}` - Update job (owner only)
- `DELETE /api/v1/jobs/{id}` - Delete job (owner only)
- `GET /api/v1/jobs/recruiter/my-jobs` - Get recruiter's posted jobs

### Applications
- `POST /api/v1/applications` - Apply to job (Candidate only)
- `GET /api/v1/applications/candidate/my-applications` - Get candidate's applications
- `GET /api/v1/applications/recruiter/job/{jobId}` - Get applications for a job (Recruiter)
- `PATCH /api/v1/applications/{id}/status` - Update application status (Recruiter only)

### Profiles
- `GET /api/v1/profile/candidate/{userId}` - Get candidate profile
- `PUT /api/v1/profile/candidate` - Update candidate profile
- `GET /api/v1/profile/recruiter/{userId}` - Get recruiter profile
- `PUT /api/v1/profile/recruiter` - Update recruiter profile

---

## 🧪 Testing

### Manual API Testing with Postman

1. Import the Postman collection from `/postman` directory
2. Set environment variables:
   - `baseUrl`: http://localhost:8080/api/v1
   - `token`: (auto-populated after login)
3. Test all endpoints with provided example requests

**Test Coverage:**
- Authentication flows (register, login, token validation)
- Job CRUD operations with authorization checks
- Job search with multiple filters
- Application workflow (apply, prevent duplicates, update status)
- Role-based access control

---

## 🐳 Deployment

### GitHub Actions CI/CD Pipeline

On every push to `main` branch:

1. **Build** - Compile and build Docker images for backend
2. **Push** - Push image to GHCR (GitHub Container Registry)
3. **Deploy** - Railway automatically deploys from GHCR image

**Pipeline File**: `.github/workflows/maven-publish.yml`

### Backend (Railway)

- Backend URL: `https://techhatch-backend.up.railway.app`
- Auto-deploys on GHCR image update

### Frontend (Vercel)

- Frontend URL: `https://techhatch.vercel.app`
- Auto-deploys on GitHub push

### Database (Railway MySQL)

- Hosted on Railway as an active service with no downtime
- Connection string provided in Railway dashboard
- Schema auto-migrates on application startup, with a `initialize.sql` file

---

## 📝 Environment Variables

**Backend (application-prod.properties):**
```properties
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
client.url={CLIENT_URL}
```

**Frontend (.env.production):**
```
REACT_APP_API_URL=https://your-backend-url/api/v1
```

---

## 🔐 Security

- Passwords encrypted with BCrypt (strength 12)
- JWT tokens with 24-hour expiration
- Spring Security role-based access control
- Protected routes on frontend with automatic redirect to login
- CORS configured for production domains only
- Secrets managed through GitHub Secrets and platform dashboards

---

## 📊 Database Schema

7 interconnected entities:
- **users** - Authentication & roles
- **candidate_profiles** - Candidate details & skills
- **recruiter_profiles** - Company information
- **jobs** - Job postings
- **applications** - Job applications with workflow stages

Constraints:
- Unique (job_id, candidate_id) on applications table to prevent duplicates
- Cascade delete for referential integrity

---

## 👤 Author

**Sanjay S K**
[GitHub](https://github.com/codaphobe) | [LinkedIn](https://linkedin.com/in/sanjay-kutakanakeri)

---

**Deployed Applications:**
- Live app : [TechHatch](https://techhatch.vercel.app)
- GitHub Repository: [Repo URL](your-repo-url)