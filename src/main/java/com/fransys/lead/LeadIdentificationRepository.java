package com.fransys.lead;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadIdentificationRepository extends JpaRepository<LeadIdentification, Long> {
    Optional<LeadIdentification> findByLeadId(Long leadId);
}
