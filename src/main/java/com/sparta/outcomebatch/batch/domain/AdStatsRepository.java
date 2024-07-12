package com.sparta.outcomebatch.batch.domain;

import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.batch.domain.AdStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdStatsRepository extends JpaRepository<AdStats,Long> {
}
