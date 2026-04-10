package com.fransys.workflow;

import com.fransys.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerService customerService;

    public WorkflowDtos.DashboardOverviewResponse overview() {
        return new WorkflowDtos.DashboardOverviewResponse(
                customerService.totalCustomers(),
                customerService.todayCustomers(),
                customerService.countByLevel("A"),
                customerService.countByLevel("B"),
                customerService.dueFollowCount(),
                customerService.archivedCount(),
                customerService.sourceChannelStats(),
                customerService.customerLevelStats(),
                customerService.recommendationTypeStats(),
                customerService.dueFollowCustomers(),
                customerService.recentCustomers()
        );
    }
}
