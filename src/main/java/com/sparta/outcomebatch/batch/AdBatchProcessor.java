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
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // 오늘 계산값
        int adViewCount = adBatchProcessorService.countAdViewsByVideoAdAndDate(videoAd, startDate, endDate);

        // AdStats 객체에 값 설정
        AdStats adStats = new AdStats();
        adStats.setDate(endDate);
        adStats.setVideoAdId(videoAd.getId());
        adStats.setAdView(adViewCount);


        return adStats;
    }
}