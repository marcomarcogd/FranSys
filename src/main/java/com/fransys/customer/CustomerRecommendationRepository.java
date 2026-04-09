package com.fransys.customer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRecommendationRepository extends JpaRepository<CustomerRecommendation, Long> {
    List<CustomerRecommendation> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}
