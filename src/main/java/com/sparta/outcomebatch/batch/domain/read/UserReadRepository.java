package com.sparta.outcomebatch.batch.domain.read;


import com.sparta.outcomebatch.batch.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserReadRepository extends JpaRepository<User, Long> {//pk의 타입

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserEmail(String userEmail);

}
