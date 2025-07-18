package net.navinkumar.usermanagement.repository;

import net.navinkumar.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByPropertyIdAndIsActiveTrue(String propertyId);
    
    List<User> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    @Query("SELECT u FROM User u JOIN u.department d WHERE d.code = :departmentCode AND u.isActive = true")
    List<User> findByDepartmentCodeAndIsActiveTrue(@Param("departmentCode") String departmentCode);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    List<User> findByRoleNameAndIsActiveTrue(@Param("roleName") String roleName);
}