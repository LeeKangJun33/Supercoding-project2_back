package com.example.project02.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {

    private Long userId;

    @Schema(description = "장바구니에 들어있는 상품 총 개수")
    private int totalCount;

    @Schema(description = "장바구니에 들어있는 상품 총 가격")
    private double totalPrice;

    @Schema(description = "장바구니 세부 사항")
    private List<ProductResponse> productList;
}
