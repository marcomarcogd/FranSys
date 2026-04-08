package com.fransys.match;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "enterprise_match")
public class EnterpriseMatch extends BaseEntity {

    @Column(name = "lead_id", nullable = false, unique = true)
    private Long leadId;

    @Column(name = "need_type", length = 64)
    private String needType;

    @Column(name = "service_mode", length = 64)
    private String serviceMode;

    @Column(name = "budget_range", length = 128)
    private String budgetRange;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;

    @Column(name = "primary_enterprise_id")
    private Long primaryEnterpriseId;

    @Column(name = "backup_enterprise_id")
    private Long backupEnterpriseId;

    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;

    @Column(name = "other_not_recommended_reason", columnDefinition = "TEXT")
    private String otherNotRecommendedReason;

    @Column(name = "enterprise_confirmed")
    private Boolean enterpriseConfirmed;

    @Column(name = "customer_accepted")
    private Boolean customerAccepted;

    @Column(name = "final_enterprise_id")
    private Long finalEnterpriseId;
}
