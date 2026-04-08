package com.fransys.workflow;

import com.fransys.aftersales.AfterSalesFollowup;
import com.fransys.auth.SysUserDetails;
import com.fransys.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aftersales")
@RequiredArgsConstructor
public class AfterSalesController {

    private final LeadWorkflowService leadWorkflowService;

    @PutMapping("/{leadId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<AfterSalesFollowup> save(
            @PathVariable Long leadId,
            @RequestBody WorkflowDtos.AfterSalesRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(leadWorkflowService.saveAfterSales(leadId, request, currentUser));
    }
}
