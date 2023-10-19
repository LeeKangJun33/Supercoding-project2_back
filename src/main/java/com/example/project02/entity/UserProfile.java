package com.example.project02.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    private Long id;

    private Long aboutMe;

    private String img;

    private String address;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user;
}
