package com.userservice.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderRes {

    private String productId;
    private Integer qty;        // 갯수
    private Integer unitPrice;  // 단일 가격
    private Integer totalPrice; // 총 가격
    private Date createAt;

    private String orderId;
}
