package com.kris.wall_e.mapper;

import com.kris.wall_e.dto.UserDto;
import com.kris.wall_e.dto.UserResponseDto;
import com.kris.wall_e.entity.User;

public interface UserMapper {

    public UserResponseDto fromUser(User user);

}
