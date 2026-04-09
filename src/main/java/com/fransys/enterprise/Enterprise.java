package com.fransys.enterprise;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "enterprise")
public class Enterprise extends BaseEntity {

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "contact_person", length = 64)
    private String contactPerson;

    @Column(length = 64)
    private String phone;

    @Column(name = "service_area", length = 255)
    private String serviceArea;

    @Column(name = "service_items", length = 500)
    private String serviceItems;

    @Column(name = "service_modes", length = 128)
    private String serviceModes;

    @Column(name = "response_speed", length = 64)
    private String responseSpeed;

    @Column(name = "certification_status", length = 64)
    private String certificationStatus;

    @Column(name = "certification_level", length = 64)
    private String certificationLevel;

    @Column(name = "expertise", length = 255)
    private String expertise;

    @Column(name = "case_experience", length = 255)
    private String caseExperience;

    @Column(name = "price_range", length = 128)
    private String priceRange;

    @Column(name = "service_time", length = 128)
    private String serviceTime;

    @Column(name = "willing_to_take")
    private Boolean willingToTake = true;

    @Column(name = "special_limit", length = 255)
    private String specialLimit;

    @Column(name = "brand_advantage", length = 1000)
    private String brandAdvantage;

    @Column(nullable = false)
    private Boolean active = true;
}
