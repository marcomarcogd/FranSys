package com.fransys.delivery;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverySupervisionRepository extends JpaRepository<DeliverySupervision, Long> {
    Optional<DeliverySupervision> findByLeadId(Long leadId);
}
