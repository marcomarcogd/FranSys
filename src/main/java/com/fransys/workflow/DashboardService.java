package com.fransys.workflow;

import com.fransys.common.enums.WorkflowStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LeadWorkflowService leadWorkflowService;

    public WorkflowDtos.DashboardOverviewResponse overview() {
        return new WorkflowDtos.DashboardOverviewResponse(
                leadWorkflowService.totalLeads(),
                leadWorkflowService.countTodayLeads(),
                leadWorkflowService.countByStage(WorkflowStage.IDENTIFIED),
                leadWorkflowService.countByStage(WorkflowStage.MATCHED),
                leadWorkflowService.countByStage(WorkflowStage.DELIVERING),
                leadWorkflowService.countByStage(WorkflowStage.AFTER_SALES),
                leadWorkflowService.sourceChannelStats(),
                leadWorkflowService.satisfactionStats(),
                leadWorkflowService.repurchaseStats(),
                leadWorkflowService.recentLeads()
        );
    }
}
