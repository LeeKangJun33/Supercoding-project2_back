package com.example.project02.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ChangeAmountRequest {

    private Long cartProductId;

    private int amount;
}
