//package com.sparta.outcomebatch.batch.job;//package com.sparta.outcome.batch.job;
//
//import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
//import com.sparta.outcomebatch.dto.BatchVideoRequestDto;
//import com.sparta.outcomebatch.entity.Video;
//import com.sparta.outcomebatch.repository.VideoRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/batch")
//public class VideoBatchController {
//    private final VideoBatchProcessorService videoBatchProcessorService;
//    private final VideoRepository videoRepository;
//
//
//    // 배치 및 정산
//    @PostMapping("/countVideoViews")
//    public String countVideoViewsExcludingUser(@RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
//        return videoBatchProcessorService.countAndCalculateAllVideoViews(date);
//    }
//    @GetMapping("/sumVideoViewDurations")
//    public Long sumVideoViewDurationsExcludingUserAndDate( @RequestBody BatchVideoRequestDto batchVideoRequestDto) {
//
//        Video video = videoRepository.findById(batchVideoRequestDto.getVidId())
//                .orElseThrow(() -> new RuntimeException("Video not found"));
//
//        return videoBatchProcessorService.sumVideoViewDurationsExcludingUserAndDate(video, batchVideoRequestDto.getDate());
//    }
//
//}

//    @GetMapping("/countVideoViews")
//    public ResponseEntity<String> countAndCalculateVideoViews(@RequestBody BatchVideoRequestDto batchVideoRequestDto){
//        videoBatchProcessorService.countAndCalculateVideoViews(batchVideoRequestDto);
//        return ResponseEntity.ok("일 조회수, 광고 통계 완료");
//    }
//
//    @GetMapping("/sumVideoViewDurations")
//    public ResponseEntity<String> sumVideoViewDurationsExcludingUserAndDate(@RequestBody BatchVideoRequestDto batchVideoRequestDto){
//        Video video = videoRepository.findById(batchVideoRequestDto.getVidId())
//                .orElseThrow(() -> new RuntimeException("Video not found"));
//
//        videoBatchProcessorService.sumVideoViewDurationsExcludingUserAndDate(video, batchVideoRequestDto.getDate());
//        return ResponseEntity.ok("재생시간 누적합");
//    }
