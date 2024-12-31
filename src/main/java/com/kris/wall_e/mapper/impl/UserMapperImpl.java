//package com.kris.wall_e.mapper.impl;
//
//import com.kris.wall_e.entity.User;
//import com.kris.wall_e.mapper.UserMapper;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UserMapperImpl implements UserMapper {
//
//    @Override
//    public UserResponseDto fromUser(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        return new UserResponseDto(
//                user.getId(),
//                user.getName(),
//                user.getEmail()
//        );
//    }
//
//}
