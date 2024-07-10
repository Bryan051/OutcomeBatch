package com.sparta.outcomebatch.repository;

import com.sparta.outcomebatch.entity.Ad;
import com.sparta.outcomebatch.entity.Video;
import com.sparta.outcomebatch.entity.VideoAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoAdRepository extends JpaRepository<VideoAd, Long> {
    List<VideoAd> findVideoAdByVideo(Video video);

    List<VideoAd> findVideoAdsByVideoAndAd(Video video, Ad ad);
}