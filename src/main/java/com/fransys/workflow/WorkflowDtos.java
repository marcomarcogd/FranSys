package com.fransys.workflow;

import com.fransys.dict.DictItem;
import com.fransys.enterprise.Enterprise;
import com.fransys.system.SysRole;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public class WorkflowDtos {
    public record DashboardStat(String label, long value) {
    }

    public record DashboardRankingItem(
            Long userId,
            String displayName,
            long count,
            int rankNo) {
    }

    public record DashboardOverviewResponse(
            String scopeLabel,
            long totalCustomers,
            long weekNewCustomers,
            long dueFollowCount,
            long archivedCustomers,
            List<DashboardStat> sourceChannelStats,
            List<DashboardStat> customerLevelStats,
            List<DashboardStat> recommendationTypeStats,
            List<com.fransys.customer.CustomerDtos.CustomerListItem> dueFollowCustomers,
            List<com.fransys.customer.CustomerDtos.CustomerListItem> recentCustomers,
            List<DashboardRankingItem> customerOwnerRankings,
            List<DashboardRankingItem> newCustomerRankings) {
    }

    public record GroupedDictResponse(Map<String, List<DictItem>> items) {
    }

    public record UserUpsertRequest(
            Long id,
            @NotBlank(message = "用户名不能为空") String username,
            String password,
            @NotBlank(message = "姓名不能为空") String displayName,
            @NotBlank(message = "角色不能为空") String roleCode,
            @NotBlank(message = "账号等级不能为空") String accountLevel,
            Long managerUserId,
            Boolean enabled) {
    }

    public record UserSummary(
            Long id,
            String username,
            String displayName,
            String roleCode,
            String accountLevel,
            Long managerUserId,
            String managerDisplayName,
            Boolean enabled) {
    }

    public record UserOption(
            Long id,
            String displayName,
            String roleCode,
            String accountLevel) {
    }

    public record SystemMetaResponse(List<UserSummary> users, List<SysRole> roles, List<Enterprise> enterprises) {
    }
}
