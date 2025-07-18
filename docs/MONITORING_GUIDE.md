# Monitoring and Troubleshooting Guide

## Post-Implementation Support Monitoring Strategy

This document provides comprehensive monitoring and troubleshooting procedures for the Property Management System during the stabilization phase.

## 1. Health Check Monitoring

### Automated Health Checks

Create a monitoring script to check all services:

```bash
#!/bin/bash
# health_check.sh - Automated health monitoring script

services=(
    "configuration:8888"
    "discovery:8761"
    "gateway:9000"
    "usermanagement:8070"
)

echo "PMS Health Check Report - $(date)"
echo "========================================"

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    echo -n "Checking $name service on port $port... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/actuator/health)
    
    if [ "$response" -eq 200 ]; then
        echo "✓ HEALTHY"
    else
        echo "✗ UNHEALTHY (HTTP $response)"
        # Get detailed health information
        curl -s http://localhost:$port/actuator/health | jq .
    fi
done

echo "========================================"
echo "Health check completed at $(date)"
```

### Health Check Automation

Set up automated health checks using cron:

```bash
# Add to crontab (crontab -e)
# Check health every 5 minutes
*/5 * * * * /path/to/health_check.sh >> /var/log/pms-health.log 2>&1

# Daily health summary
0 9 * * * /path/to/daily_health_summary.sh
```

## 2. Performance Monitoring

### Key Performance Indicators (KPIs)

| Metric | Threshold | Alert Level |
|--------|-----------|-------------|
| Response Time (95th percentile) | > 500ms | Warning |
| Response Time (95th percentile) | > 1000ms | Critical |
| Error Rate | > 1% | Warning |
| Error Rate | > 5% | Critical |
| CPU Usage | > 70% | Warning |
| Memory Usage | > 80% | Warning |
| Disk Usage | > 85% | Critical |

### Metrics Collection Script

```bash
#!/bin/bash
# collect_metrics.sh - Performance metrics collection

echo "Performance Metrics Report - $(date)"
echo "====================================="

# Service response times
for port in 8888 8761 9000 8070; do
    service_name=$(curl -s http://localhost:$port/actuator/info | jq -r '.app.name // "unknown"')
    echo "Service: $service_name (Port: $port)"
    
    # Get HTTP request metrics
    curl -s http://localhost:$port/actuator/metrics/http.server.requests | \
        jq '.measurements[] | select(.statistic == "TOTAL_TIME") | .value'
    
    echo "---"
done

# System metrics
echo "System Metrics:"
echo "CPU Usage: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)"
echo "Memory Usage: $(free | grep Mem | awk '{printf "%.2f%%", $3/$2 * 100.0}')"
echo "Disk Usage: $(df -h / | awk 'NR==2{printf "%s", $5}')"
```

## 3. Log Analysis

### Centralized Log Analysis

Create a log aggregation script:

```bash
#!/bin/bash
# log_analyzer.sh - Centralized log analysis

echo "Log Analysis Report - $(date)"
echo "==============================="

log_dirs=(
    "logs/configuration.log"
    "logs/discovery.log"
    "logs/gateway.log"
    "logs/usermanagement.log"
)

# Error analysis
echo "Error Summary (Last 24 hours):"
for log in "${log_dirs[@]}"; do
    if [ -f "$log" ]; then
        service_name=$(basename "$log" .log)
        error_count=$(grep -c "ERROR" "$log" | tail -n 1000)
        warn_count=$(grep -c "WARN" "$log" | tail -n 1000)
        echo "$service_name: $error_count errors, $warn_count warnings"
    fi
done

# Recent critical errors
echo -e "\nRecent Critical Errors:"
for log in "${log_dirs[@]}"; do
    if [ -f "$log" ]; then
        service_name=$(basename "$log" .log)
        echo "--- $service_name ---"
        grep "ERROR" "$log" | tail -n 5
    fi
done
```

### Log Monitoring Alerts

Set up log monitoring for critical errors:

