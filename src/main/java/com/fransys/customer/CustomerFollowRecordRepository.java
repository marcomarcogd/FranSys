package com.fransys.customer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerFollowRecordRepository extends JpaRepository<CustomerFollowRecord, Long> {
    List<CustomerFollowRecord> findByLeadIdOrderByFollowAtDesc(Long leadId);
}
