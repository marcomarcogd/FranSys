package com.fransys.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPackageItemRepository extends JpaRepository<ProductPackageItem, Long> {
    List<ProductPackageItem> findByPackageIdOrderBySortOrderAscIdAsc(Long packageId);
    void deleteByPackageId(Long packageId);
}
