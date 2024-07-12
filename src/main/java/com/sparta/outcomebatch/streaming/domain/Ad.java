package com.sparta.outcomebatch.streaming.domain;

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
@Entity(name = "Ad")
@Table(name = "Ad")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "ad")
    private List<VideoAd> videoAds = new ArrayList<>();

//    @OneToMany(mappedBy = "ad")
//    private List<DailyStats> dailyStats = new ArrayList<>();
}
