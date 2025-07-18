# Performance Optimization Configuration

## Post-Implementation Support: Performance Optimization Guide

This configuration provides production-ready optimizations for the Property Management System services.

### JVM Performance Configuration

Create optimized startup scripts for each service:

#### Configuration Service
```bash
#!/bin/bash
# start-configuration.sh
export JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdumps/ \
  -XX:+UseStringDeduplication -XX:+OptimizeStringConcat"

cd configuration && mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

#### Discovery Service  
```bash
#!/bin/bash
# start-discovery.sh
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdumps/ \
  -XX:+UseStringDeduplication"

cd discovery && mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

#### Gateway Service
```bash
#!/bin/bash
# start-gateway.sh
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdumps/ \
  -XX:+UseStringDeduplication -XX:MaxDirectMemorySize=256m"

cd gateway && mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

#### User Management Service
```bash
#!/bin/bash
# start-usermanagement.sh
export JAVA_OPTS="-Xms1024m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdumps/ \
  -XX:+UseStringDeduplication"

cd usermanagement && mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

### Application-Level Optimizations

#### Connection Pool Optimization (User Management)
Add to `usermanagement.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 20000
      leak-detection-threshold: 60000
      
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
    show-sql: false
    hibernate:
      ddl-auto: validate
```

#### Gateway Performance Configuration
Add to `gateway.yml`:

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        pool:
          max-connections: 1000
          max-idle-time: 30s
          max-life-time: 60s
        connect-timeout: 5000
        response-timeout: 30s
      
server:
  netty:
    connection-timeout: 5s
    max-keep-alive-requests: 1000
```

#### Eureka Performance Configuration
Add to `discovery.yml`:

```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
    renewal-threshold-update-interval-ms: 900000
    eviction-interval-timer-in-ms: 60000
    response-cache-auto-expiration-in-seconds: 180
    response-cache-update-interval-ms: 30000
```

### Caching Strategy

#### Application-Level Caching
Create caching configuration for User Management service:

```java
// CacheConfig.java - Add to User Management service
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }
    
    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(Duration.ofMinutes(10))
                .weakKeys()
                .recordStats();
    }
}
```

### Monitoring Performance Optimizations

#### Custom Metrics Configuration
Add custom metrics to monitor performance:

```yaml
# Add to all service configurations
management:
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
        jvm.gc.pause: true
        jvm.memory.used: true
        hikaricp.connections: true
      percentiles:
        http.server.requests: 0.5, 0.75, 0.9, 0.95, 0.99
      sla:
        http.server.requests: 10ms, 50ms, 100ms, 200ms, 500ms, 1s, 2s
    tags:
      service: ${spring.application.name}
      environment: production
```

### Network Optimization

#### Compression Configuration
Add to all services:

```yaml
server:
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml
    min-response-size: 1024
```

### Database Performance Optimization

#### PostgreSQL Configuration Recommendations
Add to PostgreSQL configuration:

```sql
-- postgresql.conf optimizations
shared_buffers = '256MB'
effective_cache_size = '1GB'
maintenance_work_mem = '64MB'
checkpoint_completion_target = 0.9
wal_buffers = '16MB'
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
```

#### Index Strategy
```sql
-- Recommended indexes for user management
CREATE INDEX CONCURRENTLY idx_users_email ON users(email);
CREATE INDEX CONCURRENTLY idx_users_status ON users(status);
CREATE INDEX CONCURRENTLY idx_users_created_at ON users(created_at);
```

### Log Level Optimization

#### Production Log Configuration
```yaml
# Optimized logging for production
logging:
  level:
    root: WARN
    org.springframework: INFO
    com.netflix: WARN
    org.hibernate.SQL: WARN
    org.hibernate.type: WARN
    # Application packages
    net.navinkumar: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  appender:
    console:
      threshold: WARN
