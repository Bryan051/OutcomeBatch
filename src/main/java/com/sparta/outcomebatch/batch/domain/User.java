package com.sparta.outcomebatch.batch.domain;

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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_name")
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
