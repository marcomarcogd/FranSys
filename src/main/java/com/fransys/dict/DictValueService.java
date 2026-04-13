package com.fransys.dict;

import com.fransys.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictValueService {

    private final DictItemRepository dictItemRepository;

    public String normalizeEnabledValue(String dictType, String value, String fieldLabel) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (!dictItemRepository.existsByDictTypeAndItemKeyAndEnabledTrue(dictType, normalized)) {
            throw new BusinessException(fieldLabel + "无效，请从字典选项中选择");
        }
        return normalized;
    }
}
