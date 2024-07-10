package com.sparta.outcomebatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_ad")
public class VideoAd implements Serializable {

    // 포지션별로 같은 광고여도 동영상 여러곳에 들어갈 수 있다.
    // 시청시각에 따라 광고가 카운트 되어야 함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_ad_view")
    private long totalAdView;

    @Column(name = "ad_position")
    private int adPosition;

    @ManyToOne
    @JoinColumn(name = "vid_id", nullable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

//    @Column(name = "ad_view_count")
//    private int adViewCount;
}