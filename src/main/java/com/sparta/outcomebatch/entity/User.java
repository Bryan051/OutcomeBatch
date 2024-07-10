package com.sparta.outcomebatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userEmail;

    private String userName;

    private String password;

    private boolean authority;
    private boolean role;

    public User(String userEmail, String userName, String password, boolean authority, boolean role) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.password = password;
        this.authority = authority;
        this.role = role;
    }
}
