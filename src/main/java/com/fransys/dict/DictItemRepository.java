package com.fransys.dict;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictItemRepository extends JpaRepository<DictItem, Long> {
    List<DictItem> findByEnabledTrueOrderByDictTypeAscSortOrderAsc();
    List<DictItem> findByDictTypeOrderBySortOrderAsc(String dictType);
    boolean existsByDictTypeAndItemKey(String dictType, String itemKey);
    boolean existsByDictTypeAndItemKeyAndEnabledTrue(String dictType, String itemKey);
}
