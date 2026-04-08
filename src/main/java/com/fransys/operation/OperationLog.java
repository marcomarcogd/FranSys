package com.fransys.operation;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "operation_log")
public class OperationLog extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 64)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(name = "operator_name", length = 64)
    private String operatorName;

    @Column(columnDefinition = "TEXT")
    private String detail;
}
