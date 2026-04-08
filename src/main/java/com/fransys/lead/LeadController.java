package com.fransys.lead;

import com.fransys.auth.SysUserDetails;
import com.fransys.common.api.ApiResponse;
import com.fransys.workflow.LeadWorkflowService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadWorkflowService leadWorkflowService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<List<LeadDtos.LeadListItem>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sourceChannel,
            @RequestParam(required = false) String customerLevel,
            @RequestParam(required = false) String currentStatus,
            @RequestParam(required = false) String workflowStages) {
        return ApiResponse.success(leadWorkflowService.listLeads(keyword, sourceChannel, customerLevel, currentStatus, workflowStages));
    }

    @GetMapping("/{leadId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<LeadDtos.LeadDetailResponse> detail(@PathVariable Long leadId) {
        return ApiResponse.success(leadWorkflowService.getLeadDetail(leadId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES')")
    public ApiResponse<CustomerLead> createInternal(
            @Valid @RequestBody LeadDtos.InternalLeadCreateRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(leadWorkflowService.createInternalLead(request, currentUser));
    }

    @PutMapping("/{leadId}/identification")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES')")
    public ApiResponse<LeadIdentification> saveIdentification(
            @PathVariable Long leadId,
            @RequestBody LeadDtos.IdentificationRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(leadWorkflowService.saveIdentification(leadId, request, currentUser));
    }
}
