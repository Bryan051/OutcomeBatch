package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.service.AdBatchProcessorService;
import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.streaming.domain.VideoAd;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AdRevProcessor implements ItemProcessor<VideoAd, AdRev> {

    private final AdBatchProcessorService adBatchProcessorService;

    @Override
    public AdRev process(VideoAd videoAd) throws Exception {
        LocalDate date = LocalDate.now();

        // 오늘 계산값
        int adViewCount = adBatchProcessorService.countAdViewsByVideoAdAndDate(videoAd, date);

        // 어제까지 총합
        int lastTotalView = (int) videoAd.getTotalAdView();

        // 일 정산
        double revenue = adBatchProcessorService.calculateRevenue(lastTotalView, adViewCount);

        // AdRev 객체에 값 설정
        AdRev adRev = new AdRev();
        adRev.setDate(date);
        adRev.setVideoAdId(videoAd.getId());
        adRev.setAdRevenue(BigDecimal.valueOf(revenue));

        return adRev;
    }
}