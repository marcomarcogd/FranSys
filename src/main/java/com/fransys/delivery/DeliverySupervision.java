package com.fransys.delivery;

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
@Table(name = "delivery_supervision")
public class DeliverySupervision extends BaseEntity {

    @Column(name = "lead_id", nullable = false, unique = true)
    private Long leadId;

    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    private String orderNo;

    @Column(name = "service_enterprise", length = 128)
    private String serviceEnterprise;

    @Column(name = "service_project", length = 128)
    private String serviceProject;

    @Column(name = "service_mode", length = 64)
    private String serviceMode;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Column(name = "service_time", length = 64)
    private String serviceTime;

    @Column(name = "coordinator_name", length = 64)
    private String coordinatorName;

    @Column(name = "customer_confirmed")
    private Boolean customerConfirmed;

    @Column(name = "enterprise_confirmed")
    private Boolean enterpriseConfirmed;

    @Column(name = "staff_confirmed")
    private Boolean staffConfirmed;

    @Column(name = "location_confirmed")
    private Boolean locationConfirmed;

    @Column(name = "special_need_confirmed")
    private Boolean specialNeedConfirmed;

    @Column(name = "risk_informed")
    private Boolean riskInformed;

    @Column(name = "started_on_time")
    private Boolean startedOnTime;

    @Column(name = "staff_on_site")
    private Boolean staffOnSite;

    @Column(name = "executed_as_agreed")
    private Boolean executedAsAgreed;

    @Column(name = "has_exception")
    private Boolean hasException;

    @Column(name = "exception_content", columnDefinition = "TEXT")
    private String exceptionContent;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "handler_name", length = 64)
    private String handlerName;

    @Column(name = "follow_up_time")
    private LocalDateTime followUpTime;

    @Column(name = "follow_up_by", length = 64)
    private String followUpBy;

    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;

    @Column(name = "most_satisfied_point", length = 255)
    private String mostSatisfiedPoint;

    @Column(name = "most_dissatisfied_point", length = 255)
    private String mostDissatisfiedPoint;

    @Column(name = "need_adjustment")
    private Boolean needAdjustment;

    @Column(name = "willing_continue")
    private Boolean willingContinue;

    @Column(name = "willing_recommend")
    private Boolean willingRecommend;

    @Column(name = "met_standard")
    private Boolean metStandard;

    @Column(name = "enterprise_performance_level", length = 64)
    private String enterprisePerformanceLevel;

    @Column(name = "continue_cooperation")
    private Boolean continueCooperation;

    @Column(name = "next_action", columnDefinition = "TEXT")
    private String nextAction;

    @Column(columnDefinition = "TEXT")
    private String remark;
}
