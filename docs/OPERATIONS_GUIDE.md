# Operations Guide - Post-Implementation Support

This guide provides comprehensive operational instructions for the Property Management System (PMS) microservices architecture during the Post-Implementation Support phase.

## Phase Overview
- **Duration**: 3 months (2026-10-01 to 2026-12-31)
- **Key Deliverables**: Stabilization, Optimization, Knowledge transfer

## System Architecture

The PMS consists of the following microservices:
1. **Configuration Service** (Port 8888) - Spring Cloud Config Server
2. **Discovery Service** (Port 8761) - Eureka Server
3. **Gateway Service** (Port 9000) - Spring Cloud Gateway
4. **User Management Service** (Port 8070) - Business service

## Starting the Services

### Startup Order (Critical for proper operation)
1. Configuration Service (8888) - Must start first
2. Discovery Service (8761) - Depends on configuration
3. Gateway Service (9000) - Depends on discovery
4. User Management Service (8070) - Depends on discovery and database

### Starting Commands
```bash
# 1. Configuration Service
cd configuration && mvn spring-boot:run

# 2. Discovery Service (wait for config service to be ready)
cd discovery && mvn spring-boot:run

# 3. Gateway Service (wait for discovery to be ready)
cd gateway && mvn spring-boot:run

# 4. User Management Service (ensure PostgreSQL is running)
cd usermanagement && mvn spring-boot:run
```

## Health Monitoring

### Health Check Endpoints
All services expose comprehensive health checks via Spring Boot Actuator:

| Service | Health Endpoint | Details |
|---------|----------------|---------|
| Configuration | http://localhost:8888/actuator/health | Config server status |
| Discovery | http://localhost:8761/actuator/health | Eureka server status |
| Gateway | http://localhost:9000/actuator/health | Gateway and routing status |
| User Management | http://localhost:8070/actuator/health | Application and database status |

### Additional Monitoring Endpoints
- **Metrics**: `/actuator/metrics` - Comprehensive metrics
- **Prometheus**: `/actuator/prometheus` - Prometheus format metrics
- **Info**: `/actuator/info` - Application information
- **Environment**: `/actuator/env` - Configuration properties
- **Logger Management**: `/actuator/loggers` - Runtime log level management

## Performance Monitoring

### Key Metrics to Monitor
1. **Response Times**: 95th percentile should be < 500ms
2. **Error Rates**: Should be < 1%
3. **Memory Usage**: JVM heap utilization
4. **Database Connections**: Pool utilization
5. **Gateway Throughput**: Requests per second

### Metrics Collection
- All services export metrics in Prometheus format
- Metrics include percentiles (50th, 90th, 95th, 99th)
- Custom tags for application identification

## Log Management

### Log Locations
- **Configuration**: `logs/configuration.log`
- **Discovery**: `logs/discovery.log`
- **Gateway**: `logs/gateway.log`
- **User Management**: `logs/usermanagement.log`

### Log Rotation
- Maximum file size: 10MB
- Retention period: 30 days
- Automatic compression of old logs

### Log Levels
- **Production**: INFO level
- **Troubleshooting**: DEBUG level (can be changed at runtime via actuator)

## Database Operations

### PostgreSQL Configuration
- **Host**: localhost:5432
- **Database**: homebase
- **Connection Pool**: Managed by HikariCP
- **Health Checks**: Automatic database connectivity monitoring

### Database Monitoring
- Connection pool metrics available via `/actuator/metrics`
- Health status includes database connectivity
- JPA/Hibernate metrics for query performance

## Troubleshooting

### Common Issues and Solutions

#### 1. Service Won't Start
**Symptoms**: Service fails to start or exits immediately
**Diagnosis**:
```bash
# Check if required services are running
curl http://localhost:8888/actuator/health  # Config service
curl http://localhost:8761/actuator/health  # Discovery service
```
**Solutions**:
- Ensure services start in correct order
- Check log files for error messages
- Verify database connectivity (for user management)

#### 2. Service Discovery Issues
**Symptoms**: Services cannot find each other
**Diagnosis**:
```bash
# Check Eureka dashboard
http://localhost:8761

# Check service registration
curl http://localhost:8761/actuator/health
```
**Solutions**:
- Restart discovery service
- Check network connectivity
- Verify configuration service is accessible

#### 3. High Response Times
**Symptoms**: Slow API responses
**Diagnosis**:
```bash
# Check metrics
curl http://localhost:9000/actuator/metrics/http.server.requests
curl http://localhost:8070/actuator/metrics/http.server.requests
```
**Solutions**:
- Scale user management service
- Optimize database queries
- Check resource utilization

#### 4. Database Connection Issues
**Symptoms**: User management service health check fails
**Diagnosis**:
```bash
# Check database health
curl http://localhost:8070/actuator/health
```
**Solutions**:
- Verify PostgreSQL is running
- Check connection string in configuration
- Monitor connection pool metrics

## Performance Optimization

### JVM Tuning
Recommended JVM parameters for production:
```bash
-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Database Optimization
- Enable connection pooling (already configured)
- Monitor slow queries via JPA metrics
- Optimize database indices based on query patterns

### Caching Strategy
- Implement Redis for frequently accessed data
- Cache configuration data at service level
- Monitor cache hit rates

## Security Considerations

### Actuator Endpoints
- Health endpoints are exposed for monitoring
- Sensitive endpoints (env, configprops) require authentication in production
- Consider network-level restrictions for management endpoints

### Database Security
- Use environment variables for database credentials
- Implement connection encryption
- Regular security updates for PostgreSQL

## Backup and Recovery

### Configuration Backup
- Configuration files are stored in Git
- Backup configuration data regularly
- Test configuration restore procedures

### Database Backup
- Implement automated PostgreSQL backups
- Test restore procedures regularly
- Monitor backup success/failure

## Scaling Considerations

### Horizontal Scaling
- User Management service can be scaled horizontally
- Gateway service supports load balancing
- Database may require read replicas for high load

### Monitoring Resource Usage
- CPU utilization should be < 70%
- Memory utilization should be < 80%
- Disk space should have 20% free space

## Maintenance Windows

### Recommended Maintenance Schedule
- **Daily**: Health check review
- **Weekly**: Log file review and cleanup
- **Monthly**: Performance metrics analysis
- **Quarterly**: Security updates and patches

### Rolling Updates
1. Update configuration service first
2. Update discovery service
3. Update gateway service
4. Update user management service (can be done with zero downtime)

## Support Contacts

### Escalation Matrix
- **Level 1**: Application errors, performance issues
- **Level 2**: Infrastructure issues, database problems
- **Level 3**: Architecture changes, critical system failures

For immediate support during the Post-Implementation Support phase, contact the development team for stabilization and optimization assistance.