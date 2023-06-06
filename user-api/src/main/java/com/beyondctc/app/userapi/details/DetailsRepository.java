package com.beyondctc.app.userapi.details;
import com.beyondctc.app.userapi.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailsRepository extends JpaRepository<Users, Long> {
}
