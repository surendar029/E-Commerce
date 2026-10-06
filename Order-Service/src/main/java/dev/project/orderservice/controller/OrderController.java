package dev.project.orderservice.controller;

import dev.project.orderservice.dto.ProductDetails;
import dev.project.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetails> createOrder(@PathVariable Long id){
        return ResponseEntity.ok(orderService.createOrder(id));
    }
}
