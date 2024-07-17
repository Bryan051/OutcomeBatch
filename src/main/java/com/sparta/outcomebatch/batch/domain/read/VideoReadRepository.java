package com.sparta.outcomebatch.batch.domain.read;

import com.sparta.outcomebatch.batch.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface VideoReadRepository extends JpaRepository<Video,Long> {
}
