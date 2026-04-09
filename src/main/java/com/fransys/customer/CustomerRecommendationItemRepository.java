package com.fransys.customer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRecommendationItemRepository extends JpaRepository<CustomerRecommendationItem, Long> {
    List<CustomerRecommendationItem> findByRecommendationIdOrderByPriorityNoAscIdAsc(Long recommendationId);
    void deleteByRecommendationId(Long recommendationId);
}
