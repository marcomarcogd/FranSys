package com.fransys.customer;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer_recommendation_item")
public class CustomerRecommendationItem extends BaseEntity {

    @Column(name = "recommendation_id", nullable = false)
    private Long recommendationId;

    @Column(name = "item_type", nullable = false, length = 16)
    private String itemType;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "item_name", length = 128)
    private String itemName;

    @Column(name = "enterprise_name", length = 128)
    private String enterpriseName;

    @Column(name = "priority_no")
    private Integer priorityNo;

    @Column(name = "quoted_price", precision = 10, scale = 2)
    private BigDecimal quotedPrice;

    @Column(length = 255)
    private String note;
}
