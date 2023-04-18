package tech.iamwith.usergateway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.iamwith.usergateway.models.Users;
import tech.iamwith.usergateway.repositorys.UserRepository;
import tech.iamwith.usergateway.utils.EmailHashHelper;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
    private final UserRepository userRepository;

    @Autowired
    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> getUsers() {
        for (Users s : userRepository.findAll()) {
            try {
                s.setEmailUrl(EmailHashHelper.hashEmail(s.getEmail()));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return userRepository.findAll();
    }

    public Optional<Users> getById(long id) {
        return userRepository.findById(id);
    }

}