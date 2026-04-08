package com.fransys.operation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    List<OperationLog> findTop20ByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
}
