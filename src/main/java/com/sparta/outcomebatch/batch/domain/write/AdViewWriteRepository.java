package com.sparta.outcomebatch.batch.domain.write;

import com.sparta.outcomebatch.batch.domain.AdView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
@Repository
@Transactional
public interface AdViewWriteRepository extends JpaRepository<AdView,Long> {



}

