package com.kris.wall_e.controller;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserDto userDto) {

        return new ResponseEntity<UserResponseDto>(userService.createUser(userDto), HttpStatus.OK);
    }
}
