package com.example.project02.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class UserInfoResponse {

    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private List<OrderResponse> orderList;

}
