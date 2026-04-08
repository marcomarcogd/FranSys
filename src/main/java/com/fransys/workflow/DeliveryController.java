package com.fransys.workflow;

import com.fransys.auth.SysUserDetails;
import com.fransys.common.api.ApiResponse;
import com.fransys.delivery.DeliverySupervision;
import com.fransys.lead.LeadDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final LeadWorkflowService leadWorkflowService;

    @PutMapping("/{leadId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<DeliverySupervision> save(
            @PathVariable Long leadId,
            @RequestBody WorkflowDtos.DeliveryRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(leadWorkflowService.saveDelivery(leadId, request, currentUser));
    }

    @PostMapping("/{leadId}/feedback-token")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<LeadDtos.FeedbackTokenResponse> createFeedbackToken(
            @PathVariable Long leadId,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(leadWorkflowService.createFeedbackToken(leadId, currentUser));
    }
}
