package com.fransys.dict;

import com.fransys.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dict_item")
public class DictItem extends BaseEntity {

    @Column(name = "dict_type", nullable = false, length = 64)
    private String dictType;

    @Column(name = "item_key", nullable = false, length = 64)
    private String itemKey;

    @Column(name = "item_label", nullable = false, length = 128)
    private String itemLabel;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean enabled = true;
}
