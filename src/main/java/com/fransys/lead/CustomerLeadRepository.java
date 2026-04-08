package com.fransys.lead;

import com.fransys.common.enums.WorkflowStage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerLeadRepository extends JpaRepository<CustomerLead, Long> {
    Optional<CustomerLead> findByLeadNo(String leadNo);
    long countByWorkflowStage(WorkflowStage workflowStage);
    long countByEntryDate(LocalDate entryDate);
    List<CustomerLead> findTop5ByOrderByCreatedAtDesc();
}
