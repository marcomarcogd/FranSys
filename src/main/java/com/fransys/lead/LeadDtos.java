package com.fransys.lead;

import com.fransys.aftersales.AfterSalesFollowup;
import com.fransys.assessment.NeedAssessment;
import com.fransys.delivery.DeliverySupervision;
import com.fransys.match.EnterpriseMatch;
import com.fransys.match.EnterpriseMatchCandidate;
import com.fransys.operation.OperationLog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LeadDtos {

    public record PublicLeadRequest(
            @NotBlank(message = "客户姓名不能为空") String customerName,
            @NotBlank(message = "联系方式不能为空") String contactPhone,
            String gender,
            Integer age,
            String email,
            String wechatNo,
            String region,
            String cityArea,
            String ageRange,
            String familyStructure,
            String sourceChannel,
            String referrerName,
            String serviceObject,
            String initialNeedType,
            String servicePreference,
            String urgency,
            String budgetRange,
            String remark) {
    }

    public record InternalLeadCreateRequest(
            @NotBlank(message = "客户姓名不能为空") String customerName,
            @NotBlank(message = "联系方式不能为空") String contactPhone,
            LocalDate entryDate,
            String cityArea,
            String ageRange,
            String familyStructure,
            String sourceChannel,
            String referrerName,
            String channelTrustLevel,
            String serviceObject,
            String initialNeedType,
            String servicePreference,
            String urgency,
            Boolean clearPainPoint,
            Boolean strongTrustDemand,
            String budgetRange,
            String decisionMaker,
            String decisionSpeed,
            String priceSensitivity,
            String certificationInterest,
            String customerLevel,
            String currentStatus,
            LocalDateTime followUpAt,
            String remark) {
    }

    public record IdentificationRequest(
            String channelTrustLevel,
            String serviceObject,
            String initialNeedType,
            String servicePreference,
            String urgency,
            Boolean clearPainPoint,
            Boolean strongTrustDemand,
            String budgetRange,
            String decisionMaker,
            String decisionSpeed,
            String priceSensitivity,
            String certificationInterest,
            String customerLevel,
            String currentStatus,
            LocalDateTime followUpAt,
            String ownerName,
            String remark) {
    }

    public record LeadListItem(
            Long id,
            String leadNo,
            LocalDate entryDate,
            String customerName,
            String contactPhone,
            String sourceChannel,
            String customerLevel,
            String currentStatus,
            String workflowStage,
            String ownerName,
            LocalDateTime followUpAt,
            LocalDateTime createdAt) {
    }

    public record LeadDetailResponse(
            CustomerLead lead,
            LeadIdentification identification,
            NeedAssessment assessment,
            EnterpriseMatch match,
            List<EnterpriseMatchCandidate> candidates,
            DeliverySupervision delivery,
            AfterSalesFollowup afterSales,
            List<OperationLog> logs,
            String feedbackToken) {
    }

    public record FeedbackTokenResponse(@NotNull Long leadId, String token, LocalDateTime expireAt, String publicUrl) {
    }
}
