#!/bin/bash

# Simple PostgreSQL setup without password requirements
# Uses direct file system access as postgres user

echo "🔧 Simple PostgreSQL Database Setup"
echo ""

PSQL="/Library/PostgreSQL/17/bin/psql"
CREATEDB="/Library/PostgreSQL/17/bin/createdb"

if [ ! -f "$PSQL" ]; then
    echo "❌ PostgreSQL not found at /Library/PostgreSQL/17/"
    exit 1
fi

echo "This script will set up the database using sudo."
echo "You'll need to enter your Mac password (not PostgreSQL password)."
echo ""

# Method 1: Try with peer authentication (Unix socket)
echo "Step 1: Creating database..."

# Drop existing database
sudo su - postgres -c "$PSQL -c 'DROP DATABASE IF EXISTS danceylone_dev;'" 2>/dev/null || \
sudo -u postgres $PSQL -c 'DROP DATABASE IF EXISTS danceylone_dev;' 2>/dev/null

# Create database
sudo su - postgres -c "$CREATEDB danceylone_dev" 2>/dev/null || \
sudo -u postgres $CREATEDB danceylone_dev 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ Database created"
else
    echo "❌ Failed to create database"
    echo ""
    echo "Alternative: Use H2 database (no setup needed)"
    echo "Just set: export SPRING_PROFILES_ACTIVE=dev"
    exit 1
fi

echo ""
echo "Step 2: Creating user and granting permissions..."

sudo -u postgres $PSQL -d danceylone_dev << 'EOF'
-- Create user
DROP USER IF EXISTS danceylone_user;
CREATE USER danceylone_user WITH PASSWORD 'danceylone_dev_password';

-- Grant all permissions
GRANT ALL PRIVILEGES ON DATABASE danceylone_dev TO danceylone_user;
GRANT ALL ON SCHEMA public TO danceylone_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO danceylone_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO danceylone_user;

-- Default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO danceylone_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO danceylone_user;

SELECT 'Setup complete!' as status;
\q
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database setup complete!"
    echo ""
    echo "Database: danceylone_dev"
    echo "User: danceylone_user"
    echo "Password: danceylone_dev_password"
    echo ""
    echo "Start your application with:"
    echo "  export SPRING_PROFILES_ACTIVE=postgres"
    echo "  ./mvnw spring-boot:run"
else
    echo ""
    echo "❌ Setup failed"
    echo ""
    echo "Try H2 instead (simpler):"
    echo "  export SPRING_PROFILES_ACTIVE=dev"
    echo "  ./mvnw spring-boot:run"
fi
