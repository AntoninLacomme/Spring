package com.example.order.controllers;

import com.example.order.domain.MyOrder;
import com.example.order.domain.OrderItem;
import com.example.order.repositories.OrderItemRepository;
import com.example.order.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;

    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @PostMapping("/orders")
    public List<MyOrder> getOrders () {
        return orderRepository.findAll();
    }

    @GetMapping("/order/{id}")
    public Optional<MyOrder> getOrder (@PathVariable Long id) {
        return orderRepository.findById(id);
    }

    @PostMapping("/order/save")
    public ResponseEntity<MyOrder> saveOrder (@RequestBody MyOrder order) {
        orderRepository.save(order);
        return new ResponseEntity<MyOrder>(order, HttpStatus.CREATED);
    }
}
