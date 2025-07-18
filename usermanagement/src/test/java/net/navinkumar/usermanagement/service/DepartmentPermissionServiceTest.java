package net.navinkumar.usermanagement.service;

import net.navinkumar.usermanagement.entity.Department;
import net.navinkumar.usermanagement.entity.DepartmentPermission;
import net.navinkumar.usermanagement.entity.User;
import net.navinkumar.usermanagement.repository.DepartmentRepository;
import net.navinkumar.usermanagement.repository.DepartmentPermissionRepository;
import net.navinkumar.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentPermissionServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentPermissionRepository departmentPermissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DepartmentPermissionService departmentPermissionService;

    private User user;
    private Department department;
    private DepartmentPermission permission;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setCode("SALES");
        department.setName("Sales Department");
        department.setPropertyId("PROP001");
        department.setIsActive(true);

        user = new User();
        user.setId(1L);
        user.setUsername("sales_user");
        user.setEmail("sales@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setDepartment(department);
        user.setIsActive(true);

        permission = new DepartmentPermission();
        permission.setId(1L);
        permission.setDepartment(department);
        permission.setResource("OFFER");
        permission.setAction("APPLY");
        permission.setMaxDiscountAmount(1000.0);
        permission.setApprovalThreshold(500.0);
        permission.setRequiresApproval(false);
        permission.setIsGranted(true);
    }

    @Test
    void testHasPermission_UserHasPermission() {
        // Given
        when(userRepository.findByUsername("sales_user")).thenReturn(Optional.of(user));
        when(departmentPermissionRepository.findByDepartmentIdAndResourceAndAction(1L, "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.hasPermission("sales_user", "OFFER", "APPLY");

        // Then
        assertTrue(result);
        verify(userRepository).findByUsername("sales_user");
        verify(departmentPermissionRepository).findByDepartmentIdAndResourceAndAction(1L, "OFFER", "APPLY");
    }

    @Test
    void testHasPermission_UserNotFound() {
        // Given
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        // When
        boolean result = departmentPermissionService.hasPermission("unknown_user", "OFFER", "APPLY");

        // Then
        assertFalse(result);
        verify(userRepository).findByUsername("unknown_user");
        verify(departmentPermissionRepository, never()).findByDepartmentIdAndResourceAndAction(anyLong(), anyString(), anyString());
    }

    @Test
    void testHasPermission_PermissionNotGranted() {
        // Given
        permission.setIsGranted(false);
        when(userRepository.findByUsername("sales_user")).thenReturn(Optional.of(user));
        when(departmentPermissionRepository.findByDepartmentIdAndResourceAndAction(1L, "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.hasPermission("sales_user", "OFFER", "APPLY");

        // Then
        assertFalse(result);
    }

    @Test
    void testHasOfferPermission_WithinDiscountLimit() {
        // Given
        when(userRepository.findByUsername("sales_user")).thenReturn(Optional.of(user));
        when(departmentPermissionRepository.findByDepartmentIdAndResourceAndAction(1L, "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.hasOfferPermission("sales_user", "WEEKEND20", 800.0);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasOfferPermission_ExceedsDiscountLimit() {
        // Given
        when(userRepository.findByUsername("sales_user")).thenReturn(Optional.of(user));
        when(departmentPermissionRepository.findByDepartmentIdAndResourceAndAction(1L, "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.hasOfferPermission("sales_user", "WEEKEND20", 1500.0);

        // Then
        assertFalse(result);
    }

    @Test
    void testRequiresApproval_ExceedsThreshold() {
        // Given
        when(departmentPermissionRepository.findByDepartmentCodeAndResourceAndAction("SALES", "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.requiresApproval("SALES", "OFFER", "APPLY", 600.0);

        // Then
        assertTrue(result); // Amount exceeds approval threshold of 500
    }

    @Test
    void testRequiresApproval_WithinThreshold() {
        // Given
        when(departmentPermissionRepository.findByDepartmentCodeAndResourceAndAction("SALES", "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.requiresApproval("SALES", "OFFER", "APPLY", 400.0);

        // Then
        assertFalse(result); // Amount is within approval threshold
    }

    @Test
    void testRequiresApproval_AlwaysRequiresApproval() {
        // Given
        permission.setRequiresApproval(true);
        when(departmentPermissionRepository.findByDepartmentCodeAndResourceAndAction("SALES", "OFFER", "APPLY"))
            .thenReturn(Optional.of(permission));

        // When
        boolean result = departmentPermissionService.requiresApproval("SALES", "OFFER", "APPLY", 100.0);

        // Then
        assertTrue(result); // Always requires approval
    }

    @Test
    void testCreateDepartment_Success() {
        // Given
        Department newDepartment = new Department();
        newDepartment.setCode("MARKETING");
        newDepartment.setName("Marketing Department");
        newDepartment.setPropertyId("PROP001");

        when(departmentRepository.findByCode("MARKETING")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);

        // When
        Department result = departmentPermissionService.createDepartment(newDepartment, "admin");

        // Then
        assertNotNull(result);
        assertEquals("MARKETING", result.getCode());
        verify(departmentRepository).findByCode("MARKETING");
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void testCreateDepartment_DuplicateCode() {
        // Given
        Department newDepartment = new Department();
        newDepartment.setCode("SALES");
        newDepartment.setName("Sales Department");

        when(departmentRepository.findByCode("SALES")).thenReturn(Optional.of(department));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> departmentPermissionService.createDepartment(newDepartment, "admin"));

        assertEquals("Department with code SALES already exists", exception.getMessage());
        verify(departmentRepository).findByCode("SALES");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void testGetDepartmentsForProperty() {
        // Given
        when(departmentRepository.findByPropertyIdAndIsActiveTrue("PROP001"))
            .thenReturn(Arrays.asList(department));

        // When
        var result = departmentPermissionService.getDepartmentsForProperty("PROP001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SALES", result.get(0).getCode());
    }

    @Test
    void testGrantPermission_Success() {
        // Given
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentPermissionRepository.save(any(DepartmentPermission.class))).thenReturn(permission);

        // When
        DepartmentPermission result = departmentPermissionService.grantPermission(
            1L, "OFFER", "APPLY", 1000.0, 500.0, false);

        // Then
        assertNotNull(result);
        assertEquals("OFFER", result.getResource());
        assertEquals("APPLY", result.getAction());
        assertEquals(1000.0, result.getMaxDiscountAmount());
        assertEquals(500.0, result.getApprovalThreshold());
        assertFalse(result.getRequiresApproval());
        assertTrue(result.getIsGranted());
        verify(departmentRepository).findById(1L);
        verify(departmentPermissionRepository).save(any(DepartmentPermission.class));
    }

    @Test
    void testGrantPermission_DepartmentNotFound() {
        // Given
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> departmentPermissionService.grantPermission(999L, "OFFER", "APPLY", 1000.0, 500.0, false));

        assertEquals("Department not found with id: 999", exception.getMessage());
        verify(departmentRepository).findById(999L);
        verify(departmentPermissionRepository, never()).save(any(DepartmentPermission.class));
    }
}