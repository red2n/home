package tech.iamwith.usergateway.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.iamwith.usergateway.models.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

}
