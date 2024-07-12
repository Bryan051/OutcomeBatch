package com.sparta.outcomebatch.batch.domain;

import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.batch.domain.VideoRev;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRevRepository extends JpaRepository<VideoRev,Long> {
}