```bash
#!/bin/bash
# log_monitor.sh - Real-time log monitoring

tail -f logs/*.log | while read line; do
    if echo "$line" | grep -q "ERROR\|FATAL"; then
        echo "ALERT: Critical error detected at $(date)"
        echo "$line"
        # Send notification (email, Slack, etc.)
        # echo "$line" | mail -s "PMS Critical Error" admin@company.com
    fi
done
```

## 4. Database Monitoring

### Database Health Check

```bash
#!/bin/bash
# db_health_check.sh - Database monitoring

echo "Database Health Check - $(date)"
echo "==============================="

# Check database connectivity via service health endpoint
echo "Database connectivity through User Management service:"
curl -s http://localhost:8070/actuator/health | jq '.components.db'

# Check connection pool metrics
echo -e "\nConnection Pool Metrics:"
curl -s http://localhost:8070/actuator/metrics | \
    jq '.names[] | select(contains("hikaricp"))'

# Get specific connection pool stats
echo -e "\nActive Connections:"
curl -s http://localhost:8070/actuator/metrics/hikaricp.connections.active | \
    jq '.measurements[0].value'

echo -e "\nIdle Connections:"
curl -s http://localhost:8070/actuator/metrics/hikaricp.connections.idle | \
    jq '.measurements[0].value'
```

## 5. Service Discovery Monitoring

### Eureka Service Registry Check

```bash
#!/bin/bash
# eureka_monitor.sh - Service discovery monitoring

echo "Service Discovery Status - $(date)"
echo "================================="

# Get registered services
echo "Registered Services:"
curl -s http://localhost:8761/eureka/apps | \
    grep -o '<name>[^<]*</name>' | \
    sed 's/<name>\(.*\)<\/name>/\1/' | \
    sort | uniq

# Check service instances
echo -e "\nService Instance Details:"
curl -s http://localhost:8761/eureka/apps -H "Accept: application/json" | \
    jq '.applications.application[].instance[] | {app: .app, status: .status, hostName: .hostName, port: .port.@port}'
```

## 6. Gateway Monitoring

### Gateway Route Health

```bash
#!/bin/bash
# gateway_monitor.sh - Gateway-specific monitoring

echo "Gateway Monitoring Report - $(date)"
echo "==================================="

# Check gateway routes
echo "Configured Routes:"
curl -s http://localhost:9000/actuator/gateway/routes | jq '.[] | {id: .route_id, uri: .uri, predicates: .predicates}'

# Check gateway metrics
echo -e "\nGateway Request Metrics:"
curl -s http://localhost:9000/actuator/metrics/gateway.requests | \
    jq '.measurements[] | {statistic: .statistic, value: .value}'

# Test route availability
echo -e "\nRoute Health Test:"
curl -s -o /dev/null -w "Test route status: %{http_code}\n" http://localhost:9000/uuid
```

## 7. Troubleshooting Procedures

### Service Startup Issues

```bash
#!/bin/bash
# startup_troubleshoot.sh - Service startup troubleshooting

echo "Service Startup Troubleshooting - $(date)"
echo "========================================="

services=("configuration:8888" "discovery:8761" "gateway:9000" "usermanagement:8070")

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    echo "Troubleshooting $name service..."
    
    # Check if port is in use
    if netstat -tulpn | grep -q ":$port "; then
        echo "✓ Port $port is in use"
        
        # Check if service responds
        if curl -s http://localhost:$port/actuator/health > /dev/null; then
            echo "✓ Service is responding to health checks"
        else
            echo "✗ Service is not responding to health checks"
            echo "  Recommended action: Check service logs"
        fi
    else
        echo "✗ Port $port is not in use"
        echo "  Recommended action: Start the $name service"
    fi
    echo "---"
done
```

### Performance Troubleshooting

