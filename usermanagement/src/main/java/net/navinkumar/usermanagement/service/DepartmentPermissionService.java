package net.navinkumar.usermanagement.service;

import net.navinkumar.usermanagement.entity.Department;
import net.navinkumar.usermanagement.entity.DepartmentPermission;
import net.navinkumar.usermanagement.entity.User;
import net.navinkumar.usermanagement.repository.DepartmentRepository;
import net.navinkumar.usermanagement.repository.DepartmentPermissionRepository;
import net.navinkumar.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentPermissionService {
    
    private final DepartmentRepository departmentRepository;
    private final DepartmentPermissionRepository departmentPermissionRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public boolean hasPermission(String username, String resource, String action) {
        log.debug("Checking permission for user: {}, resource: {}, action: {}", username, resource, action);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", username);
            return false;
        }
        
        User user = userOpt.get();
        Department department = user.getDepartment();
        
        if (department == null) {
            log.warn("User {} has no department assigned", username);
            return false;
        }
        
        Optional<DepartmentPermission> permission = departmentPermissionRepository
            .findByDepartmentIdAndResourceAndAction(department.getId(), resource, action);
        
        boolean hasPermission = permission.isPresent() && permission.get().getIsGranted();
        log.debug("Permission result for user {}: {}", username, hasPermission);
        
        return hasPermission;
    }
    
    @Transactional(readOnly = true)
    public boolean hasOfferPermission(String username, String offerCode, Double discountAmount) {
        log.debug("Checking offer permission for user: {}, offer: {}, discount: {}", username, offerCode, discountAmount);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        Department department = user.getDepartment();
        
        if (department == null) {
            return false;
        }
        
        Optional<DepartmentPermission> permission = departmentPermissionRepository
            .findByDepartmentIdAndResourceAndAction(department.getId(), "OFFER", "APPLY");
        
        if (permission.isEmpty() || !permission.get().getIsGranted()) {
            return false;
        }
        
        DepartmentPermission perm = permission.get();
        
        // Check discount limits
        if (perm.getMaxDiscountAmount() != null && discountAmount > perm.getMaxDiscountAmount()) {
            log.debug("Discount amount {} exceeds maximum allowed {} for department {}", 
                     discountAmount, perm.getMaxDiscountAmount(), department.getCode());
            return false;
        }
        
        return true;
    }
    
    @Transactional(readOnly = true)
    public boolean requiresApproval(String departmentCode, String resource, String action, Double amount) {
        Optional<DepartmentPermission> permission = departmentPermissionRepository
            .findByDepartmentCodeAndResourceAndAction(departmentCode, resource, action);
        
        if (permission.isEmpty()) {
            return true; // Default to requiring approval if no permission found
        }
        
        DepartmentPermission perm = permission.get();
        
        if (Boolean.TRUE.equals(perm.getRequiresApproval())) {
            return true;
        }
        
        // Check if amount exceeds approval threshold
        if (perm.getApprovalThreshold() != null && amount != null && amount > perm.getApprovalThreshold()) {
            return true;
        }
        
        return false;
    }
    
    @Transactional(readOnly = true)
    public List<Department> getDepartmentsForProperty(String propertyId) {
        return departmentRepository.findByPropertyIdAndIsActiveTrue(propertyId);
    }
    
    @Transactional(readOnly = true)
    public List<DepartmentPermission> getDepartmentPermissions(String departmentCode) {
        return departmentPermissionRepository.findByDepartmentCodeAndIsGrantedTrue(departmentCode);
    }
    
    public Department createDepartment(Department department, String userId) {
        log.info("Creating department: {}", department.getCode());
        
        if (departmentRepository.findByCode(department.getCode()).isPresent()) {
            throw new IllegalArgumentException("Department with code " + department.getCode() + " already exists");
        }
        
        department.setCreatedBy(userId);
        department.setUpdatedBy(userId);
        
        return departmentRepository.save(department);
    }
    
    public DepartmentPermission grantPermission(Long departmentId, String resource, String action, 
                                               Double maxDiscountAmount, Double approvalThreshold, Boolean requiresApproval) {
        log.info("Granting permission to department {}: {} - {}", departmentId, resource, action);
        
        Optional<Department> departmentOpt = departmentRepository.findById(departmentId);
        if (departmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Department not found with id: " + departmentId);
        }
        
        DepartmentPermission permission = new DepartmentPermission();
        permission.setDepartment(departmentOpt.get());
        permission.setResource(resource);
        permission.setAction(action);
        permission.setMaxDiscountAmount(maxDiscountAmount);
        permission.setApprovalThreshold(approvalThreshold);
        permission.setRequiresApproval(requiresApproval);
        permission.setIsGranted(true);
        
        return departmentPermissionRepository.save(permission);
    }
    
    public void revokePermission(Long departmentId, String resource, String action) {
        log.info("Revoking permission from department {}: {} - {}", departmentId, resource, action);
        
        Optional<DepartmentPermission> permission = departmentPermissionRepository
            .findByDepartmentIdAndResourceAndAction(departmentId, resource, action);
        
        if (permission.isPresent()) {
            permission.get().setIsGranted(false);
            departmentPermissionRepository.save(permission.get());
        }
    }
}