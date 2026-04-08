package com.fransys.operation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public void log(String entityType, Long entityId, String action, String operatorName, String detail) {
        OperationLog log = new OperationLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setOperatorName(operatorName);
        log.setDetail(detail);
        operationLogRepository.save(log);
    }

    public List<OperationLog> list(String entityType, Long entityId) {
        return operationLogRepository.findTop20ByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }
}
