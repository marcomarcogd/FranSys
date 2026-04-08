package com.fransys.workflow;

import com.fransys.aftersales.AfterSalesFollowup;
import com.fransys.aftersales.AfterSalesFollowupRepository;
import com.fransys.assessment.NeedAssessment;
import com.fransys.assessment.NeedAssessmentRepository;
import com.fransys.auth.SysUserDetails;
import com.fransys.common.enums.PublicFormType;
import com.fransys.common.enums.WorkflowStage;
import com.fransys.common.exception.BusinessException;
import com.fransys.config.AppProperties;
import com.fransys.delivery.DeliverySupervision;
import com.fransys.delivery.DeliverySupervisionRepository;
import com.fransys.enterprise.Enterprise;
import com.fransys.enterprise.EnterpriseRepository;
import com.fransys.lead.CustomerLead;
import com.fransys.lead.CustomerLeadRepository;
import com.fransys.lead.LeadDtos;
import com.fransys.lead.LeadIdentification;
import com.fransys.lead.LeadIdentificationRepository;
import com.fransys.match.EnterpriseMatch;
import com.fransys.match.EnterpriseMatchCandidate;
import com.fransys.match.EnterpriseMatchCandidateRepository;
import com.fransys.match.EnterpriseMatchRepository;
import com.fransys.operation.OperationLogService;
import com.fransys.publicapi.PublicFormToken;
import com.fransys.publicapi.PublicFormTokenRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeadWorkflowService {

    private final CustomerLeadRepository customerLeadRepository;
    private final LeadIdentificationRepository leadIdentificationRepository;
    private final NeedAssessmentRepository needAssessmentRepository;
    private final EnterpriseMatchRepository enterpriseMatchRepository;
    private final EnterpriseMatchCandidateRepository enterpriseMatchCandidateRepository;
    private final DeliverySupervisionRepository deliverySupervisionRepository;
    private final AfterSalesFollowupRepository afterSalesFollowupRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PublicFormTokenRepository publicFormTokenRepository;
    private final OperationLogService operationLogService;
    private final AppProperties appProperties;

    public List<LeadDtos.LeadListItem> listLeads(String keyword, String sourceChannel, String customerLevel, String currentStatus, String workflowStages) {
        List<String> stageFilters = parseWorkflowStages(workflowStages);
        return customerLeadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .filter(lead -> isBlank(keyword) || contains(lead.getLeadNo(), keyword) || contains(lead.getCustomerName(), keyword) || contains(lead.getContactPhone(), keyword))
                .filter(lead -> isBlank(sourceChannel) || Objects.equals(lead.getSourceChannel(), sourceChannel))
                .filter(lead -> isBlank(customerLevel) || Objects.equals(lead.getCustomerLevel(), customerLevel))
                .filter(lead -> isBlank(currentStatus) || Objects.equals(lead.getCurrentStatus(), currentStatus))
                .filter(lead -> stageFilters.isEmpty() || stageFilters.contains(lead.getWorkflowStage().name()))
                .map(this::toListItem)
                .toList();
    }

    public LeadDtos.LeadDetailResponse getLeadDetail(Long leadId) {
        CustomerLead lead = getLead(leadId);
        LeadIdentification identification = leadIdentificationRepository.findByLeadId(leadId).orElse(null);
        NeedAssessment assessment = needAssessmentRepository.findByLeadId(leadId).orElse(null);
        EnterpriseMatch match = enterpriseMatchRepository.findByLeadId(leadId).orElse(null);
        List<EnterpriseMatchCandidate> candidates = match == null ? List.of()
                : enterpriseMatchCandidateRepository.findByMatchIdOrderBySlotCodeAsc(match.getId());
        DeliverySupervision delivery = deliverySupervisionRepository.findByLeadId(leadId).orElse(null);
        AfterSalesFollowup afterSales = afterSalesFollowupRepository.findByLeadId(leadId).orElse(null);
        String feedbackToken = publicFormTokenRepository.findFirstByLeadIdAndFormTypeOrderByCreatedAtDesc(leadId, PublicFormType.FEEDBACK)
                .filter(token -> token.getExpireAt().isAfter(LocalDateTime.now()))
                .map(PublicFormToken::getToken)
                .orElse(null);
        return new LeadDtos.LeadDetailResponse(
                lead,
                identification,
                assessment,
                match,
                candidates,
                delivery,
                afterSales,
                operationLogService.list("LEAD", leadId),
                feedbackToken
        );
    }

    @Transactional
    public CustomerLead createInternalLead(LeadDtos.InternalLeadCreateRequest request, SysUserDetails currentUser) {
        CustomerLead lead = new CustomerLead();
        lead.setLeadNo(generateNo("L"));
        lead.setEntryDate(request.entryDate() == null ? LocalDate.now() : request.entryDate());
        lead.setCustomerName(request.customerName());
        lead.setContactPhone(request.contactPhone());
        lead.setCityArea(request.cityArea());
        lead.setAgeRange(request.ageRange());
        lead.setFamilyStructure(request.familyStructure());
        lead.setSourceChannel(request.sourceChannel());
        lead.setReferrerName(request.referrerName());
        lead.setChannelTrustLevel(request.channelTrustLevel());
        lead.setServiceObject(request.serviceObject());
        lead.setInitialNeedType(request.initialNeedType());
        lead.setServicePreference(request.servicePreference());
        lead.setUrgency(request.urgency());
        lead.setClearPainPoint(request.clearPainPoint());
        lead.setStrongTrustDemand(request.strongTrustDemand());
        lead.setBudgetRange(request.budgetRange());
        lead.setDecisionMaker(request.decisionMaker());
        lead.setDecisionSpeed(request.decisionSpeed());
        lead.setPriceSensitivity(request.priceSensitivity());
        lead.setCertificationInterest(request.certificationInterest());
        lead.setCustomerLevel(request.customerLevel());
        lead.setCurrentStatus(defaultIfBlank(request.currentStatus(), "待联系"));
        lead.setWorkflowStage(WorkflowStage.NEW);
        lead.setOwnerId(currentUser.getUserId());
        lead.setOwnerName(currentUser.getDisplayName());
        lead.setFollowUpAt(request.followUpAt());
        lead.setRemark(request.remark());
        CustomerLead saved = customerLeadRepository.save(lead);
        operationLogService.log("LEAD", saved.getId(), "CREATE_INTERNAL", currentUser.getDisplayName(), "内部创建客户线索");
        return saved;
    }

    @Transactional
    public CustomerLead createPublicLead(LeadDtos.PublicLeadRequest request) {
        CustomerLead lead = new CustomerLead();
        lead.setLeadNo(generateNo("L"));
        lead.setEntryDate(LocalDate.now());
        lead.setCustomerName(request.customerName());
        lead.setContactPhone(request.contactPhone());
        lead.setCityArea(request.cityArea());
        lead.setAgeRange(request.ageRange());
        lead.setFamilyStructure(request.familyStructure());
        lead.setSourceChannel(defaultIfBlank(request.sourceChannel(), "自然咨询"));
        lead.setReferrerName(request.referrerName());
        lead.setServiceObject(request.serviceObject());
        lead.setInitialNeedType(request.initialNeedType());
        lead.setServicePreference(request.servicePreference());
        lead.setUrgency(request.urgency());
        lead.setBudgetRange(request.budgetRange());
        lead.setCurrentStatus("待联系");
        lead.setWorkflowStage(WorkflowStage.NEW);
        lead.setRemark(request.remark());
        CustomerLead saved = customerLeadRepository.save(lead);
        operationLogService.log("LEAD", saved.getId(), "CREATE_PUBLIC", "PUBLIC", "公开表单提交线索");
        return saved;
    }

    @Transactional
    public LeadIdentification saveIdentification(Long leadId, LeadDtos.IdentificationRequest request, SysUserDetails currentUser) {
        CustomerLead lead = getLead(leadId);
        LeadIdentification identification = leadIdentificationRepository.findByLeadId(leadId).orElseGet(LeadIdentification::new);
        identification.setLeadId(leadId);
        identification.setChannelTrustLevel(request.channelTrustLevel());
        identification.setServiceObject(request.serviceObject());
        identification.setInitialNeedType(request.initialNeedType());
        identification.setServicePreference(request.servicePreference());
        identification.setUrgency(request.urgency());
        identification.setClearPainPoint(request.clearPainPoint());
        identification.setStrongTrustDemand(request.strongTrustDemand());
        identification.setBudgetRange(request.budgetRange());
        identification.setDecisionMaker(request.decisionMaker());
        identification.setDecisionSpeed(request.decisionSpeed());
        identification.setPriceSensitivity(request.priceSensitivity());
        identification.setCertificationInterest(request.certificationInterest());
        identification.setCustomerLevel(request.customerLevel());
        identification.setCurrentStatus(request.currentStatus());
        identification.setFollowUpAt(request.followUpAt());
        identification.setOwnerName(defaultIfBlank(request.ownerName(), currentUser.getDisplayName()));
        identification.setRemark(request.remark());
        LeadIdentification saved = leadIdentificationRepository.save(identification);

        lead.setChannelTrustLevel(request.channelTrustLevel());
        lead.setServiceObject(request.serviceObject());
        lead.setInitialNeedType(request.initialNeedType());
        lead.setServicePreference(request.servicePreference());
        lead.setUrgency(request.urgency());
        lead.setClearPainPoint(request.clearPainPoint());
        lead.setStrongTrustDemand(request.strongTrustDemand());
        lead.setBudgetRange(request.budgetRange());
        lead.setDecisionMaker(request.decisionMaker());
        lead.setDecisionSpeed(request.decisionSpeed());
        lead.setPriceSensitivity(request.priceSensitivity());
        lead.setCertificationInterest(request.certificationInterest());
        lead.setCustomerLevel(request.customerLevel());
        lead.setCurrentStatus(defaultIfBlank(request.currentStatus(), "已沟通"));
        lead.setWorkflowStage(WorkflowStage.IDENTIFIED);
        lead.setOwnerId(currentUser.getUserId());
        lead.setOwnerName(defaultIfBlank(request.ownerName(), currentUser.getDisplayName()));
        lead.setFollowUpAt(request.followUpAt());
        lead.setRemark(request.remark());
        customerLeadRepository.save(lead);
        operationLogService.log("LEAD", leadId, "IDENTIFY", currentUser.getDisplayName(), "更新客户识别与分级");
        return saved;
    }

    @Transactional
    public NeedAssessment saveAssessment(Long leadId, WorkflowDtos.NeedAssessmentRequest request, SysUserDetails currentUser) {
        CustomerLead lead = getLead(leadId);
        NeedAssessment assessment = needAssessmentRepository.findByLeadId(leadId).orElseGet(NeedAssessment::new);
        assessment.setLeadId(leadId);
        assessment.setAssessmentDate(request.assessmentDate());
        assessment.setAssessorName(defaultIfBlank(request.assessorName(), currentUser.getDisplayName()));
        assessment.setMainIssue(request.mainIssue());
        assessment.setBackgroundInfo(request.backgroundInfo());
        assessment.setNeedDuration(request.needDuration());
        assessment.setGoalType(request.goalType());
        assessment.setServiceMode(request.serviceMode());
        assessment.setServiceTimePreference(request.serviceTimePreference());
        assessment.setServiceFrequency(request.serviceFrequency());
        assessment.setPrivacyRequirement(request.privacyRequirement());
        assessment.setEnvironmentRequirement(request.environmentRequirement());
        assessment.setStaffRequirement(request.staffRequirement());
        assessment.setRiskConcern(request.riskConcern());
        assessment.setAcceptPlatformMatch(request.acceptPlatformMatch());
        assessment.setNeedPlatformSupervision(request.needPlatformSupervision());
        assessment.setBadServiceExperience(request.badServiceExperience());
        assessment.setBudgetRange(request.budgetRange());
        assessment.setDecisionMode(request.decisionMode());
        assessment.setStartMode(request.startMode());
        assessment.setPlatformConclusion(request.platformConclusion());
        assessment.setRecommendedServiceMode(request.recommendedServiceMode());
        assessment.setRecommendedCycle(request.recommendedCycle());
        assessment.setRecommendedEnterpriseType(request.recommendedEnterpriseType());
        assessment.setRiskTip(request.riskTip());
        assessment.setEnterMatchFlow(request.enterMatchFlow());
        NeedAssessment saved = needAssessmentRepository.save(assessment);

        lead.setBudgetRange(defaultIfBlank(request.budgetRange(), lead.getBudgetRange()));
        lead.setCurrentStatus(Boolean.TRUE.equals(request.enterMatchFlow()) ? "待评估" : "暂缓");
        lead.setWorkflowStage(WorkflowStage.ASSESSED);
        customerLeadRepository.save(lead);
        operationLogService.log("LEAD", leadId, "ASSESS", currentUser.getDisplayName(), "更新需求评估");
        return saved;
    }

    @Transactional
    public EnterpriseMatch saveMatch(Long leadId, WorkflowDtos.EnterpriseMatchRequest request, SysUserDetails currentUser) {
        CustomerLead lead = getLead(leadId);
        EnterpriseMatch match = enterpriseMatchRepository.findByLeadId(leadId).orElseGet(EnterpriseMatch::new);
        match.setLeadId(leadId);
        match.setNeedType(request.needType());
        match.setServiceMode(request.serviceMode());
        match.setBudgetRange(request.budgetRange());
        match.setSpecialRequirements(request.specialRequirements());
        match.setPrimaryEnterpriseId(request.primaryEnterpriseId());
        match.setBackupEnterpriseId(request.backupEnterpriseId());
        match.setRecommendationReason(request.recommendationReason());
        match.setOtherNotRecommendedReason(request.otherNotRecommendedReason());
        match.setEnterpriseConfirmed(request.enterpriseConfirmed());
        match.setCustomerAccepted(request.customerAccepted());
        match.setFinalEnterpriseId(request.finalEnterpriseId());
        EnterpriseMatch saved = enterpriseMatchRepository.save(match);

        enterpriseMatchCandidateRepository.deleteByMatchId(saved.getId());
        if (request.candidates() != null) {
            for (WorkflowDtos.MatchCandidateRequest candidateRequest : request.candidates()) {
                EnterpriseMatchCandidate candidate = new EnterpriseMatchCandidate();
                candidate.setMatchId(saved.getId());
                candidate.setSlotCode(defaultIfBlank(candidateRequest.slotCode(), "A"));
                if (candidateRequest.enterpriseId() != null) {
                    enterpriseRepository.findById(candidateRequest.enterpriseId()).ifPresent(enterprise -> fillCandidateFromEnterprise(candidate, enterprise));
                }
                candidate.setEnterpriseId(candidateRequest.enterpriseId());
                candidate.setEnterpriseName(defaultIfBlank(candidateRequest.enterpriseName(), candidate.getEnterpriseName()));
                candidate.setContactPerson(defaultIfBlank(candidateRequest.contactPerson(), candidate.getContactPerson()));
                candidate.setPhone(defaultIfBlank(candidateRequest.phone(), candidate.getPhone()));
                candidate.setServiceArea(defaultIfBlank(candidateRequest.serviceArea(), candidate.getServiceArea()));
                candidate.setServiceItems(defaultIfBlank(candidateRequest.serviceItems(), candidate.getServiceItems()));
                candidate.setServiceModes(defaultIfBlank(candidateRequest.serviceModes(), candidate.getServiceModes()));
                candidate.setResponseSpeed(defaultIfBlank(candidateRequest.responseSpeed(), candidate.getResponseSpeed()));
                candidate.setCertificationStatus(defaultIfBlank(candidateRequest.certificationStatus(), candidate.getCertificationStatus()));
                candidate.setCertificationLevel(defaultIfBlank(candidateRequest.certificationLevel(), candidate.getCertificationLevel()));
                candidate.setExpertise(defaultIfBlank(candidateRequest.expertise(), candidate.getExpertise()));
                candidate.setCaseExperience(defaultIfBlank(candidateRequest.caseExperience(), candidate.getCaseExperience()));
                candidate.setFitScore(candidateRequest.fitScore());
                candidate.setPriceRange(defaultIfBlank(candidateRequest.priceRange(), candidate.getPriceRange()));
                candidate.setServiceTime(defaultIfBlank(candidateRequest.serviceTime(), candidate.getServiceTime()));
                candidate.setWillingToTake(candidateRequest.willingToTake());
                candidate.setSpecialLimit(defaultIfBlank(candidateRequest.specialLimit(), candidate.getSpecialLimit()));
                enterpriseMatchCandidateRepository.save(candidate);
            }
        }

        if (Boolean.TRUE.equals(request.customerAccepted())) {
            lead.setCurrentStatus("已成交");
        }
        lead.setWorkflowStage(WorkflowStage.MATCHED);
        customerLeadRepository.save(lead);
        operationLogService.log("LEAD", leadId, "MATCH", currentUser.getDisplayName(), "更新企业匹配与推荐");
        return saved;
    }

    @Transactional
    public DeliverySupervision saveDelivery(Long leadId, WorkflowDtos.DeliveryRequest request, SysUserDetails currentUser) {
        CustomerLead lead = getLead(leadId);
        DeliverySupervision delivery = deliverySupervisionRepository.findByLeadId(leadId).orElseGet(DeliverySupervision::new);
        delivery.setLeadId(leadId);
        if (delivery.getOrderNo() == null) {
            delivery.setOrderNo(generateNo("O"));
        }
        delivery.setServiceEnterprise(request.serviceEnterprise());
        delivery.setServiceProject(request.serviceProject());
        delivery.setServiceMode(request.serviceMode());
        delivery.setServiceDate(request.serviceDate());
        delivery.setServiceTime(request.serviceTime());
        delivery.setCoordinatorName(defaultIfBlank(request.coordinatorName(), currentUser.getDisplayName()));
        delivery.setCustomerConfirmed(request.customerConfirmed());
        delivery.setEnterpriseConfirmed(request.enterpriseConfirmed());
        delivery.setStaffConfirmed(request.staffConfirmed());
        delivery.setLocationConfirmed(request.locationConfirmed());
        delivery.setSpecialNeedConfirmed(request.specialNeedConfirmed());
        delivery.setRiskInformed(request.riskInformed());
        delivery.setStartedOnTime(request.startedOnTime());
        delivery.setStaffOnSite(request.staffOnSite());
        delivery.setExecutedAsAgreed(request.executedAsAgreed());
        delivery.setHasException(request.hasException());
        delivery.setExceptionContent(request.exceptionContent());
        delivery.setResolution(request.resolution());
        delivery.setHandlerName(request.handlerName());
        delivery.setFollowUpTime(request.followUpTime());
        delivery.setFollowUpBy(request.followUpBy());
        delivery.setSatisfactionScore(request.satisfactionScore());
        delivery.setMostSatisfiedPoint(request.mostSatisfiedPoint());
        delivery.setMostDissatisfiedPoint(request.mostDissatisfiedPoint());
        delivery.setNeedAdjustment(request.needAdjustment());
        delivery.setWillingContinue(request.willingContinue());
        delivery.setWillingRecommend(request.willingRecommend());
        delivery.setMetStandard(request.metStandard());
        delivery.setEnterprisePerformanceLevel(request.enterprisePerformanceLevel());
        delivery.setContinueCooperation(request.continueCooperation());
        delivery.setNextAction(request.nextAction());
        delivery.setRemark(request.remark());
        DeliverySupervision saved = deliverySupervisionRepository.save(delivery);

        lead.setCurrentStatus(request.satisfactionScore() == null ? "交付中" : "已回访");
        lead.setWorkflowStage(request.satisfactionScore() == null ? WorkflowStage.DELIVERING : WorkflowStage.DELIVERED);
        customerLeadRepository.save(lead);
        operationLogService.log("LEAD", leadId, "DELIVERY", currentUser.getDisplayName(), "更新交付监督与回访");
        return saved;
    }

    @Transactional
    public AfterSalesFollowup saveAfterSales(Long leadId, WorkflowDtos.AfterSalesRequest request, SysUserDetails currentUser) {
        CustomerLead lead = getLead(leadId);
        AfterSalesFollowup afterSales = afterSalesFollowupRepository.findByLeadId(leadId).orElseGet(AfterSalesFollowup::new);
        afterSales.setLeadId(leadId);
        afterSales.setFirstDealDate(request.firstDealDate());
        afterSales.setFirstEnterprise(request.firstEnterprise());
        afterSales.setFirstServiceProject(request.firstServiceProject());
        afterSales.setFirstSatisfaction(request.firstSatisfaction());
        afterSales.setPlatformRecognition(request.platformRecognition());
        afterSales.setEnterpriseRecognition(request.enterpriseRecognition());
        afterSales.setHasNewDemand(request.hasNewDemand());
        afterSales.setOtherFamilyDemand(request.otherFamilyDemand());
        afterSales.setRepurchasePossibility(request.repurchasePossibility());
        afterSales.setRepurchaseDirection(request.repurchaseDirection());
        afterSales.setRecommendedPlan(request.recommendedPlan());
        afterSales.setRecommendedCommunicationTime(request.recommendedCommunicationTime());
        afterSales.setRecommendedCommunicationPerson(request.recommendedCommunicationPerson());
        afterSales.setWillingReferral(request.willingReferral());
        afterSales.setPotentialReferralTarget(request.potentialReferralTarget());
        afterSales.setReferralProgress(request.referralProgress());
        afterSales.setNewLeadCreated(request.newLeadCreated());
        afterSales.setCustomerLifecycleStatus(request.customerLifecycleStatus());
        afterSales.setNextContactTime(request.nextContactTime());
        afterSales.setLongTermOwner(defaultIfBlank(request.longTermOwner(), currentUser.getDisplayName()));
        afterSales.setRemark(request.remark());
        AfterSalesFollowup saved = afterSalesFollowupRepository.save(afterSales);

        lead.setWorkflowStage(WorkflowStage.AFTER_SALES);
        lead.setCurrentStatus("已回访");
        customerLeadRepository.save(lead);
        operationLogService.log("LEAD", leadId, "AFTER_SALES", currentUser.getDisplayName(), "更新售后复购跟进");
        return saved;
    }

    @Transactional
    public LeadDtos.FeedbackTokenResponse createFeedbackToken(Long leadId, SysUserDetails currentUser) {
        getLead(leadId);
        DeliverySupervision delivery = deliverySupervisionRepository.findByLeadId(leadId)
                .orElseThrow(() -> new BusinessException("请先保存交付监督信息"));

        PublicFormToken token = new PublicFormToken();
        token.setLeadId(leadId);
        token.setOrderNo(delivery.getOrderNo());
        token.setFormType(PublicFormType.FEEDBACK);
        token.setToken(UUID.randomUUID().toString().replace("-", ""));
        token.setExpireAt(LocalDateTime.now().plusDays(appProperties.getPublicFeedback().getExpireDays()));
        token.setUsedFlag(false);
        PublicFormToken saved = publicFormTokenRepository.save(token);
        operationLogService.log("LEAD", leadId, "CREATE_FEEDBACK_TOKEN", currentUser.getDisplayName(), "生成公开回访链接");
        return new LeadDtos.FeedbackTokenResponse(leadId, saved.getToken(), saved.getExpireAt(), "/public/feedback/" + saved.getToken());
    }

    public WorkflowDtos.PublicFeedbackContextResponse getPublicFeedbackContext(String tokenValue) {
        PublicFormToken token = getAvailableToken(tokenValue);
        CustomerLead lead = getLead(token.getLeadId());
        DeliverySupervision delivery = deliverySupervisionRepository.findByLeadId(token.getLeadId())
                .orElseThrow(() -> new BusinessException("交付记录不存在"));
        return new WorkflowDtos.PublicFeedbackContextResponse(
                lead.getCustomerName(),
                delivery.getOrderNo(),
                delivery.getServiceEnterprise(),
                delivery.getServiceProject(),
                delivery.getServiceDate());
    }

    @Transactional
    public void submitPublicFeedback(String tokenValue, WorkflowDtos.PublicFeedbackSubmitRequest request) {
        PublicFormToken token = getAvailableToken(tokenValue);
        CustomerLead lead = getLead(token.getLeadId());
        DeliverySupervision delivery = deliverySupervisionRepository.findByLeadId(token.getLeadId())
                .orElseThrow(() -> new BusinessException("交付记录不存在"));

        delivery.setSatisfactionScore(request.satisfactionScore());
        delivery.setMostSatisfiedPoint(request.mostSatisfiedPoint());
        delivery.setMostDissatisfiedPoint(request.mostDissatisfiedPoint());
        delivery.setNeedAdjustment(request.needAdjustment());
        delivery.setWillingContinue(request.willingContinue());
        delivery.setWillingRecommend(request.willingRecommend());
        delivery.setNextAction(request.nextAction());
        delivery.setRemark(request.remark());
        delivery.setFollowUpTime(LocalDateTime.now());
        delivery.setFollowUpBy("PUBLIC");
        deliverySupervisionRepository.save(delivery);

        lead.setWorkflowStage(WorkflowStage.DELIVERED);
        lead.setCurrentStatus("已回访");
        customerLeadRepository.save(lead);

        AfterSalesFollowup afterSales = afterSalesFollowupRepository.findByLeadId(token.getLeadId()).orElseGet(AfterSalesFollowup::new);
        afterSales.setLeadId(token.getLeadId());
        afterSales.setFirstDealDate(delivery.getServiceDate());
        afterSales.setFirstEnterprise(delivery.getServiceEnterprise());
        afterSales.setFirstServiceProject(delivery.getServiceProject());
        afterSales.setFirstSatisfaction(request.satisfactionScore() == null ? null : String.valueOf(request.satisfactionScore()));
        afterSales.setHasNewDemand(request.hasNewDemand());
        afterSalesFollowupRepository.save(afterSales);

        token.setUsedFlag(true);
        publicFormTokenRepository.save(token);
        operationLogService.log("LEAD", token.getLeadId(), "PUBLIC_FEEDBACK", "PUBLIC", "外部回访提交完成");
    }

    public List<WorkflowDtos.DashboardStat> sourceChannelStats() {
        return customerLeadRepository.findAll().stream()
                .collect(Collectors.groupingBy(lead -> defaultIfBlank(lead.getSourceChannel(), "未填写"), Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(entry -> new WorkflowDtos.DashboardStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<WorkflowDtos.DashboardStat> satisfactionStats() {
        return deliverySupervisionRepository.findAll().stream()
                .filter(delivery -> delivery.getSatisfactionScore() != null)
                .collect(Collectors.groupingBy(delivery -> String.valueOf(delivery.getSatisfactionScore()), Collectors.counting()))
                .entrySet().stream()
                .sorted(Comparator.comparing(java.util.Map.Entry::getKey))
                .map(entry -> new WorkflowDtos.DashboardStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<WorkflowDtos.DashboardStat> repurchaseStats() {
        return afterSalesFollowupRepository.findAll().stream()
                .collect(Collectors.groupingBy(afterSales -> defaultIfBlank(afterSales.getRepurchasePossibility(), "未填写"), Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(entry -> new WorkflowDtos.DashboardStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    public long countTodayLeads() {
        return customerLeadRepository.countByEntryDate(LocalDate.now());
    }

    public long countByStage(WorkflowStage stage) {
        return customerLeadRepository.countByWorkflowStage(stage);
    }

    public long totalLeads() {
        return customerLeadRepository.count();
    }

    public List<LeadDtos.LeadListItem> recentLeads() {
        return customerLeadRepository.findTop5ByOrderByCreatedAtDesc().stream().map(this::toListItem).toList();
    }

    private PublicFormToken getAvailableToken(String tokenValue) {
        PublicFormToken token = publicFormTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("回访链接不存在"));
        if (token.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("回访链接已过期");
        }
        if (Boolean.TRUE.equals(token.getUsedFlag())) {
            throw new BusinessException("回访链接已使用");
        }
        return token;
    }

    private CustomerLead getLead(Long leadId) {
        return customerLeadRepository.findById(leadId).orElseThrow(() -> new BusinessException("客户线索不存在"));
    }

    private void fillCandidateFromEnterprise(EnterpriseMatchCandidate candidate, Enterprise enterprise) {
        candidate.setEnterpriseName(enterprise.getName());
        candidate.setContactPerson(enterprise.getContactPerson());
        candidate.setPhone(enterprise.getPhone());
        candidate.setServiceArea(enterprise.getServiceArea());
        candidate.setServiceItems(enterprise.getServiceItems());
        candidate.setServiceModes(enterprise.getServiceModes());
        candidate.setResponseSpeed(enterprise.getResponseSpeed());
        candidate.setCertificationStatus(enterprise.getCertificationStatus());
        candidate.setCertificationLevel(enterprise.getCertificationLevel());
        candidate.setExpertise(enterprise.getExpertise());
        candidate.setCaseExperience(enterprise.getCaseExperience());
        candidate.setPriceRange(enterprise.getPriceRange());
        candidate.setServiceTime(enterprise.getServiceTime());
        candidate.setWillingToTake(enterprise.getWillingToTake());
        candidate.setSpecialLimit(enterprise.getSpecialLimit());
    }

    private LeadDtos.LeadListItem toListItem(CustomerLead lead) {
        return new LeadDtos.LeadListItem(
                lead.getId(),
                lead.getLeadNo(),
                lead.getEntryDate(),
                lead.getCustomerName(),
                lead.getContactPhone(),
                lead.getSourceChannel(),
                lead.getCustomerLevel(),
                lead.getCurrentStatus(),
                lead.getWorkflowStage().name(),
                lead.getOwnerName(),
                lead.getFollowUpAt(),
                lead.getCreatedAt()
        );
    }

    private String generateNo(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    private List<String> parseWorkflowStages(String workflowStages) {
        if (isBlank(workflowStages)) {
            return List.of();
        }
        return Stream.of(workflowStages.split(","))
                .map(String::trim)
                .filter(stage -> !stage.isBlank())
                .toList();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }
}
