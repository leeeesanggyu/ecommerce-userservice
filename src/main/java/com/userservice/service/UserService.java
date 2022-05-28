package com.userservice.service;

import com.userservice.domain.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);
}
