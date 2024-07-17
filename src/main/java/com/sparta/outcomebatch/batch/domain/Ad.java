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
@Table(name = "ad")
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
