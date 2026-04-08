package com.fransys.lead;

import com.fransys.common.entity.BaseEntity;
import com.fransys.common.enums.WorkflowStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer_lead")
public class CustomerLead extends BaseEntity {

    @Column(name = "lead_no", nullable = false, unique = true, length = 32)
    private String leadNo;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "customer_name", nullable = false, length = 64)
    private String customerName;

    @Column(name = "contact_phone", nullable = false, length = 64)
    private String contactPhone;

    @Column(name = "city_area", length = 128)
    private String cityArea;

    @Column(name = "age_range", length = 64)
    private String ageRange;

    @Column(name = "family_structure", length = 255)
    private String familyStructure;

    @Column(name = "source_channel", length = 64)
    private String sourceChannel;

    @Column(name = "referrer_name", length = 64)
    private String referrerName;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow_stage", nullable = false, length = 32)
    private WorkflowStage workflowStage = WorkflowStage.NEW;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_name", length = 64)
    private String ownerName;

    @Column(name = "follow_up_at")
    private LocalDateTime followUpAt;

    @Column(columnDefinition = "TEXT")
    private String remark;
}
