package com.fransys.customer;

import com.fransys.auth.SysUserDetails;
import com.fransys.common.api.ApiResponse;
import com.fransys.lead.CustomerLead;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<List<CustomerDtos.CustomerListItem>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sourceChannel,
            @RequestParam(required = false) String customerLevel,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastFollowStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastFollowEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nextFollowStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nextFollowEnd) {
        return ApiResponse.success(customerService.listCustomers(
                keyword,
                sourceChannel,
                customerLevel,
                archived,
                lastFollowStart,
                lastFollowEnd,
                nextFollowStart,
                nextFollowEnd));
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<CustomerDtos.CustomerDetailResponse> detail(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.getCustomerDetail(customerId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<CustomerLead> create(
            @Valid @RequestBody CustomerDtos.CustomerUpsertRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(customerService.saveCustomer(request, currentUser));
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<CustomerLead> update(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerDtos.CustomerUpsertRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(customerService.saveCustomer(
                new CustomerDtos.CustomerUpsertRequest(
                        customerId,
                        request.customerName(),
                        request.contactPhone(),
                        request.gender(),
                        request.age(),
                        request.email(),
                        request.wechatNo(),
                        request.region(),
                        request.sourceChannel(),
                        request.customerLevel(),
                        request.currentStatus(),
                        request.referrerName(),
                        request.initialNeedType(),
                        request.servicePreference(),
                        request.budgetRange(),
                        request.entryDate(),
                        request.followUpAt(),
                        request.remark(),
                        request.archived()),
                currentUser));
    }

    @PostMapping("/{customerId}/follow-records")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<CustomerFollowRecord> addFollowRecord(
            @PathVariable Long customerId,
            @RequestBody CustomerDtos.FollowRecordRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(customerService.addFollowRecord(customerId, request, currentUser));
    }

    @PostMapping("/{customerId}/recommendations")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<CustomerDtos.RecommendationView> addRecommendation(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerDtos.RecommendationRequest request,
            @AuthenticationPrincipal SysUserDetails currentUser) {
        return ApiResponse.success(customerService.addRecommendation(customerId, request, currentUser));
    }
}
