package com.userservice.domain.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserReq {

    @NotNull(message = "Email can't be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Password can't be null")
    @Size(min = 4, max = 16, message = "Password not be 4 ~ 16 characters")
    private String pwd;

    @NotNull(message = "name can't be null")
    @Size(min = 2, message = "name not be less than two characters")
    private String name;
}
