# Danceylone Development Guide

## Quick Start

Start both backend and frontend with one command:

```bash
./dev.sh
```

This will start:
- **Backend** (Spring Boot) → http://localhost:8080
- **Frontend** (Next.js) → http://localhost:3000
- **Swagger UI** → http://localhost:8080/swagger-ui.html
- **H2 Console** → http://localhost:8080/h2-console

Press `Ctrl+C` to stop all servers.

## Manual Start

### Backend Only
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Only
```bash
cd frontend
npm install  # First time only
npm run dev
```

## View Logs

```bash
# Backend logs
tail -f /tmp/danceylone-backend.log

# Frontend logs
tail -f /tmp/danceylone-frontend.log
```

## Configuration

- **JWT Secret**: Configured in `backend/src/main/resources/application.yml`
- **Database**: H2 in-memory (no setup required)
- **Profiles**: Development mode active by default

## Troubleshooting

**Port already in use?**
```bash
# Kill process on port 8080 (backend)
lsof -ti:8080 | xargs kill -9

# Kill process on port 3000 (frontend)
lsof -ti:3000 | xargs kill -9
```

**Frontend dependencies issue?**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```
