package com.sparta.outcomebatch.controller;

import com.sparta.outcomebatch.dto.BatchVideoRequestDto;
import com.sparta.outcomebatch.entity.Video;
import com.sparta.outcomebatch.repository.VideoRepository;
import com.sparta.outcomebatch.service.BatchVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchVideoController {

    public final BatchVideoService batchVideoService;
    private final VideoRepository videoRepository;

    // 배치 및 정산
    @PostMapping("/countVideoViews")
    public String countVideoViewsExcludingUser(@RequestBody BatchVideoRequestDto batchVideoRequestDto) {
        return batchVideoService.countAndCalculateVideoViews(batchVideoRequestDto);
    }
    @GetMapping("/sumVideoViewDurations")
    public Long sumVideoViewDurationsExcludingUserAndDate( @RequestBody BatchVideoRequestDto batchVideoRequestDto) {

        Video video = videoRepository.findById(batchVideoRequestDto.getVidId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        return batchVideoService.sumVideoViewDurationsExcludingUserAndDate(video, batchVideoRequestDto.getDate());
    }

}
