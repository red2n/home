package com.beyondctc.app.userapi.permission;

import com.beyondctc.app.userapi.model.PermissionLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionLookup, Long> {
}
