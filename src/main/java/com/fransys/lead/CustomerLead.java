package com.fransys.lead;

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

    @Column(length = 16)
    private String gender;

    @Column
    private Integer age;

    @Column(length = 128)
    private String email;

    @Column(name = "wechat_no", length = 64)
    private String wechatNo;

    @Column(length = 128)
    private String region;

    @Column(name = "source_channel", length = 64)
    private String sourceChannel;

    @Column(name = "referrer_name", length = 64)
    private String referrerName;

    @Column(name = "initial_need_type", length = 64)
    private String initialNeedType;

    @Column(name = "service_preference", length = 64)
    private String servicePreference;

    @Column(name = "budget_range", length = 128)
    private String budgetRange;

    @Column(name = "customer_level", length = 32)
    private String customerLevel;

    @Column(name = "customer_value_level", length = 32)
    private String customerValueLevel;

    @Column(name = "current_status", length = 32)
    private String currentStatus;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_name", length = 64)
    private String ownerName;

    @Column(name = "follow_up_at")
    private LocalDateTime followUpAt;

    @Column(name = "last_follow_up_at")
    private LocalDateTime lastFollowUpAt;

    @Column(nullable = false)
    private Boolean archived = false;

    @Column(columnDefinition = "TEXT")
    private String remark;
}
