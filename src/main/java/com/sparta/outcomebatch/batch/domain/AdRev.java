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
@Table(name = "ad_rev")
public class AdRev {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(name = "ad_revenue")
    private Long adRevenue;

    @Column(name = "video_ad_id")
    private Long videoAdId;

//    private Long videoId;
//
//    private Long adId;


}
