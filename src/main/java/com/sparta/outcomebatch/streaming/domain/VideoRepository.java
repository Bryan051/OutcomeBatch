package com.sparta.outcomebatch.streaming.domain;

import com.sparta.outcomebatch.streaming.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {
}
