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
@Table(name = "product_package")
public class ProductPackage extends BaseEntity {

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "applicable_scene", length = 255)
    private String applicableScene;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;
}
