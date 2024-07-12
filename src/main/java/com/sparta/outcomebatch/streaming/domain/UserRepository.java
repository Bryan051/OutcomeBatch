package com.sparta.outcomebatch.streaming.domain;


import com.sparta.outcomebatch.streaming.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {//pk의 타입

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserEmail(String userEmail);

}
