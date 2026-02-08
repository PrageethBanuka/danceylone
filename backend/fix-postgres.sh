#!/bin/bash

# Fix PostgreSQL password authentication for local development

echo "🔧 PostgreSQL Authentication Fix"
echo ""

PG_HBA_CONF="/Library/PostgreSQL/17/data/pg_hba.conf"
PG_CTL="/Library/PostgreSQL/17/bin/pg_ctl"
PSQL="/Library/PostgreSQL/17/bin/psql"

if ! sudo test -f "$PG_HBA_CONF"; then
    echo "❌ PostgreSQL config not found at $PG_HBA_CONF"
    exit 1
fi

echo "Step 1: Backup current config"
sudo cp "$PG_HBA_CONF" "$PG_HBA_CONF.backup.$(date +%Y%m%d)"
echo "✓ Backup created"
echo ""

echo "Step 2: Update authentication to 'trust' for local connections"
echo "(This allows password-less local access for development)"
echo ""

# Create new pg_hba.conf with trust authentication
sudo tee "$PG_HBA_CONF" > /dev/null << 'EOF'
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             all                                     trust
# IPv4 local connections:
host    all             all             127.0.0.1/32            trust
# IPv6 local connections:
host    all             all             ::1/128                 trust
# Allow replication connections from localhost, by a user with the
# replication privilege.
local   replication     all                                     trust
host    replication     all             127.0.0.1/32            trust
host    replication     all             ::1/128                 trust
EOF

if [ $? -eq 0 ]; then
    echo "✓ Config updated"
    echo ""
    
    echo "Step 3: Restart PostgreSQL"
    sudo -u postgres "$PG_CTL" -D /Library/PostgreSQL/17/data restart
    
    echo ""
    echo "Waiting for PostgreSQL to start..."
    sleep 3
    
    echo ""
    echo "Step 4: Test connection"
    if $PSQL -U postgres -h localhost -c '\l' > /dev/null 2>&1; then
        echo "✅ Connection successful!"
        echo ""
        echo "Step 5: Set new password for postgres user (optional)"
        echo "Run this command if you want to set a password:"
        echo "  /Library/PostgreSQL/17/bin/psql -U postgres -h localhost"
        echo "  Then: ALTER USER postgres PASSWORD 'your_password';"
        echo ""
        echo "Now run: ./quick-setup.sh"
    else
        echo "❌ Connection still failing. Check PostgreSQL logs:"
        echo "   tail -f /Library/PostgreSQL/17/data/log/postgresql-*.log"
    fi
else
    echo "❌ Failed to update config"
    exit 1
fi
