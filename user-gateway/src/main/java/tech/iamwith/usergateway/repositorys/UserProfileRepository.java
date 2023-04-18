package tech.iamwith.usergateway.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.iamwith.usergateway.models.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
