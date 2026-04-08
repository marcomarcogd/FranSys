package com.fransys.workflow;

import com.fransys.common.exception.BusinessException;
import com.fransys.dict.DictItem;
import com.fransys.dict.DictItemRepository;
import com.fransys.enterprise.Enterprise;
import com.fransys.enterprise.EnterpriseRepository;
import com.fransys.system.SysRoleRepository;
import com.fransys.system.SysUser;
import com.fransys.system.SysUserRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemDataService {

    private final DictItemRepository dictItemRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final SysUserRepository sysUserRepository;
    private final SysRoleRepository sysRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, List<DictItem>> groupedDicts() {
        return dictItemRepository.findByEnabledTrueOrderByDictTypeAscSortOrderAsc()
                .stream()
                .collect(Collectors.groupingBy(DictItem::getDictType));
    }

    public List<DictItem> listDictItems() {
        return dictItemRepository.findAll();
    }

    @Transactional
    public DictItem saveDictItem(DictItem item) {
        if (item.getId() == null && dictItemRepository.existsByDictTypeAndItemKey(item.getDictType(), item.getItemKey())) {
            throw new BusinessException("字典项已存在");
        }
        return dictItemRepository.save(item);
    }

    public List<Enterprise> listEnterprises() {
        return enterpriseRepository.findAll();
    }

    @Transactional
    public Enterprise saveEnterprise(Enterprise enterprise) {
        return enterpriseRepository.save(enterprise);
    }

    public WorkflowDtos.SystemMetaResponse systemMeta() {
        return new WorkflowDtos.SystemMetaResponse(
                sysUserRepository.findAll().stream()
                        .map(user -> new WorkflowDtos.UserSummary(
                                user.getId(),
                                user.getUsername(),
                                user.getDisplayName(),
                                user.getRoleCode(),
                                user.getEnabled()))
                        .toList(),
                sysRoleRepository.findAll(),
                enterpriseRepository.findAll());
    }

    @Transactional
    public WorkflowDtos.UserSummary saveUser(WorkflowDtos.UserUpsertRequest request) {
        SysUser user = request.id() == null
                ? new SysUser()
                : sysUserRepository.findById(request.id()).orElseThrow(() -> new BusinessException("用户不存在"));
        if (request.id() == null && sysUserRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        user.setUsername(request.username());
        user.setDisplayName(request.displayName());
        user.setRoleCode(request.roleCode());
        user.setEnabled(request.enabled() == null || request.enabled());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        } else if (user.getId() == null) {
            throw new BusinessException("新建用户时必须设置密码");
        }
        SysUser saved = sysUserRepository.save(user);
        return new WorkflowDtos.UserSummary(saved.getId(), saved.getUsername(), saved.getDisplayName(), saved.getRoleCode(), saved.getEnabled());
    }
}
