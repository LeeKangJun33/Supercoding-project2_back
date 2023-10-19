package com.example.project02.dto;

import com.example.project02.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private String productName;
    private double productPrice;
    private int count;
    private double totalPrice;
}
