package com.beyondctc.app.userapi.details;

import com.beyondctc.app.userapi.model.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final DetailService detailService;

    public UsersController(DetailService detailService) {
        this.detailService = detailService;
    }

    @GetMapping
    public ResponseEntity<List<Users>> getDefault() {
        return new ResponseEntity<>(detailService.getUsers(), HttpStatus.OK);
    }

}
