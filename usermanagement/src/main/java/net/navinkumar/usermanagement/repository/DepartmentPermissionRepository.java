package net.navinkumar.usermanagement.repository;

import net.navinkumar.usermanagement.entity.DepartmentPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentPermissionRepository extends JpaRepository<DepartmentPermission, Long> {
    
    List<DepartmentPermission> findByDepartmentIdAndIsGrantedTrue(Long departmentId);
    
    Optional<DepartmentPermission> findByDepartmentIdAndResourceAndAction(Long departmentId, String resource, String action);
    
    @Query("SELECT dp FROM DepartmentPermission dp JOIN dp.department d " +
           "WHERE d.code = :departmentCode AND dp.resource = :resource AND dp.action = :action AND dp.isGranted = true")
    Optional<DepartmentPermission> findByDepartmentCodeAndResourceAndAction(@Param("departmentCode") String departmentCode,
                                                                           @Param("resource") String resource,
                                                                           @Param("action") String action);
    
    @Query("SELECT dp FROM DepartmentPermission dp JOIN dp.department d " +
           "WHERE d.code = :departmentCode AND dp.isGranted = true")
    List<DepartmentPermission> findByDepartmentCodeAndIsGrantedTrue(@Param("departmentCode") String departmentCode);
}