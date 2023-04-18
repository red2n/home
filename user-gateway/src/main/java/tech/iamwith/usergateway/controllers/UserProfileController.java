package tech.iamwith.usergateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.iamwith.usergateway.models.UserProfile;
import tech.iamwith.usergateway.services.UserProfileServices;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-profile")
public class UserProfileController {

    private  final UserProfileServices userProfileServices;

    public UserProfileController(UserProfileServices userProfileServices) {
        this.userProfileServices = userProfileServices;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserProfile>> GetUserProfile() {
        return new ResponseEntity<>(userProfileServices.getUserProfile(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserProfile> GetUserProfileById(@RequestParam long id) {
        return new ResponseEntity<>(userProfileServices.getById(id).orElse(null), HttpStatus.OK);
    }
}