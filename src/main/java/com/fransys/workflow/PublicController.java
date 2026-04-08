package com.fransys.workflow;

import com.fransys.common.api.ApiResponse;
import com.fransys.lead.CustomerLead;
import com.fransys.lead.LeadDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final LeadWorkflowService leadWorkflowService;

    @PostMapping("/leads")
    public ApiResponse<CustomerLead> createLead(@Valid @RequestBody LeadDtos.PublicLeadRequest request) {
        return ApiResponse.success(leadWorkflowService.createPublicLead(request));
    }

    @GetMapping("/feedback/{token}")
    public ApiResponse<WorkflowDtos.PublicFeedbackContextResponse> feedbackContext(@PathVariable String token) {
        return ApiResponse.success(leadWorkflowService.getPublicFeedbackContext(token));
    }

    @PostMapping("/feedback/{token}")
    public ApiResponse<Void> submitFeedback(
            @PathVariable String token,
            @RequestBody WorkflowDtos.PublicFeedbackSubmitRequest request) {
        leadWorkflowService.submitPublicFeedback(token, request);
        return ApiResponse.success();
    }
}
