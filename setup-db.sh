#!/bin/bash

# PostgreSQL Setup for Danceylone
# Creates database and user for local development

set -e

echo "🐘 Setting up PostgreSQL for Danceylone..."
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Check if PostgreSQL is installed
PSQL_PATH=""
if command -v psql &> /dev/null; then
    PSQL_PATH="psql"
elif [ -f "/Library/PostgreSQL/17/bin/psql" ]; then
    PSQL_PATH="/Library/PostgreSQL/17/bin/psql"
elif [ -f "/opt/homebrew/bin/psql" ]; then
    PSQL_PATH="/opt/homebrew/bin/psql"
else
    echo -e "${RED}❌ PostgreSQL is not installed${NC}"
    echo ""
    echo "Install PostgreSQL:"
    echo "  macOS:   brew install postgresql@16"
    echo "  Ubuntu:  sudo apt install postgresql postgresql-contrib"
    echo ""
    exit 1
fi

echo -e "${GREEN}✓ Found PostgreSQL at: $PSQL_PATH${NC}"

# Check if PostgreSQL is running (skip brew services, use direct connection test)
echo -e "${BLUE}Testing PostgreSQL connection...${NC}"

# Database configuration
DB_NAME="danceylone_dev"
DB_USER="${DB_USERNAME:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-postgres}"

echo -e "${BLUE}Database Configuration:${NC}"
echo "  Name:     $DB_NAME"
echo "  User:     $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""

# Try to connect and create database
echo -e "${GREEN}Creating database (you may be prompted for postgres password)...${NC}"
$PSQL_PATH -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" 2>/dev/null | grep -q 1 && {
    echo -e "${YELLOW}Database '$DB_NAME' already exists. Drop and recreate? (y/n)${NC}"
    read -p "" -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        $PSQL_PATH -U postgres -c "DROP DATABASE $DB_NAME;"
        $PSQL_PATH -U postgres -c "CREATE DATABASE $DB_NAME;"
        echo -e "${GREEN}✓ Database recreated${NC}"
    fi
} || {
    $PSQL_PATH -U postgres -c "CREATE DATABASE $DB_NAME;"
    echo -e "${GREEN}✓ Database created${NC}"
}

# Create user (if not exists)
$PSQL_PATH -U postgres -tc "SELECT 1 FROM pg_user WHERE usename = '$DB_USER'" 2>/dev/null | grep -q 1 || {
    echo -e "${GREEN}Creating user...${NC}"
    $PSQL_PATH -U postgres -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';"
    echo -e "${GREEN}✓ User created${NC}"
}

# Grant privileges
echo -e "${GREEN}Granting privileges...${NC}"
$PSQL_PATH -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"
$PSQL_PATH -U postgres -d $DB_NAME -c "GRANT ALL ON SCHEMA public TO $DB_USER;"
echo -e "${GREEN}✓ Privileges granted${NC}"

echo ""
echo -e "${GREEN}✅ PostgreSQL setup complete!${NC}"
echo ""
echo "Connection details:"
echo "  URL:      jdbc:postgresql://localhost:5432/$DB_NAME"
echo "  Username: $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""
echo "Next steps:"
echo "  1. Run: ./dev.sh"
echo "  2. Flyway will automatically create tables"
echo "  3. Sample data will be loaded"
