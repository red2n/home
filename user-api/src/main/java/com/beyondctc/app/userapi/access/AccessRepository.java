package com.beyondctc.app.userapi.access;

import com.beyondctc.app.userapi.model.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRepository extends JpaRepository<AccessGroup, Long> {
}
