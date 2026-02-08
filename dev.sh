#!/bin/bash

# Danceylone Development Startup Script
# Starts both backend (Spring Boot) and frontend (Next.js)

set -e

echo "🚀 Starting Danceylone Development Environment..."
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if backend directory exists
if [ ! -d "backend" ]; then
    echo "❌ Backend directory not found. Run this script from project root."
    exit 1
fi

# Check if frontend directory exists
if [ ! -d "frontend" ]; then
    echo "❌ Frontend directory not found. Run this script from project root."
    exit 1
fi

# Function to kill background processes on exit
cleanup() {
    echo ""
    echo "🛑 Shutting down development servers..."
    kill $(jobs -p) 2>/dev/null || true
    exit
}

trap cleanup SIGINT SIGTERM

# Start Backend (Spring Boot)
echo -e "${BLUE}[Backend]${NC} Starting Spring Boot on http://localhost:8080"
echo -e "${BLUE}[Backend]${NC} Swagger UI: http://localhost:8080/swagger-ui.html"
cd backend
./mvnw spring-boot:run > /tmp/danceylone-backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Wait for backend to start
echo -e "${YELLOW}[Backend]${NC} Waiting for Spring Boot to start..."
sleep 10

# Check if backend started successfully
if ! kill -0 $BACKEND_PID 2>/dev/null; then
    echo -e "${YELLOW}[Backend]${NC} Failed to start. Check logs: tail -f /tmp/danceylone-backend.log"
fi

# Install frontend dependencies if node_modules doesn't exist
if [ ! -d "frontend/node_modules" ]; then
    echo -e "${GREEN}[Frontend]${NC} Installing dependencies..."
    cd frontend
    npm install
    cd ..
fi

# Start Frontend (Next.js)
echo -e "${GREEN}[Frontend]${NC} Starting Next.js on http://localhost:3000"
cd frontend
npm run dev > /tmp/danceylone-frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo ""
echo "✅ Development servers started!"
echo ""
echo "📍 URLs:"
echo "   Frontend:  http://localhost:3000"
echo "   Backend:   http://localhost:8080"
echo "   Swagger:   http://localhost:8080/swagger-ui.html"
echo "   H2 Console: http://localhost:8080/h2-console"
echo ""
echo "📋 Logs:"
echo "   Backend:  tail -f /tmp/danceylone-backend.log"
echo "   Frontend: tail -f /tmp/danceylone-frontend.log"
echo ""
echo "Press Ctrl+C to stop all servers"

# Wait for processes
wait
