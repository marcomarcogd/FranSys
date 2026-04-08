package com.fransys.match;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseMatchRepository extends JpaRepository<EnterpriseMatch, Long> {
    Optional<EnterpriseMatch> findByLeadId(Long leadId);
}
