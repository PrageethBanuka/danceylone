#!/bin/bash

# Quick PostgreSQL Setup for Danceylone
# Fixes the connection issues we had earlier

echo "🔧 PostgreSQL Quick Setup"
echo ""

# Find PostgreSQL installation
PSQL_PATH=""
CREATEDB_PATH=""
CREATEUSER_PATH=""

if [ -f "/Library/PostgreSQL/17/bin/psql" ]; then
    PSQL_PATH="/Library/PostgreSQL/17/bin/psql"
    CREATEDB_PATH="/Library/PostgreSQL/17/bin/createdb"
    CREATEUSER_PATH="/Library/PostgreSQL/17/bin/createuser"
    echo "✓ Found PostgreSQL 17"
elif command -v psql &> /dev/null; then
    PSQL_PATH="psql"
    CREATEDB_PATH="createdb"
    CREATEUSER_PATH="createuser"
    echo "✓ Found PostgreSQL in PATH"
else
    echo "❌ PostgreSQL not found. Install with:"
    echo "   brew install postgresql@16"
    exit 1
fi

echo ""
echo "Step 1: Setting up as postgres user"
echo ""

# Use sudo to run as postgres user (no password needed)
sudo -u postgres $PSQL_PATH -c "SELECT version();" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Cannot connect as postgres user"
    echo "Run: ./fix-postgres.sh to configure authentication"
    exit 1
fi

echo "✓ Connected successfully"
echo ""

echo "Step 2: Creating database and user"

# Drop and recreate database
sudo -u postgres $PSQL_PATH << EOF
-- Create database
DROP DATABASE IF EXISTS danceylone_dev;
CREATE DATABASE danceylone_dev;

-- Create user if not exists
DO
\$\$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_user WHERE usename = 'danceylone_user') THEN
      CREATE USER danceylone_user WITH PASSWORD 'danceylone_dev_password';
   END IF;
END
\$\$;

-- Grant database privileges
GRANT ALL PRIVILEGES ON DATABASE danceylone_dev TO danceylone_user;

\q
EOF

echo ""
echo "Step 3: Granting schema permissions"

# Connect to the database and grant schema permissions
sudo -u postgres $PSQL_PATH -d danceylone_dev << EOF
-- Grant schema permissions
GRANT ALL ON SCHEMA public TO danceylone_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO danceylone_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO danceylone_user;

-- Grant default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO danceylone_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO danceylone_user;

\q
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database setup complete!"
    echo ""
    echo "Next steps:"
    echo "1. Switch to PostgreSQL profile: export SPRING_PROFILES_ACTIVE=postgres"
    echo "2. Start backend: ./mvnw spring-boot:run"
    echo "3. Flyway will auto-run migrations"
else
    echo ""
    echo "❌ Setup failed. Common issues:"
    echo "1. PostgreSQL not running: brew services start postgresql@16"
    echo "2. Password incorrect: Reset with initdb or check pg_hba.conf"
    echo "3. Connection refused: Check PostgreSQL is listening on localhost:5432"
fi
