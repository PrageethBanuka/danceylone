#!/bin/bash

# Quick Database Setup - Uses your existing PostgreSQL installation
# Just creates the database, Spring Boot will handle the schema via Flyway

echo "🚀 Quick Database Setup for Danceylone"
echo ""

# Use PostgreSQL 17
PSQL="/Library/PostgreSQL/17/bin/psql"

echo "Creating database danceylone_dev..."
echo "Enter your PostgreSQL postgres user password when prompted"
echo ""

# Create database (will prompt for password)
$PSQL -U postgres -c "CREATE DATABASE danceylone_dev;" 2>/dev/null || echo "Database may already exist - that's OK!"

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next step: Run ./dev.sh to start the application"
echo "Flyway will automatically create all tables and load sample data"
