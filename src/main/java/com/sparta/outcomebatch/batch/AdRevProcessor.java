package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.service.AdBatchProcessorService;
import com.sparta.outcomebatch.batch.domain.AdRev;
import com.sparta.outcomebatch.batch.domain.VideoAd;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AdRevProcessor implements ItemProcessor<VideoAd, AdRev> {

    private final AdBatchProcessorService adBatchProcessorService;

    @Override
    @Transactional(transactionManager = "streamingTransactionManager", readOnly = true)
    public AdRev process(VideoAd videoAd) throws Exception {
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().minusDays(2);

        // 오늘 계산값
        int adViewCount = adBatchProcessorService.countAdViewsByVideoAdAndDate(videoAd, startDate, endDate);

        // 어제까지 총합
        int lastTotalView = (int) videoAd.getTotalAdView();

        // 일 정산
        double revenue = adBatchProcessorService.calculateRevenue(lastTotalView, adViewCount);

        // AdRev 객체에 값 설정
        AdRev adRev = new AdRev();
        adRev.setDate(endDate);
        adRev.setVideoAdId(videoAd.getId());
        adRev.setAdRevenue((long) revenue);

        return adRev;
    }
}