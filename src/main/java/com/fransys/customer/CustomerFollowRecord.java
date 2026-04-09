package com.fransys.customer;

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
@Table(name = "customer_follow_record")
public class CustomerFollowRecord extends BaseEntity {

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "follow_at", nullable = false)
    private LocalDateTime followAt;

    @Column(name = "contact_method", length = 32)
    private String contactMethod;

    @Column(name = "communication_summary", columnDefinition = "TEXT")
    private String communicationSummary;

    @Column(name = "customer_need", columnDefinition = "TEXT")
    private String customerNeed;

    @Column(name = "our_feedback", columnDefinition = "TEXT")
    private String ourFeedback;

    @Column(name = "customer_feedback", columnDefinition = "TEXT")
    private String customerFeedback;

    @Column(name = "next_action", columnDefinition = "TEXT")
    private String nextAction;

    @Column(name = "next_follow_up_at")
    private LocalDateTime nextFollowUpAt;

    @Column(name = "level_before", length = 16)
    private String levelBefore;

    @Column(name = "level_after", length = 16)
    private String levelAfter;

    @Column(name = "owner_name", length = 64)
    private String ownerName;
}
