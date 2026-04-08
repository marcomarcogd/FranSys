package com.fransys.assessment;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "need_assessment")
public class NeedAssessment extends BaseEntity {

    @Column(name = "lead_id", nullable = false, unique = true)
    private Long leadId;

    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    @Column(name = "assessor_name", length = 64)
    private String assessorName;

    @Column(name = "main_issue", columnDefinition = "TEXT")
    private String mainIssue;

    @Column(name = "background_info", columnDefinition = "TEXT")
    private String backgroundInfo;

    @Column(name = "need_duration", length = 64)
    private String needDuration;

    @Column(name = "goal_type", length = 64)
    private String goalType;

    @Column(name = "service_mode", length = 64)
    private String serviceMode;

    @Column(name = "service_time_preference", length = 128)
    private String serviceTimePreference;

    @Column(name = "service_frequency", length = 128)
    private String serviceFrequency;

    @Column(name = "privacy_requirement", length = 255)
    private String privacyRequirement;

    @Column(name = "environment_requirement", length = 255)
    private String environmentRequirement;

    @Column(name = "staff_requirement", length = 255)
    private String staffRequirement;

    @Column(name = "risk_concern", length = 128)
    private String riskConcern;

    @Column(name = "accept_platform_match")
    private Boolean acceptPlatformMatch;

    @Column(name = "need_platform_supervision")
    private Boolean needPlatformSupervision;

    @Column(name = "bad_service_experience")
    private Boolean badServiceExperience;

    @Column(name = "budget_range", length = 128)
    private String budgetRange;

    @Column(name = "decision_mode", length = 64)
    private String decisionMode;

    @Column(name = "start_mode", length = 64)
    private String startMode;

    @Column(name = "platform_conclusion", columnDefinition = "TEXT")
    private String platformConclusion;

    @Column(name = "recommended_service_mode", length = 64)
    private String recommendedServiceMode;

    @Column(name = "recommended_cycle", length = 128)
    private String recommendedCycle;

    @Column(name = "recommended_enterprise_type", length = 128)
    private String recommendedEnterpriseType;

    @Column(name = "risk_tip", columnDefinition = "TEXT")
    private String riskTip;

    @Column(name = "enter_match_flow")
    private Boolean enterMatchFlow;
}
