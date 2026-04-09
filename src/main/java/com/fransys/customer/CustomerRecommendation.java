package com.fransys.customer;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer_recommendation")
public class CustomerRecommendation extends BaseEntity {

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "owner_name", length = 64)
    private String ownerName;
}
