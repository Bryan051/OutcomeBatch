package com.sparta.outcomebatch.streaming.domain;

import com.sparta.outcomebatch.streaming.domain.AdView;
import com.sparta.outcomebatch.streaming.domain.VideoAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AdViewRepository extends JpaRepository<AdView,Long> {

    // 일 광고 뷰 조회
    @Query("SELECT COUNT(a) FROM AdView a WHERE a.videoAd IN :videoAds AND a.createdAt = :date")
    long countByVideoAdsAndDate(@Param("videoAds") List<VideoAd> videoAds, @Param("date") LocalDate date);


}

