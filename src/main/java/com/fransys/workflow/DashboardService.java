package com.fransys.workflow;

import com.fransys.auth.DataPermissionService;
import com.fransys.auth.SysUserDetails;
import com.fransys.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerService customerService;
    private final DataPermissionService dataPermissionService;

    public WorkflowDtos.DashboardOverviewResponse overview(SysUserDetails currentUser) {
        return new WorkflowDtos.DashboardOverviewResponse(
                dataPermissionService.scopeLabel(currentUser),
                customerService.totalCustomers(currentUser),
                customerService.weekNewCustomers(currentUser),
                customerService.dueFollowCount(currentUser),
                customerService.archivedCount(currentUser),
                customerService.sourceChannelStats(currentUser),
                customerService.customerLevelStats(currentUser),
                customerService.recommendationTypeStats(currentUser),
                customerService.dueFollowCustomers(currentUser),
                customerService.recentCustomers(currentUser),
                customerService.customerOwnerRankings(),
                customerService.newCustomerRankings()
        );
    }
}
