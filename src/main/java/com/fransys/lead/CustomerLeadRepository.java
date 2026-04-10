package com.fransys.lead;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerLeadRepository extends JpaRepository<CustomerLead, Long> {
    Optional<CustomerLead> findByLeadNo(String leadNo);
}
