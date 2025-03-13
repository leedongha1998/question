package com.bulbas23r.client.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/api/orders")
    public String orders() {
        return "test order";
    }

    @GetMapping("/api/orders/11")
    public String orders11() {
        return "test order 11";
    }
}
