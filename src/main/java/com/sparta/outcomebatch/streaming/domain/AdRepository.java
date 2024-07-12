package com.sparta.outcomebatch.streaming.domain;

import com.sparta.outcomebatch.streaming.domain.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends JpaRepository<Ad,Long> {
}
