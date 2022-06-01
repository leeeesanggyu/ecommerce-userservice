package com.userservice.service;

import com.userservice.domain.dto.UserDto;
import com.userservice.domain.dto.UserRes;
import com.userservice.domain.entity.UserEntity;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
