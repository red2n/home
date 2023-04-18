package tech.iamwith.usergateway.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.iamwith.usergateway.models.Users;
import tech.iamwith.usergateway.services.UserServices;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:9000")
public class UsersController {
    private final UserServices userService;

    @Autowired
    public UsersController(UserServices userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Users>> GetUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Users> getById(@RequestParam long id) {
        return new ResponseEntity<>(userService.getById(id).orElse(null), HttpStatus.OK);
    }
}