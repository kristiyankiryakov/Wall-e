package com.kris.wall_e.service;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;

public interface UserService {

    public UserResponseDto createUser(UserDto userDto);

    public User getUser(Long id);

}
