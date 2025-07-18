#!/bin/bash
# startup_order.sh - Proper service startup sequence for Post-Implementation Support

echo "======================================================"
echo "PMS Service Startup Sequence"
echo "Post-Implementation Support Phase"
echo "======================================================"

# Create logs directory if it doesn't exist
mkdir -p logs

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    echo "Waiting for $service_name to be ready on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "http://localhost:$port/actuator/health" >/dev/null 2>&1; then
            echo "✓ $service_name is ready"
            return 0
        fi
        
        echo "  Attempt $attempt/$max_attempts - waiting..."
        sleep 5
        ((attempt++))
    done
    
    echo "✗ $service_name failed to start within expected time"
    return 1
}

# Function to start service
start_service() {
    local service_name=$1
    local port=$2
    local directory=$3
    
    echo "Starting $service_name..."
    
    # Check if port is already in use
    if netstat -tulpn 2>/dev/null | grep -q ":$port "; then
        echo "⚠ Port $port is already in use. Checking if service is healthy..."
        if curl -s "http://localhost:$port/actuator/health" >/dev/null 2>&1; then
            echo "✓ $service_name is already running and healthy"
            return 0
        else
            echo "✗ Port $port is in use but service is not responding"
            return 1
        fi
    fi
    
    # Start the service in background
    cd "$directory" || { echo "✗ Directory $directory not found"; return 1; }
    nohup mvn spring-boot:run > "../logs/${service_name}_startup.log" 2>&1 &
    cd - >/dev/null || return 1
    
    # Wait for service to be ready
    wait_for_service "$service_name" "$port"
}

echo "Starting services in proper order..."
echo

# 1. Configuration Service (must start first)
echo "Step 1: Starting Configuration Service"
start_service "configuration" "8888" "configuration"
if [ $? -ne 0 ]; then
    echo "✗ Failed to start Configuration Service. Aborting startup sequence."
    exit 1
fi
echo

# 2. Discovery Service (depends on configuration)
echo "Step 2: Starting Discovery Service"
start_service "discovery" "8761" "discovery"
if [ $? -ne 0 ]; then
    echo "✗ Failed to start Discovery Service. Aborting startup sequence."
    exit 1
fi
echo

# 3. Gateway Service (depends on discovery)
echo "Step 3: Starting Gateway Service"
start_service "gateway" "9000" "gateway"
if [ $? -ne 0 ]; then
    echo "✗ Failed to start Gateway Service. Aborting startup sequence."
    exit 1
fi
echo

# 4. User Management Service (depends on discovery and database)
echo "Step 4: Starting User Management Service"
echo "Note: Ensure PostgreSQL is running before starting this service"
start_service "usermanagement" "8070" "usermanagement"
if [ $? -ne 0 ]; then
    echo "✗ Failed to start User Management Service."
    echo "Check if PostgreSQL is running and accessible"
    exit 1
fi
echo

echo "======================================================"
echo "All services started successfully!"
echo "Running final health check..."
echo "======================================================"

# Run health check
./scripts/health_check.sh

echo
echo "Service URLs:"
echo "- Configuration Service: http://localhost:8888/actuator/health"
echo "- Discovery Service: http://localhost:8761"
echo "- Gateway Service: http://localhost:9000/actuator/health"
echo "- User Management Service: http://localhost:8070/actuator/health"
echo
echo "Startup sequence completed at $(date)"