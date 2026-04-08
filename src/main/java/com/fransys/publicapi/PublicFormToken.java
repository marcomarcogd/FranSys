package com.fransys.publicapi;

import com.fransys.common.entity.BaseEntity;
import com.fransys.common.enums.PublicFormType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "public_form_token")
public class PublicFormToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 128)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "form_type", nullable = false, length = 32)
    private PublicFormType formType;

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "order_no", length = 32)
    private String orderNo;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "used_flag", nullable = false)
    private Boolean usedFlag = false;
}
