package com.fransys.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDtos {

    public record ProductUpsertRequest(
            Long id,
            @NotNull(message = "请选择所属企业") Long enterpriseId,
            @NotBlank(message = "产品名称不能为空") String name,
            String specification,
            String category,
            String applicablePeople,
            BigDecimal price,
            String imageUrl,
            String serviceProcess,
            String regulatoryInfo,
            String description,
            Boolean active) {
    }

    public record ProductView(
            Long id,
            Long enterpriseId,
            String enterpriseName,
            String name,
            String specification,
            String category,
            String applicablePeople,
            BigDecimal price,
            String imageUrl,
            String serviceProcess,
            String regulatoryInfo,
            String description,
            Boolean active,
            LocalDateTime updatedAt) {
    }

    public record PackageItemRequest(
            @NotNull(message = "请选择产品") Long productId,
            Integer quantity,
            String itemNote,
            Integer sortOrder) {
    }

    public record ProductPackageUpsertRequest(
            Long id,
            @NotBlank(message = "套餐名称不能为空") String name,
            String applicableScene,
            BigDecimal price,
            String description,
            Boolean active,
            @Valid @NotEmpty(message = "请至少选择一个产品") List<PackageItemRequest> items) {
    }

    public record ProductPackageItemView(
            Long id,
            Long productId,
            String productName,
            String enterpriseName,
            Integer quantity,
            String itemNote,
            Integer sortOrder) {
    }

    public record ProductPackageView(
            Long id,
            String name,
            String applicableScene,
            BigDecimal price,
            String description,
            Boolean active,
            int itemCount,
            LocalDateTime updatedAt,
            List<ProductPackageItemView> items) {
    }
}
