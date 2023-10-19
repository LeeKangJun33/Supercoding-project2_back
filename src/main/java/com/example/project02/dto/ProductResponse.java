package com.example.project02.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long cartProductId;
    private Long productId;
    private String productName;
    private String productImage;
    private double productPrice;
    private double totalPrice;
    private int amount;
    private Integer stockQuantity;

}
