# Stage 1: Build the Vite React App
FROM node:20-alpine AS build
WORKDIR /app

# Accept the API URL as an argument during the build process
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# Stage 2: Serve the App using Nginx
FROM nginx:alpine
# Copy the built React assets to the Nginx public folder
COPY --from=build /app/dist /usr/share/nginx/html

# Overwrite default Nginx configuration to support Single Page Applications (SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Cloud Run expects the container to listen on $PORT (usually 8080)
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]