package com.sparta.outcomebatch.batch.domain.write;

import com.sparta.outcomebatch.batch.domain.VideoAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface VideoAdWriteRepository extends JpaRepository<VideoAd, Long> {

}