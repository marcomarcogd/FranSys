package com.fransys.aftersales;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfterSalesFollowupRepository extends JpaRepository<AfterSalesFollowup, Long> {
    Optional<AfterSalesFollowup> findByLeadId(Long leadId);
}
