package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoAd;
import com.sparta.outcomebatch.batch.service.AdBatchProcessorService;
import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoAdUpdateProcessor implements ItemProcessor<VideoAd, VideoAd> {

    private final AdBatchProcessorService adBatchProcessorService;

    @Override
    @Transactional(transactionManager = "batchTransactionManager")
    public VideoAd process(VideoAd videoAd) throws Exception {
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().minusDays(2);

        // 오늘 계산값
        int adViewCount = adBatchProcessorService.countAdViewsByVideoAdAndDate(videoAd, startDate, endDate);

        // 어제까지 총합
        int lastTotalView = (int) videoAd.getTotalAdView();

        // videoAd 엔티티 업데이트
        videoAd.setTotalAdView(lastTotalView + adViewCount);

        return videoAd;
    }
}