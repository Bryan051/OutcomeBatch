package com.sparta.outcomebatch.batch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_stats")
public class VideoStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(name = "video_view")
    private int videoView;

    @Column(name = "play_time")
    private Long playTime;

    @Column(name = "video_id")
    private Long videoId;

    // getters and setters
}
