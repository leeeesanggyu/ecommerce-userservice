package com.userservice.controller;

import com.userservice.domain.dto.Greeting;
import com.userservice.domain.dto.UserDto;
import com.userservice.domain.dto.UserReq;
import com.userservice.domain.dto.UserRes;
import com.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

    private final Greeting greeting;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/user-service/health_check")
    public String status(HttpServletRequest request) {
        return "It's working in user-service, port = " + request.getServerPort();
    }

    @GetMapping("/user-service/welcome")
    public String welcome() {
        return greeting.getMessage();
    }

    @PostMapping("/user")
    public ResponseEntity<UserRes> createUser(@Validated @RequestBody UserReq user) {
        final UserDto result = userService.createUser(modelMapper.map(user, UserDto.class));
        final UserRes userRes = modelMapper.map(result, UserRes.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRes);
    }
}
