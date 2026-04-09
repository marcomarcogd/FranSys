package com.fransys.product;

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
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(name = "enterprise_id", nullable = false)
    private Long enterpriseId;

    @Column(name = "enterprise_name", length = 128)
    private String enterpriseName;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 255)
    private String specification;

    @Column(length = 64)
    private String category;

    @Column(name = "applicable_people", length = 255)
    private String applicablePeople;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "service_process", columnDefinition = "TEXT")
    private String serviceProcess;

    @Column(name = "regulatory_info", columnDefinition = "TEXT")
    private String regulatoryInfo;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;
}
