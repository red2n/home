package com.beyondctc.app.userapi.access;


import com.beyondctc.app.userapi.model.AccessGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/access")
public class AccessController {

    private final AccessService accessService;

    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }

    @GetMapping
    public ResponseEntity<List<AccessGroup>> getUserAccess() {
        return new ResponseEntity<>(accessService.getUserAccess(), HttpStatus.OK);
    }
}
