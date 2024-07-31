package com.sparta.outcomebatch.batch.service;

import com.sparta.outcomebatch.batch.domain.VideoAd;
import com.sparta.outcomebatch.batch.domain.read.AdViewReadRepository;
import com.sparta.outcomebatch.batch.domain.read.VideoAdReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdBatchProcessorService {

    public final VideoAdReadRepository videoAdReadRepository;
    private final AdViewReadRepository adViewReadRepository;

    @Transactional
    public int countAdViewsByVideoAdAndDate(VideoAd videoAd, LocalDate startDate, LocalDate endDate) {
        List<VideoAd> videoAds = Collections.singletonList(videoAd);
        return (int) adViewReadRepository.countByVideoAdsAndDate(videoAds, startDate, endDate);
    }

    public double calculateRevenue(int lastTotalView, int adViewCount) {
        double revenue = 0;
        if (lastTotalView < 100000) {
            int firstHalfView = 99999 - lastTotalView; // 10만까지 남은 뷰
            if (adViewCount <= firstHalfView) {
                revenue = adViewCount * 10;
            } else {
                revenue += firstHalfView * 10; // * 1원
                int secondHalfView = adViewCount - firstHalfView; //10만 넘김
                revenue += secondHalfView * 12;
            }
        } else if (lastTotalView < 500000) {
            int firstHalfView = 499999 - lastTotalView; // 50만 미만
            if (adViewCount <= firstHalfView) {
                revenue = adViewCount * 12;
            } else {
                revenue += firstHalfView * 12;
                int secondHalfView = adViewCount - firstHalfView; //50만 넘김
                revenue += secondHalfView * 15;
            }
        } else if (lastTotalView < 1000000) {
            int firstHalfView = 999999 - lastTotalView; // 100만 미만
            if (adViewCount <= firstHalfView) {
                revenue = adViewCount * 15;
            } else {
                revenue += firstHalfView * 15;
                int secondHalfView = adViewCount - firstHalfView; //100만 넘김
                revenue += secondHalfView * 20;
            }
        } else {
            revenue = adViewCount * 20;
        }
        return revenue;
    }
}