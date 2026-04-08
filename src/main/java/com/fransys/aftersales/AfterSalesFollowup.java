package com.fransys.aftersales;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "after_sales_followup")
public class AfterSalesFollowup extends BaseEntity {

    @Column(name = "lead_id", nullable = false, unique = true)
    private Long leadId;

    @Column(name = "first_deal_date")
    private LocalDate firstDealDate;

    @Column(name = "first_enterprise", length = 128)
    private String firstEnterprise;

    @Column(name = "first_service_project", length = 128)
    private String firstServiceProject;

    @Column(name = "first_satisfaction", length = 64)
    private String firstSatisfaction;

    @Column(name = "platform_recognition", length = 64)
    private String platformRecognition;

    @Column(name = "enterprise_recognition", length = 64)
    private String enterpriseRecognition;

    @Column(name = "has_new_demand")
    private Boolean hasNewDemand;

    @Column(name = "other_family_demand")
    private Boolean otherFamilyDemand;

    @Column(name = "repurchase_possibility", length = 64)
    private String repurchasePossibility;

    @Column(name = "repurchase_direction", length = 64)
    private String repurchaseDirection;

    @Column(name = "recommended_plan", columnDefinition = "TEXT")
    private String recommendedPlan;

    @Column(name = "recommended_communication_time")
    private LocalDateTime recommendedCommunicationTime;

    @Column(name = "recommended_communication_person", length = 64)
    private String recommendedCommunicationPerson;

    @Column(name = "willing_referral")
    private Boolean willingReferral;

    @Column(name = "potential_referral_target", length = 255)
    private String potentialReferralTarget;

    @Column(name = "referral_progress", length = 255)
    private String referralProgress;

    @Column(name = "new_lead_created")
    private Boolean newLeadCreated;

    @Column(name = "customer_lifecycle_status", length = 64)
    private String customerLifecycleStatus;

    @Column(name = "next_contact_time")
    private LocalDateTime nextContactTime;

    @Column(name = "long_term_owner", length = 64)
    private String longTermOwner;

    @Column(columnDefinition = "TEXT")
    private String remark;
}
