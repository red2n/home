package net.navinkumar.usermanagement.repository;

import net.navinkumar.usermanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByCode(String code);
    
    List<Department> findByPropertyIdAndIsActiveTrue(String propertyId);
    
    List<Department> findByParentDepartmentIdAndIsActiveTrue(Long parentDepartmentId);
    
    List<Department> findByPropertyIdAndParentDepartmentIdIsNullAndIsActiveTrue(String propertyId);
}