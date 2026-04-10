package com.fransys.auth;

import com.fransys.common.enums.AccountLevel;
import com.fransys.common.enums.RoleCode;
import com.fransys.common.exception.BusinessException;
import com.fransys.lead.CustomerLead;
import com.fransys.system.SysUser;
import com.fransys.system.SysUserRepository;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataPermissionService {

    private final SysUserRepository sysUserRepository;

    public boolean isAdmin(SysUserDetails currentUser) {
        return currentUser != null && RoleCode.ROLE_ADMIN.name().equals(currentUser.getRoleCode());
    }

    public boolean isLeader(SysUserDetails currentUser) {
        return currentUser != null && AccountLevel.LEADER.name().equals(currentUser.getAccountLevel());
    }

    public boolean canManageOwnership(SysUserDetails currentUser) {
        return isAdmin(currentUser) || isLeader(currentUser);
    }

    public boolean canViewUnassigned(SysUserDetails currentUser) {
        return isAdmin(currentUser) || isLeader(currentUser);
    }

    public Set<Long> accessibleUserIds(SysUserDetails currentUser) {
        if (currentUser == null) {
            return Set.of();
        }
        if (isAdmin(currentUser)) {
            return sysUserRepository.findAll().stream()
                    .map(SysUser::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (!isLeader(currentUser)) {
            return Set.of(currentUser.getUserId());
        }
        return selfAndSubordinates(currentUser.getUserId(), sysUserRepository.findAll());
    }

    public List<SysUser> assignableUsers(SysUserDetails currentUser) {
        Set<Long> allowedIds = assignableUserIds(currentUser);
        return sysUserRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
                .filter(user -> allowedIds.contains(user.getId()))
                .sorted(Comparator.comparing(SysUser::getDisplayName, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    public Set<Long> assignableUserIds(SysUserDetails currentUser) {
        if (currentUser == null) {
            return Set.of();
        }
        if (isAdmin(currentUser)) {
            return sysUserRepository.findAll().stream()
                    .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
                    .map(SysUser::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (!isLeader(currentUser)) {
            return Set.of(currentUser.getUserId());
        }
        return selfAndSubordinates(currentUser.getUserId(), sysUserRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
                .toList());
    }

    public SysUser requireAssignableOwner(Long ownerId, SysUserDetails currentUser) {
        if (ownerId == null) {
            throw new BusinessException("请选择负责人");
        }
        if (!assignableUserIds(currentUser).contains(ownerId)) {
            throw new BusinessException("无权将客户分配给该账号");
        }
        SysUser owner = sysUserRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException("负责人不存在"));
        if (!Boolean.TRUE.equals(owner.getEnabled())) {
            throw new BusinessException("负责人已停用，无法分配");
        }
        return owner;
    }

    public void assertCustomerAccessible(CustomerLead customer, SysUserDetails currentUser) {
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (isAdmin(currentUser)) {
            return;
        }
        if (customer.getOwnerId() == null) {
            if (canViewUnassigned(currentUser)) {
                return;
            }
            throw new BusinessException("无权查看该客户");
        }
        if (accessibleUserIds(currentUser).contains(customer.getOwnerId())) {
            return;
        }
        throw new BusinessException("无权查看该客户");
    }

    public String scopeLabel(SysUserDetails currentUser) {
        if (isAdmin(currentUser)) {
            return "全部客户";
        }
        if (isLeader(currentUser)) {
            return "团队客户";
        }
        return "我的客户";
    }

    private Set<Long> selfAndSubordinates(Long rootUserId, List<SysUser> users) {
        Map<Long, List<SysUser>> byManager = users.stream()
                .filter(user -> user.getManagerUserId() != null)
                .collect(Collectors.groupingBy(SysUser::getManagerUserId));
        Set<Long> result = new LinkedHashSet<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        result.add(rootUserId);
        queue.add(rootUserId);
        while (!queue.isEmpty()) {
            Long parentId = queue.poll();
            for (SysUser child : byManager.getOrDefault(parentId, List.of())) {
                if (result.add(child.getId())) {
                    queue.add(child.getId());
                }
            }
        }
        return result;
    }

    public boolean wouldCreateManagerCycle(Long userId, Long managerUserId) {
        if (userId == null || managerUserId == null) {
            return false;
        }
        Map<Long, SysUser> userMap = sysUserRepository.findAll().stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        Long currentManagerId = managerUserId;
        while (currentManagerId != null) {
            if (Objects.equals(currentManagerId, userId)) {
                return true;
            }
            SysUser manager = userMap.get(currentManagerId);
            currentManagerId = manager == null ? null : manager.getManagerUserId();
        }
        return false;
    }
}
