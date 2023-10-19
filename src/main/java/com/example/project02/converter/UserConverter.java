package com.example.project02.converter;

import com.example.project02.entity.User;
import com.example.project02.dto.UserRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserConverter {
    public User toEntity(UserRequest response) {
        return User.builder()
                .name(response.getName())
                .email(response.getEmail())
                .address(response.getAddress())
                .phone(response.getPhone())
                .password(response.getPassword())
                .status("REGISTERED")
                .gender(response.getGender())
                .img("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")
                .build();
    }


}
