package com.sparta.outcomebatch.streaming.domain;

import com.sparta.outcomebatch.streaming.domain.Ad;
import com.sparta.outcomebatch.streaming.domain.Video;
import com.sparta.outcomebatch.streaming.domain.VideoAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoAdRepository extends JpaRepository<VideoAd, Long> {
    List<VideoAd> findVideoAdByVideo(Video video);

    List<VideoAd> findVideoAdsByVideoAndAd(Video video, Ad ad);
}