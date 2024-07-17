package com.sparta.outcomebatch.batch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vid_id", nullable = false)
    private Long vidId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "vid_length")
    private int vidLength; // 초 단위

    @Column(name = "total_video_view")
    private int totalVideoView; // 조회수 누적합

//    @Column(name = "view_count")
//    private int viewCount;
    // on update 자동갱신
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "created_at")
    private LocalDate createdAt;

//    @OneToMany(mappedBy = "video")
//    private List<DailyStats> dailyStats = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<VideoAd> videoAds = new ArrayList<>();


    // getters and setters
}

