package com.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRes {

    private String userId;
    private String email;
    private String name;

    private List<OrderRes> orders;
}
