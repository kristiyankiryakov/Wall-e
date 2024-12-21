package com.kris.wall_e.service;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;

public interface UserService {

    public UserResponseDto createUser(UserDto userDto);

}
