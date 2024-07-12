package com.sparta.outcomebatch.batch.domain;

import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.batch.domain.VideoStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoStatsRepository extends JpaRepository<VideoStats,Long> {
}
