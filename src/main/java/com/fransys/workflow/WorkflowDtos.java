package com.fransys.workflow;

import com.fransys.dict.DictItem;
import com.fransys.enterprise.Enterprise;
import com.fransys.system.SysRole;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class WorkflowDtos {

    public record NeedAssessmentRequest(
            LocalDate assessmentDate,
            String assessorName,
            String mainIssue,
            String backgroundInfo,
            String needDuration,
            String goalType,
            String serviceMode,
            String serviceTimePreference,
            String serviceFrequency,
            String privacyRequirement,
            String environmentRequirement,
            String staffRequirement,
            String riskConcern,
            Boolean acceptPlatformMatch,
            Boolean needPlatformSupervision,
            Boolean badServiceExperience,
            String budgetRange,
            String decisionMode,
            String startMode,
            String platformConclusion,
            String recommendedServiceMode,
            String recommendedCycle,
            String recommendedEnterpriseType,
            String riskTip,
            Boolean enterMatchFlow) {
    }

    public record MatchCandidateRequest(
            String slotCode,
            Long enterpriseId,
            String enterpriseName,
            String contactPerson,
            String phone,
            String serviceArea,
            String serviceItems,
            String serviceModes,
            String responseSpeed,
            String certificationStatus,
            String certificationLevel,
            String expertise,
            String caseExperience,
            Integer fitScore,
            String priceRange,
            String serviceTime,
            Boolean willingToTake,
            String specialLimit) {
    }

    public record EnterpriseMatchRequest(
            String needType,
            String serviceMode,
            String budgetRange,
            String specialRequirements,
            Long primaryEnterpriseId,
            Long backupEnterpriseId,
            String recommendationReason,
            String otherNotRecommendedReason,
            Boolean enterpriseConfirmed,
            Boolean customerAccepted,
            Long finalEnterpriseId,
            List<MatchCandidateRequest> candidates) {
    }

    public record DeliveryRequest(
            String serviceEnterprise,
            String serviceProject,
            String serviceMode,
            LocalDate serviceDate,
            String serviceTime,
            String coordinatorName,
            Boolean customerConfirmed,
            Boolean enterpriseConfirmed,
            Boolean staffConfirmed,
            Boolean locationConfirmed,
            Boolean specialNeedConfirmed,
            Boolean riskInformed,
            Boolean startedOnTime,
            Boolean staffOnSite,
            Boolean executedAsAgreed,
            Boolean hasException,
            String exceptionContent,
            String resolution,
            String handlerName,
            LocalDateTime followUpTime,
            String followUpBy,
            Integer satisfactionScore,
            String mostSatisfiedPoint,
            String mostDissatisfiedPoint,
            Boolean needAdjustment,
            Boolean willingContinue,
            Boolean willingRecommend,
            Boolean metStandard,
            String enterprisePerformanceLevel,
            Boolean continueCooperation,
            String nextAction,
            String remark) {
    }

    public record AfterSalesRequest(
            LocalDate firstDealDate,
            String firstEnterprise,
            String firstServiceProject,
            String firstSatisfaction,
            String platformRecognition,
            String enterpriseRecognition,
            Boolean hasNewDemand,
            Boolean otherFamilyDemand,
            String repurchasePossibility,
            String repurchaseDirection,
            String recommendedPlan,
            LocalDateTime recommendedCommunicationTime,
            String recommendedCommunicationPerson,
            Boolean willingReferral,
            String potentialReferralTarget,
            String referralProgress,
            Boolean newLeadCreated,
            String customerLifecycleStatus,
            LocalDateTime nextContactTime,
            String longTermOwner,
            String remark) {
    }

    public record PublicFeedbackContextResponse(
            String customerName,
            String orderNo,
            String serviceEnterprise,
            String serviceProject,
            LocalDate serviceDate) {
    }

    public record PublicFeedbackSubmitRequest(
            Integer satisfactionScore,
            String mostSatisfiedPoint,
            String mostDissatisfiedPoint,
            Boolean needAdjustment,
            Boolean willingContinue,
            Boolean willingRecommend,
            String nextAction,
            String remark,
            Boolean hasNewDemand) {
    }

    public record DashboardStat(String label, long value) {
    }

    public record DashboardOverviewResponse(
            long totalCustomers,
            long todayCustomers,
            long aLevelCustomers,
            long bLevelCustomers,
            long dueFollowCount,
            long archivedCustomers,
            List<DashboardStat> sourceChannelStats,
            List<DashboardStat> customerLevelStats,
            List<DashboardStat> recommendationTypeStats,
            List<com.fransys.customer.CustomerDtos.CustomerListItem> recentCustomers) {
    }

    public record GroupedDictResponse(Map<String, List<DictItem>> items) {
    }

    public record UserUpsertRequest(
            Long id,
            @NotBlank(message = "用户名不能为空") String username,
            String password,
            @NotBlank(message = "姓名不能为空") String displayName,
            @NotBlank(message = "角色不能为空") String roleCode,
            Boolean enabled) {
    }

    public record UserSummary(Long id, String username, String displayName, String roleCode, Boolean enabled) {
    }

    public record SystemMetaResponse(List<UserSummary> users, List<SysRole> roles, List<Enterprise> enterprises) {
    }
}
