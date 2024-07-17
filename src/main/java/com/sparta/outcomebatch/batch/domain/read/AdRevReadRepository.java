package com.sparta.outcomebatch.batch.domain.read;

import com.sparta.outcomebatch.batch.domain.AdRev;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AdRevReadRepository extends JpaRepository<AdRev,Long> {
}
