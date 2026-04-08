package com.fransys.assessment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeedAssessmentRepository extends JpaRepository<NeedAssessment, Long> {
    Optional<NeedAssessment> findByLeadId(Long leadId);
}
