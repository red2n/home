package net.navinkumar.usermanagement.controller;

import net.navinkumar.usermanagement.entity.Department;
import net.navinkumar.usermanagement.entity.DepartmentPermission;
import net.navinkumar.usermanagement.service.DepartmentPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user-management")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {
    
    private final DepartmentPermissionService departmentPermissionService;
    
    @GetMapping("/departments/property/{propertyId}")
    public ResponseEntity<List<Department>> getDepartmentsForProperty(@PathVariable String propertyId) {
        log.info("Request to get departments for property: {}", propertyId);
        
        List<Department> departments = departmentPermissionService.getDepartmentsForProperty(propertyId);
        return ResponseEntity.ok(departments);
    }
    
    @GetMapping("/departments/{departmentCode}/permissions")
    public ResponseEntity<List<DepartmentPermission>> getDepartmentPermissions(@PathVariable String departmentCode) {
        log.info("Request to get permissions for department: {}", departmentCode);
        
        List<DepartmentPermission> permissions = departmentPermissionService.getDepartmentPermissions(departmentCode);
        return ResponseEntity.ok(permissions);
    }
    
    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(
            @RequestBody Department department,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request to create department: {}", department.getCode());
        
        try {
            Department createdDepartment = departmentPermissionService.createDepartment(department, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (IllegalArgumentException e) {
            log.error("Error creating department: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/permissions/check")
    public ResponseEntity<Map<String, Boolean>> checkPermission(
            @RequestParam String username,
            @RequestParam String resource,
            @RequestParam String action) {
        
        log.info("Permission check for user: {}, resource: {}, action: {}", username, resource, action);
        
        boolean hasPermission = departmentPermissionService.hasPermission(username, resource, action);
        return ResponseEntity.ok(Map.of("hasPermission", hasPermission));
    }
    
    @PostMapping("/permissions/offer-check")
    public ResponseEntity<Map<String, Object>> checkOfferPermission(
            @RequestParam String username,
            @RequestParam String offerCode,
            @RequestParam Double discountAmount) {
        
        log.info("Offer permission check for user: {}, offer: {}, discount: {}", username, offerCode, discountAmount);
        
        boolean hasPermission = departmentPermissionService.hasOfferPermission(username, offerCode, discountAmount);
        return ResponseEntity.ok(Map.of(
            "hasPermission", hasPermission,
            "username", username,
            "offerCode", offerCode,
            "discountAmount", discountAmount
        ));
    }
    
    @PostMapping("/permissions/approval-check")
    public ResponseEntity<Map<String, Boolean>> checkApprovalRequired(
            @RequestParam String departmentCode,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam(required = false) Double amount) {
        
        log.info("Approval check for department: {}, resource: {}, action: {}, amount: {}", 
                departmentCode, resource, action, amount);
        
        boolean requiresApproval = departmentPermissionService.requiresApproval(departmentCode, resource, action, amount);
        return ResponseEntity.ok(Map.of("requiresApproval", requiresApproval));
    }
    
    @PostMapping("/departments/{departmentId}/permissions")
    public ResponseEntity<DepartmentPermission> grantPermission(
            @PathVariable Long departmentId,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam(required = false) Double maxDiscountAmount,
            @RequestParam(required = false) Double approvalThreshold,
            @RequestParam(required = false, defaultValue = "false") Boolean requiresApproval) {
        
        log.info("Granting permission to department {}: {} - {}", departmentId, resource, action);
        
        try {
            DepartmentPermission permission = departmentPermissionService.grantPermission(
                departmentId, resource, action, maxDiscountAmount, approvalThreshold, requiresApproval);
            return ResponseEntity.status(HttpStatus.CREATED).body(permission);
        } catch (IllegalArgumentException e) {
            log.error("Error granting permission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/departments/{departmentId}/permissions")
    public ResponseEntity<Void> revokePermission(
            @PathVariable Long departmentId,
            @RequestParam String resource,
            @RequestParam String action) {
        
        log.info("Revoking permission from department {}: {} - {}", departmentId, resource, action);
        
        departmentPermissionService.revokePermission(departmentId, resource, action);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Management Service is running");
    }
}