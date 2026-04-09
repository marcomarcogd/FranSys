package com.fransys.product;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_package_item")
public class ProductPackageItem extends BaseEntity {

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", length = 128)
    private String productName;

    @Column(name = "enterprise_name", length = 128)
    private String enterpriseName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "item_note", length = 255)
    private String itemNote;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
