# Database Migration Guide

## Quick Setup (First Time)

### 1. Install PostgreSQL

**macOS (Homebrew):**
```bash
brew install postgresql@16
brew services start postgresql@16
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Windows:**
Download from: https://www.postgresql.org/download/windows/

### 2. Run Database Setup

```bash
./setup-db.sh
```

This will:
- Create `danceylone_dev` database
- Set up user credentials
- Grant necessary privileges

### 3. Start the Application

```bash
./dev.sh
```

Flyway will automatically:
- Create all tables
- Load seed data
- Apply future migrations

## Manual Database Commands

### Connect to Database
```bash
psql -U postgres -d danceylone_dev
```

### Common SQL Queries
```sql
-- View all tables
\dt

-- Check users
SELECT * FROM users;

-- Check products
SELECT * FROM products;

-- View migration history
SELECT * FROM flyway_schema_history;
```

### Reset Database
```bash
./setup-db.sh  # Choose 'y' to recreate
```

## Migration Files

Located in: `backend/src/main/resources/db/migration/`

**Naming Convention:** `V{version}__{description}.sql`

Examples:
- `V1__Initial_Schema.sql` - Creates all tables
- `V2__Seed_Data.sql` - Loads sample data
- `V3__Add_User_Phone.sql` - Future migration

## Creating New Migrations

1. Create file: `V3__Your_Description.sql`
2. Add SQL changes:
```sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```
3. Restart backend - Flyway auto-applies

## Profiles

### Development (default)
- Database: `danceylone_dev`
- Flyway: enabled
- SQL logging: enabled
- Swagger: enabled

### Production
- Activate: `export SPRING_PROFILES_ACTIVE=prod`
- Database: from `DATABASE_URL` env var
- Flyway: enabled (migrations only)
- SQL logging: disabled
- Swagger: disabled

## Troubleshooting

**PostgreSQL not starting?**
```bash
# macOS
brew services restart postgresql@16

# Ubuntu
sudo systemctl restart postgresql
```

**Connection refused?**
Check PostgreSQL is running:
```bash
pg_isready
```

**Flyway validation failed?**
```bash
# Reset Flyway state (dev only)
psql -U postgres -d danceylone_dev -c "DROP TABLE flyway_schema_history;"
```

## Database Schema

### Core Tables
- **users** - User accounts and authentication
- **products** - Product catalog
- **orders** - Order tracking
- **order_items** - Order line items
- **cart** - Shopping cart
- **cart_items** - Cart contents
- **reviews** - Product reviews

### Sample Credentials
- **Admin**: admin@danceylone.com / admin123
- **User**: user@danceylone.com / admin123

## Environment Variables

```bash
# Optional - defaults are set in application-dev.yml
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export DATABASE_URL=jdbc:postgresql://localhost:5432/danceylone_dev
```