```bash
#!/bin/bash
# performance_troubleshoot.sh - Performance issue diagnosis

echo "Performance Troubleshooting - $(date)"
echo "====================================="

# Check system resources
echo "System Resource Usage:"
echo "CPU: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)"
echo "Memory: $(free | grep Mem | awk '{printf "%.2f%%", $3/$2 * 100.0}')"
echo "Load Average: $(uptime | awk -F'load average:' '{print $2}')"

# Check service response times
echo -e "\nService Response Time Analysis:"
for port in 8888 8761 9000 8070; do
    echo "Port $port:"
    time curl -s http://localhost:$port/actuator/health > /dev/null
done

# Check for memory leaks
echo -e "\nJVM Memory Analysis:"
for port in 8888 8761 9000 8070; do
    echo "Port $port JVM metrics:"
    curl -s http://localhost:$port/actuator/metrics/jvm.memory.used | \
        jq '.measurements[0].value' 2>/dev/null || echo "N/A"
done
```

## 8. Automated Alerts

### Alert Configuration

Create alert thresholds:

```bash
#!/bin/bash
# alert_monitor.sh - Automated alerting system

# Configuration
ERROR_THRESHOLD=5
RESPONSE_TIME_THRESHOLD=1000
CPU_THRESHOLD=80
MEMORY_THRESHOLD=85

check_errors() {
    local service=$1
    local log_file="logs/${service}.log"
    local recent_errors=$(grep "ERROR" "$log_file" | tail -n 100 | wc -l)
    
    if [ "$recent_errors" -gt "$ERROR_THRESHOLD" ]; then
        send_alert "High error rate in $service: $recent_errors errors"
    fi
}

check_performance() {
    local port=$1
    local service_name=$2
    
    # Simplified response time check
    local start_time=$(date +%s%N)
    curl -s http://localhost:$port/actuator/health > /dev/null
    local end_time=$(date +%s%N)
    local response_time=$(( (end_time - start_time) / 1000000 ))
    
    if [ "$response_time" -gt "$RESPONSE_TIME_THRESHOLD" ]; then
        send_alert "High response time in $service_name: ${response_time}ms"
    fi
}

send_alert() {
    local message=$1
    echo "ALERT: $message" | tee -a alerts.log
    # Add notification integrations here (email, Slack, PagerDuty, etc.)
}

# Run checks
check_errors "configuration"
check_errors "discovery"
check_errors "gateway"
check_errors "usermanagement"

check_performance "8888" "configuration"
check_performance "8761" "discovery"
check_performance "9000" "gateway"
check_performance "8070" "usermanagement"
```

## 9. Maintenance Scripts

### Weekly Maintenance

```bash
#!/bin/bash
# weekly_maintenance.sh - Weekly maintenance tasks

echo "Weekly Maintenance Report - $(date)"
echo "=================================="

# Log rotation check
echo "Log file sizes:"
du -sh logs/*.log

# Clean old logs (older than 30 days)
find logs/ -name "*.log.*" -mtime +30 -delete

# Performance summary
echo -e "\nWeekly Performance Summary:"
echo "Average response times, error rates, and resource usage over the past week"

# Health check summary
echo -e "\nHealth Check Summary:"
echo "Service availability percentage over the past week"

# Recommendations
echo -e "\nRecommendations:"
echo "- Review high error rate periods"
echo "- Consider scaling if response times are consistently high"
echo "- Update dependencies if security patches are available"
```

## 10. Knowledge Transfer Checklist

### Operations Team Handover

- [ ] All monitoring scripts installed and tested
- [ ] Alert thresholds configured and validated
- [ ] Log rotation and cleanup procedures documented
- [ ] Troubleshooting procedures tested
- [ ] Emergency contact information updated
- [ ] Service dependencies documented
- [ ] Performance baselines established
- [ ] Backup and recovery procedures verified

### Documentation Requirements

- [ ] Operations runbook completed
- [ ] Monitoring guide documented
- [ ] Troubleshooting procedures validated
- [ ] Performance optimization guide created
- [ ] Security considerations documented
- [ ] Scaling procedures outlined

This comprehensive monitoring and troubleshooting guide ensures effective stabilization and optimization during the Post-Implementation Support phase.