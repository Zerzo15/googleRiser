# Google Riser Submission: Company Intelligence OS

## Executive Overview (Business & Finance)
When evaluating new businesses, staff lose valuable time manually searching the web to verify basic information. Our team present an automated intelligence platform designed to eliminate this manual data entry. 
*   **Initiation:** Input a company's name or website into the Riser dashboard.
*   **Aggregation & Synthesis:** Using AI to search the live internet and synthesize public data into key metrics (sector, scale, products, and target market).
*   **Delivery:** The finalized profile is securely saved to the workspace for future reference and can be instantly exported.

## Technical Architecture (Engineering)
Monolithic Spring Boot backend serving a React Single Page Application, built for reliability and real-time AI processing.
*   **Frontend Ecosystem:** React 19 and Vite with raw CSS. Features exponential backoff polling to efficiently track asynchronous AI tasks without overloading the server.
*   **Backend Infrastructure:** Java 21, Spring Boot 3.x, and Spring Security. Features strict JWT authentication with role-based access control and centralized Global Exception Handling.
*   **Data Layer:** PostgreSQL (via Google Cloud SQL) for persistent storage and Redis (via Bucket4j) for robust IP and user-based rate limiting.
*   **AI Integration:** Integrates the Google Gemini 1.5 Flash API with native Google Search Grounding to guarantee factual accuracy and completely prevent LLM hallucinations.

## Local Setup Instructions
Follow these steps to run the application locally:
*   **Environment:** Configure `GEMINI_API_KEY`, `JWT_SECRET`, and `SPRING_DATASOURCE_PASSWORD` in your environment variables.
*   **Infrastructure:** Start the local database and Redis cache by running `docker-compose up -d` in the `backend/app` directory.
*   **Backend Server:** Execute `./mvnw spring-boot:run` to launch the Spring application.
*   **Frontend Client:** Navigate to the `frontend` folder, run `npm install`, and start the Vite development server with `npm run dev`.
