package com.fransys.lead;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lead_identification")
public class LeadIdentification extends BaseEntity {

    @Column(name = "lead_id", nullable = false, unique = true)
    private Long leadId;

    @Column(name = "channel_trust_level", length = 64)
    private String channelTrustLevel;

    @Column(name = "service_object", length = 64)
    private String serviceObject;

    @Column(name = "initial_need_type", length = 64)
    private String initialNeedType;

    @Column(name = "service_preference", length = 64)
    private String servicePreference;

    @Column(length = 64)
    private String urgency;

    @Column(name = "clear_pain_point")
    private Boolean clearPainPoint;

    @Column(name = "strong_trust_demand")
    private Boolean strongTrustDemand;

    @Column(name = "budget_range", length = 128)
    private String budgetRange;

    @Column(name = "decision_maker", length = 64)
    private String decisionMaker;

    @Column(name = "decision_speed", length = 64)
    private String decisionSpeed;

    @Column(name = "price_sensitivity", length = 64)
    private String priceSensitivity;

    @Column(name = "certification_interest", length = 64)
    private String certificationInterest;

    @Column(name = "customer_level", length = 32)
    private String customerLevel;

    @Column(name = "current_status", length = 32)
    private String currentStatus;

    @Column(name = "follow_up_at")
    private LocalDateTime followUpAt;

    @Column(name = "owner_name", length = 64)
    private String ownerName;

    @Column(columnDefinition = "TEXT")
    private String remark;
}
