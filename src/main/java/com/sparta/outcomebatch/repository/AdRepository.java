package com.sparta.outcomebatch.repository;

import com.sparta.outcomebatch.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends JpaRepository<Ad,Long> {
}
