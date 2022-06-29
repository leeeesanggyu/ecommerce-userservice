package com.userservice.client;

import com.userservice.domain.dto.OrderRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/order")
    List<OrderRes> getOrders(@PathVariable String userId);
}