```

### Memory Optimization

#### Garbage Collection Tuning
```bash
# G1GC optimization for high-throughput services
export GC_OPTS="-XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1HeapRegionSize=16m \
  -XX:G1ReservePercent=25 \
  -XX:InitiatingHeapOccupancyPercent=30"
```

### Startup Time Optimization

#### Class Data Sharing
```bash
# Generate CDS archive for faster startup
java -Xshare:dump

# Use CDS in startup scripts
export JAVA_OPTS="$JAVA_OPTS -Xshare:on"
```

#### Spring Boot Optimization
Add to `application.yml` for all services:

```yaml
spring:
  main:
    lazy-initialization: true
  jpa:
    defer-datasource-initialization: true
```

### Performance Testing Scripts

#### Load Testing Script
```bash
#!/bin/bash
# load_test.sh - Basic load testing

echo "Performance Load Test - $(date)"
echo "=============================="

# Test Gateway endpoint
echo "Testing Gateway performance..."
ab -n 1000 -c 10 http://localhost:9000/actuator/health

# Test User Management endpoint  
echo "Testing User Management performance..."
ab -n 1000 -c 10 http://localhost:8070/actuator/health

# Test Discovery endpoint
echo "Testing Discovery performance..."
ab -n 500 -c 5 http://localhost:8761/actuator/health

echo "Load test completed"
```

#### Performance Baseline Script
```bash
#!/bin/bash
# performance_baseline.sh - Establish performance baselines

echo "Performance Baseline Measurement - $(date)"
echo "=========================================="

services=("8888:configuration" "8761:discovery" "9000:gateway" "8070:usermanagement")

for service in "${services[@]}"; do
    IFS=':' read -r port name <<< "$service"
    echo "Measuring $name service baseline..."
    
    # Response time measurement
    for i in {1..10}; do
        curl -w "@curl-format.txt" -s -o /dev/null http://localhost:$port/actuator/health
    done | awk '{sum+=$1; count++} END {printf "Average response time: %.2fms\n", sum/count*1000}'
    
    # Memory usage
    curl -s http://localhost:$port/actuator/metrics/jvm.memory.used | \
        jq '.measurements[0].value' | \
        awk '{printf "Memory usage: %.2f MB\n", $1/1024/1024}'
    
    echo "---"
done
```

Create curl format file:
```bash
# curl-format.txt
%{time_total}
```

### Continuous Performance Monitoring

#### Performance Alert Script
```bash
#!/bin/bash
# performance_monitor.sh - Continuous performance monitoring

# Thresholds
RESPONSE_TIME_THRESHOLD=500  # milliseconds
MEMORY_THRESHOLD=80          # percentage
CPU_THRESHOLD=75             # percentage

check_performance() {
    local port=$1
    local service=$2
    
    # Check response time
    response_time=$(curl -w "%{time_total}" -s -o /dev/null http://localhost:$port/actuator/health)
    response_time_ms=$(echo "$response_time * 1000" | bc)
    
    if (( $(echo "$response_time_ms > $RESPONSE_TIME_THRESHOLD" | bc -l) )); then
        echo "ALERT: High response time in $service: ${response_time_ms}ms"
    fi
    
    # Check memory usage
    memory_used=$(curl -s http://localhost:$port/actuator/metrics/jvm.memory.used | jq '.measurements[0].value')
    memory_max=$(curl -s http://localhost:$port/actuator/metrics/jvm.memory.max | jq '.measurements[0].value')
    memory_percent=$(echo "scale=2; $memory_used * 100 / $memory_max" | bc)
    
    if (( $(echo "$memory_percent > $MEMORY_THRESHOLD" | bc -l) )); then
        echo "ALERT: High memory usage in $service: ${memory_percent}%"
    fi
}

# Monitor all services
check_performance "8888" "configuration"
check_performance "8761" "discovery"  
check_performance "9000" "gateway"
check_performance "8070" "usermanagement"
```

This performance optimization configuration provides a comprehensive approach to system optimization during the Post-Implementation Support phase, focusing on JVM tuning, application configuration, caching strategies, and continuous monitoring.