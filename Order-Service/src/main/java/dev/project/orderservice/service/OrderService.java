package dev.project.orderservice.service;

import dev.project.orderservice.client.InventoryClient;
import dev.project.orderservice.client.ProductClient;
import dev.project.orderservice.dto.InventoryDetails;
import dev.project.orderservice.dto.ProductDetails;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderService(ProductClient productClient, InventoryClient inventoryClient) {
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
    }

    public ProductDetails createOrder(Long id){

    }
}
