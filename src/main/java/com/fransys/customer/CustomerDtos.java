package com.fransys.customer;

import com.fransys.lead.CustomerLead;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerDtos {

    public record CustomerUpsertRequest(
            Long id,
            @NotBlank(message = "客户姓名不能为空") String customerName,
            @NotBlank(message = "联系电话不能为空") String contactPhone,
            String gender,
            Integer age,
            String email,
            String wechatNo,
            String region,
            String sourceChannel,
            String customerLevel,
            String currentStatus,
            String referrerName,
            String initialNeedType,
            String servicePreference,
            String budgetRange,
            LocalDate entryDate,
            LocalDateTime followUpAt,
            String remark,
            Boolean archived) {
    }

    public record CustomerListItem(
            Long id,
            String leadNo,
            String customerName,
            String gender,
            Integer age,
            String contactPhone,
            String sourceChannel,
            String customerLevel,
            String ownerName,
            LocalDateTime lastFollowUpAt,
            LocalDateTime followUpAt,
            Boolean archived,
            LocalDate entryDate) {
    }

    public record FollowRecordRequest(
            LocalDateTime followAt,
            String contactMethod,
            String communicationSummary,
            String customerNeed,
            String ourFeedback,
            String customerFeedback,
            String nextAction,
            LocalDateTime nextFollowUpAt,
            String customerLevel) {
    }

    public record RecommendationItemRequest(
            @NotBlank(message = "推荐类型不能为空") String itemType,
            @NotNull(message = "请选择推荐项") Long itemId,
            Integer priorityNo,
            BigDecimal quotedPrice,
            String note) {
    }

    public record RecommendationRequest(
            String recommendationReason,
            String remark,
            @Valid @NotEmpty(message = "请至少添加一个推荐项") List<RecommendationItemRequest> items) {
    }

    public record RecommendationItemView(
            Long id,
            String itemType,
            Long itemId,
            String itemName,
            String enterpriseName,
            Integer priorityNo,
            BigDecimal quotedPrice,
            String note) {
    }

    public record RecommendationView(
            Long id,
            String recommendationReason,
            String remark,
            String ownerName,
            LocalDateTime createdAt,
            List<RecommendationItemView> items) {
    }

    public record CustomerDetailResponse(
            CustomerLead customer,
            List<CustomerFollowRecord> followRecords,
            List<RecommendationView> recommendations,
            String followFrequencyHint) {
    }
}
