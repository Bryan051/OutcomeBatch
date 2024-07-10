package com.sparta.outcomebatch.controller;

import com.sparta.outcomebatch.dto.BatchAdRequestDto;
import com.sparta.outcomebatch.repository.VideoAdRepository;
import com.sparta.outcomebatch.service.BatchAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchAdController {

    private final BatchAdService batchAdService;
    private final VideoAdRepository videoAdRepository;

    // 광고 통계 및 정산
    @GetMapping("/countAndAccumulateAdViews")
    public String countAdViewsByVideoAdAndDate(@RequestBody BatchAdRequestDto batchAdRequestDto) {
        return batchAdService.countAdViewsByVideoAdAndDate(batchAdRequestDto);
    }



}
