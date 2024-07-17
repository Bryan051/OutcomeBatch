package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.service.AdBatchProcessorService;
import com.sparta.outcomebatch.batch.domain.AdStats;
import com.sparta.outcomebatch.batch.domain.VideoAd;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AdBatchProcessor implements ItemProcessor<VideoAd, AdStats> {

    private final AdBatchProcessorService adBatchProcessorService;

    @Override
    public AdStats process(VideoAd videoAd) throws Exception {
        LocalDate date = LocalDate.now();

        // 오늘 계산값
        int adViewCount = adBatchProcessorService.countAdViewsByVideoAdAndDate(videoAd, date);

        // 어제까지 총합
        int lastTotalView = (int) videoAd.getTotalAdView();

        // AdStats 객체에 값 설정
        AdStats adStats = new AdStats();
        adStats.setDate(date);
        adStats.setVideoAdId(videoAd.getId());
        adStats.setAdView(adViewCount);

        // videoAd 엔티티 업데이트
        videoAd.setTotalAdView(lastTotalView + adViewCount);

        return adStats;
    }
}