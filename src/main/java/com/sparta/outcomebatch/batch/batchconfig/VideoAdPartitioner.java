package com.sparta.outcomebatch.batch.batchconfig;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class VideoAdPartitioner implements Partitioner {

    private final EntityManager entityManager;
    @Override
    @Transactional
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionMap = new HashMap<>();

        // 총 비디오 수를 구합니다
        Long videoadCount = entityManager.createQuery("SELECT COUNT(v) FROM VideoAd v", Long.class).getSingleResult();

        int range = (int) (videoadCount / gridSize);
        int minValue = 0;
        int maxValue = range;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putInt("minValue", minValue);
            context.putInt("maxValue", maxValue);
            partitionMap.put("partition" + i, context);

            minValue = maxValue + 1;
            maxValue += range;
        }

        return partitionMap;
    }
}