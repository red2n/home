package tech.iamwith.usergateway.services;

import org.springframework.stereotype.Service;
import tech.iamwith.usergateway.models.UserProfile;
import tech.iamwith.usergateway.repositorys.UserProfileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileServices {

    private final UserProfileRepository userProfileRepository;

    public UserProfileServices(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }
    public List<UserProfile> getUserProfile() {
        return userProfileRepository.findAll();
    }


    public Optional<UserProfile> getById(long id) {
        return userProfileRepository.findById(id);
    }
}
