package com.kris.wall_e.controller;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String userEndpoint() {
        return "Hello, User!";
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Validated @RequestBody UserDto userDto) {

        return new ResponseEntity<UserResponseDto>(userService.createUser(userDto), HttpStatus.OK);
    }
}
