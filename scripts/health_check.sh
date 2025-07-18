#!/bin/bash
# health_check.sh - Automated health monitoring script for Post-Implementation Support

echo "======================================================"
echo "PMS Health Check Report - $(date)"
echo "Post-Implementation Support Phase Monitoring"
echo "======================================================"

services=(
    "configuration:8888"
    "discovery:8761"
    "gateway:9000"
    "usermanagement:8070"
)

overall_status="HEALTHY"

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    echo -n "Checking $name service on port $port... "
    
    # Check if port is listening
    if ! nc -z localhost "$port" 2>/dev/null; then
        echo "✗ SERVICE DOWN (Port not listening)"
        overall_status="UNHEALTHY"
        continue
    fi
    
    # Check health endpoint
    response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port/actuator/health" 2>/dev/null)
    
    if [ "$response" -eq 200 ]; then
        echo "✓ HEALTHY"
        
        # Get additional health details for monitoring
        health_details=$(curl -s "http://localhost:$port/actuator/health" 2>/dev/null)
        
        # Check if there are any DOWN components
        if echo "$health_details" | grep -q '"status":"DOWN"'; then
            echo "  ⚠ WARNING: Some components are DOWN"
            echo "$health_details" | jq '.components // empty' 2>/dev/null || echo "  Unable to parse health details"
            overall_status="DEGRADED"
        fi
        
        # Quick metrics check
        if curl -s "http://localhost:$port/actuator/metrics" >/dev/null 2>&1; then
            echo "  ✓ Metrics endpoint available"
        else
            echo "  ⚠ Metrics endpoint unavailable"
        fi
        
    else
        echo "✗ UNHEALTHY (HTTP $response)"
        overall_status="UNHEALTHY"
        
        # Try to get error details
        curl -s "http://localhost:$port/actuator/health" 2>/dev/null || echo "  Unable to retrieve health details"
    fi
    echo
done

echo "======================================================"
echo "Overall System Status: $overall_status"
echo "Health check completed at $(date)"
echo "======================================================"

# Exit with appropriate code for automation
case $overall_status in
    "HEALTHY")
        exit 0
        ;;
    "DEGRADED")
        exit 1
        ;;
    "UNHEALTHY")
        exit 2
        ;;
esac