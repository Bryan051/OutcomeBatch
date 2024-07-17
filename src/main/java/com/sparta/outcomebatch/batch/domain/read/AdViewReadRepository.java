package com.sparta.outcomebatch.batch.domain.read;

import com.sparta.outcomebatch.batch.domain.AdView;
import com.sparta.outcomebatch.batch.domain.VideoAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AdViewReadRepository extends JpaRepository<AdView,Long> {

    // 일 광고 뷰 조회
    @Query("SELECT COUNT(a) FROM AdView a WHERE a.videoAd IN :videoAds AND a.createdAt = :date")
    long countByVideoAdsAndDate(@Param("videoAds") List<VideoAd> videoAds, @Param("date") LocalDate date);


}

