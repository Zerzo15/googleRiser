# Google Riser Submission: Company Intelligence OS

## Executive Overview (Business & Finance)

When evaluating new businesses, staff lose valuable time manually searching the web to verify basic information. Our team present an automated intelligence platform designed to eliminate this manual data entry.

- **Initiation:** Input a company's name or website into the dashboard.
- **Aggregation & Synthesis:** Using AI to search the live internet and synthesize public data into key metrics (sector, scale, products, and target market).
- **Delivery:** The finalized profile is securely saved to the workspace for future reference and can be instantly exported.

## Technical Architecture (Engineering)

Monolithic Spring Boot backend serving a React Single Page Application, built for reliability and real-time AI processing.

- **Frontend Ecosystem:** React 19 and Vite with raw CSS. Features exponential backoff polling to efficiently track asynchronous AI tasks without overloading the server.
- **Backend Infrastructure:** Java 21, Spring Boot 4.x, and Spring Security. Features strict JWT authentication with role-based access control and centralized Global Exception Handling.
- **Data Layer:** PostgreSQL (via Google Cloud SQL) for persistent storage and Redis (via Bucket4j) for robust IP and user-based rate limiting.
- **AI Integration:** Uses a server-side AI provider with native web search and JSON output. The provider and credentials are never exposed to the frontend.

## Google Cloud Deployment

The platform is fully deployed and managed on Google Cloud Platform (GCP) for high availability and security.

- **Compute:** Hosted on **Google Cloud Run**, providing a fully managed, serverless execution environment that automatically scales based on HTTP traffic.
- **Database:** Powered by **Google Cloud SQL (PostgreSQL)** for secure, persistent relational data storage.
- **Security:** Environment variables and sensitive credentials (such as `AI_API_KEY` and `JWT_SECRET`) are securely injected into the container at runtime.

## Local Setup Instructions

Follow these steps to run the application locally:

- **Environment:** Configure `AI_API_KEY`, `AI_API_URL`, `AI_MODEL`, `JWT_SECRET`, and `SPRING_DATASOURCE_PASSWORD` in the DEV environment variables. For the current DeepSeek setup, use `https://api.deepseek.com/responses` and `deepseek-v4-flash` for the URL/model.
- **Infrastructure:** Start PostgreSQL locally and, for production-parity rate limiting, Redis by running `docker-compose up -d` in the `backend/app` directory. The `dev` profile uses an in-memory rate-limit fallback when Redis is unavailable.
- **Backend Server:** Execute `./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"` to launch the Spring application.
- **Frontend Client:** Navigate to the `frontend` folder, run `npm install`, and start the Vite development server with `npm run dev`.
