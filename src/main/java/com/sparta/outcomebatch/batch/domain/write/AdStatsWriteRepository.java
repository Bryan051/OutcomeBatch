package com.sparta.outcomebatch.batch.domain.write;

import com.sparta.outcomebatch.batch.domain.AdStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AdStatsWriteRepository extends JpaRepository<AdStats,Long> {
}
