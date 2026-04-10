package com.fransys.customer;

import com.fransys.auth.DataPermissionService;
import com.fransys.auth.SysUserDetails;
import com.fransys.common.exception.BusinessException;
import com.fransys.lead.CustomerLead;
import com.fransys.lead.CustomerLeadRepository;
import com.fransys.lead.LeadDtos;
import com.fransys.operation.OperationLogService;
import com.fransys.product.Product;
import com.fransys.product.ProductPackage;
import com.fransys.product.ProductPackageRepository;
import com.fransys.product.ProductRepository;
import com.fransys.system.SysUser;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerLeadRepository customerLeadRepository;
    private final CustomerFollowRecordRepository customerFollowRecordRepository;
    private final CustomerRecommendationRepository customerRecommendationRepository;
    private final CustomerRecommendationItemRepository customerRecommendationItemRepository;
    private final ProductRepository productRepository;
    private final ProductPackageRepository productPackageRepository;
    private final OperationLogService operationLogService;
    private final DataPermissionService dataPermissionService;

    public List<CustomerDtos.CustomerListItem> listCustomers(
            String keyword,
            String sourceChannel,
            String customerLevel,
            Long ownerId,
            Boolean includeUnassigned,
            Boolean unassignedOnly,
            Boolean archived,
            LocalDateTime lastFollowStart,
            LocalDateTime lastFollowEnd,
            LocalDateTime nextFollowStart,
            LocalDateTime nextFollowEnd,
            SysUserDetails currentUser) {
        return scopedCustomersForList(currentUser, ownerId, includeUnassigned, unassignedOnly).stream()
                .filter(customer -> matchesKeyword(customer, keyword))
                .filter(customer -> isBlank(sourceChannel) || Objects.equals(customer.getSourceChannel(), sourceChannel))
                .filter(customer -> isBlank(customerLevel) || Objects.equals(customer.getCustomerLevel(), customerLevel))
                .filter(customer -> archived == null || Objects.equals(Boolean.TRUE.equals(customer.getArchived()), archived))
                .filter(customer -> inRange(customer.getLastFollowUpAt(), lastFollowStart, lastFollowEnd))
                .filter(customer -> inRange(customer.getFollowUpAt(), nextFollowStart, nextFollowEnd))
                .sorted(customerPriorityComparator())
                .map(this::toListItem)
                .toList();
    }

    public CustomerDtos.CustomerDetailResponse getCustomerDetail(Long customerId, SysUserDetails currentUser) {
        CustomerLead customer = getAccessibleCustomer(customerId, currentUser);
        List<CustomerFollowRecord> followRecords = customerFollowRecordRepository.findByLeadIdOrderByFollowAtDesc(customerId);
        List<CustomerDtos.RecommendationView> recommendations = customerRecommendationRepository.findByLeadIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toRecommendationView)
                .toList();
        return new CustomerDtos.CustomerDetailResponse(customer, followRecords, recommendations, followFrequencyHint(customer.getCustomerLevel()));
    }

    @Transactional
    public CustomerLead saveCustomer(CustomerDtos.CustomerUpsertRequest request, SysUserDetails currentUser) {
        CustomerLead customer = request.id() == null
                ? new CustomerLead()
                : getAccessibleCustomer(request.id(), currentUser);

        Long originalOwnerId = customer.getOwnerId();
        String originalOwnerName = customer.getOwnerName();
        OwnerSelection ownerSelection = resolveOwner(request, customer, currentUser);

        if (customer.getLeadNo() == null) {
            customer.setLeadNo(generateNo("C"));
        }
        customer.setEntryDate(request.entryDate() == null ? LocalDate.now() : request.entryDate());
        customer.setCustomerName(request.customerName());
        customer.setContactPhone(request.contactPhone());
        customer.setGender(request.gender());
        customer.setAge(request.age());
        customer.setEmail(request.email());
        customer.setWechatNo(request.wechatNo());
        customer.setRegion(request.region());
        customer.setCityArea(request.region());
        customer.setSourceChannel(request.sourceChannel());
        customer.setCustomerLevel(defaultIfBlank(request.customerLevel(), customer.getCustomerLevel()));
        customer.setCurrentStatus(defaultIfBlank(request.currentStatus(), defaultIfBlank(customer.getCurrentStatus(), "待跟进")));
        customer.setReferrerName(request.referrerName());
        customer.setInitialNeedType(request.initialNeedType());
        customer.setServicePreference(request.servicePreference());
        customer.setBudgetRange(request.budgetRange());
        customer.setFollowUpAt(request.followUpAt());
        customer.setRemark(request.remark());
        customer.setArchived(request.archived() != null && request.archived());
        customer.setOwnerId(ownerSelection.ownerId());
        customer.setOwnerName(ownerSelection.ownerName());
        CustomerLead saved = customerLeadRepository.save(customer);

        String detail = "保存客户主档";
        if (!Objects.equals(originalOwnerId, saved.getOwnerId())) {
            detail = "保存客户主档并调整负责人";
        }
        operationLogService.log("LEAD", saved.getId(), request.id() == null ? "CUSTOMER_CREATE" : "CUSTOMER_UPDATE", currentUser.getDisplayName(), detail);
        if (!Objects.equals(originalOwnerId, saved.getOwnerId())) {
            operationLogService.log(
                    "LEAD",
                    saved.getId(),
                    "CUSTOMER_ASSIGN",
                    currentUser.getDisplayName(),
                    String.format("客户负责人由 %s 调整为 %s", defaultIfBlank(originalOwnerName, "未分配"), defaultIfBlank(saved.getOwnerName(), "未分配")));
        }
        return saved;
    }

    @Transactional
    public CustomerLead createPublicCustomer(LeadDtos.PublicLeadRequest request) {
        CustomerLead customer = new CustomerLead();
        customer.setLeadNo(generateNo("C"));
        customer.setEntryDate(LocalDate.now());
        customer.setCustomerName(request.customerName());
        customer.setContactPhone(request.contactPhone());
        customer.setGender(request.gender());
        customer.setAge(request.age());
        customer.setEmail(request.email());
        customer.setWechatNo(request.wechatNo());
        customer.setRegion(defaultIfBlank(request.region(), request.cityArea()));
        customer.setCityArea(defaultIfBlank(request.cityArea(), request.region()));
        customer.setAgeRange(request.ageRange());
        customer.setFamilyStructure(request.familyStructure());
        customer.setSourceChannel(defaultIfBlank(request.sourceChannel(), "自然咨询"));
        customer.setReferrerName(request.referrerName());
        customer.setServiceObject(request.serviceObject());
        customer.setInitialNeedType(request.initialNeedType());
        customer.setServicePreference(request.servicePreference());
        customer.setUrgency(request.urgency());
        customer.setBudgetRange(request.budgetRange());
        customer.setCustomerLevel("B");
        customer.setCurrentStatus("待分配");
        customer.setArchived(false);
        customer.setRemark(request.remark());
        customer.setOwnerId(null);
        customer.setOwnerName(null);
        CustomerLead saved = customerLeadRepository.save(customer);

        CustomerFollowRecord followRecord = new CustomerFollowRecord();
        followRecord.setLeadId(saved.getId());
        followRecord.setFollowAt(LocalDateTime.now());
        followRecord.setContactMethod("PHONE");
        followRecord.setCommunicationSummary("公开表单提交需求登记");
        followRecord.setCustomerNeed(defaultIfBlank(request.initialNeedType(), "待补充"));
        followRecord.setOurFeedback("已收到需求登记，待顾问分配");
        followRecord.setOwnerName("SYSTEM");
        followRecord.setLevelAfter(saved.getCustomerLevel());
        customerFollowRecordRepository.save(followRecord);

        operationLogService.log("LEAD", saved.getId(), "CREATE_PUBLIC", "PUBLIC", "公开表单创建客户并进入待分配");
        return saved;
    }

    @Transactional
    public CustomerFollowRecord addFollowRecord(Long customerId, CustomerDtos.FollowRecordRequest request, SysUserDetails currentUser) {
        CustomerLead customer = getAccessibleCustomer(customerId, currentUser);
        CustomerFollowRecord record = new CustomerFollowRecord();
        record.setLeadId(customerId);
        record.setFollowAt(request.followAt() == null ? LocalDateTime.now() : request.followAt());
        record.setContactMethod(request.contactMethod());
        record.setCommunicationSummary(request.communicationSummary());
        record.setCustomerNeed(request.customerNeed());
        record.setOurFeedback(request.ourFeedback());
        record.setCustomerFeedback(request.customerFeedback());
        record.setNextAction(request.nextAction());
        record.setNextFollowUpAt(request.nextFollowUpAt());
        record.setLevelBefore(customer.getCustomerLevel());
        record.setLevelAfter(defaultIfBlank(request.customerLevel(), customer.getCustomerLevel()));
        record.setOwnerName(currentUser.getDisplayName());
        CustomerFollowRecord saved = customerFollowRecordRepository.save(record);

        customer.setLastFollowUpAt(saved.getFollowAt());
        customer.setFollowUpAt(saved.getNextFollowUpAt());
        customer.setCustomerLevel(saved.getLevelAfter());
        customer.setCurrentStatus("跟进中");
        customerLeadRepository.save(customer);

        operationLogService.log("LEAD", customerId, "FOLLOW_RECORD", currentUser.getDisplayName(), "新增客户跟进记录");
        return saved;
    }

    @Transactional
    public CustomerDtos.RecommendationView addRecommendation(Long customerId, CustomerDtos.RecommendationRequest request, SysUserDetails currentUser) {
        getAccessibleCustomer(customerId, currentUser);
        CustomerRecommendation recommendation = new CustomerRecommendation();
        recommendation.setLeadId(customerId);
        recommendation.setRecommendationReason(request.recommendationReason());
        recommendation.setRemark(request.remark());
        recommendation.setOwnerName(currentUser.getDisplayName());
        CustomerRecommendation saved = customerRecommendationRepository.save(recommendation);

        for (CustomerDtos.RecommendationItemRequest itemRequest : request.items()) {
            CustomerRecommendationItem item = new CustomerRecommendationItem();
            item.setRecommendationId(saved.getId());
            item.setItemType(normalizeItemType(itemRequest.itemType()));
            item.setItemId(itemRequest.itemId());
            item.setPriorityNo(itemRequest.priorityNo());
            item.setQuotedPrice(itemRequest.quotedPrice());
            item.setNote(itemRequest.note());
            fillRecommendationItem(item);
            customerRecommendationItemRepository.save(item);
        }

        operationLogService.log("LEAD", customerId, "RECOMMENDATION", currentUser.getDisplayName(), "新增产品或套餐推荐");
        return toRecommendationView(saved);
    }

    public long totalCustomers(SysUserDetails currentUser) {
        return scopedCustomers(currentUser).size();
    }

    public long weekNewCustomers(SysUserDetails currentUser) {
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return scopedCustomers(currentUser).stream()
                .filter(customer -> customer.getEntryDate() != null && !customer.getEntryDate().isBefore(weekStart))
                .count();
    }

    public long dueFollowCount(SysUserDetails currentUser) {
        LocalDateTime now = LocalDateTime.now();
        return scopedCustomers(currentUser).stream()
                .filter(customer -> !Boolean.TRUE.equals(customer.getArchived()))
                .filter(customer -> customer.getFollowUpAt() != null && !customer.getFollowUpAt().isAfter(now))
                .count();
    }

    public long archivedCount(SysUserDetails currentUser) {
        return scopedCustomers(currentUser).stream()
                .filter(customer -> Boolean.TRUE.equals(customer.getArchived()))
                .count();
    }

    public List<CustomerDtos.CustomerListItem> dueFollowCustomers(SysUserDetails currentUser) {
        return scopedCustomers(currentUser).stream()
                .filter(customer -> !Boolean.TRUE.equals(customer.getArchived()))
                .filter(customer -> customer.getFollowUpAt() != null)
                .sorted(
                        Comparator.comparing(CustomerLead::getFollowUpAt, Comparator.nullsLast(LocalDateTime::compareTo))
                                .thenComparing(CustomerLead::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6)
                .map(this::toListItem)
                .toList();
    }

    public List<CustomerDtos.CustomerListItem> recentCustomers(SysUserDetails currentUser) {
        return scopedCustomers(currentUser).stream()
                .sorted(Comparator.comparing(CustomerLead::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::toListItem)
                .toList();
    }

    public List<com.fransys.workflow.WorkflowDtos.DashboardStat> sourceChannelStats(SysUserDetails currentUser) {
        return scopedCustomers(currentUser).stream()
                .collect(Collectors.groupingBy(customer -> defaultIfBlank(customer.getSourceChannel(), "未填写"), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new com.fransys.workflow.WorkflowDtos.DashboardStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<com.fransys.workflow.WorkflowDtos.DashboardStat> customerLevelStats(SysUserDetails currentUser) {
        return scopedCustomers(currentUser).stream()
                .collect(Collectors.groupingBy(customer -> defaultIfBlank(customer.getCustomerLevel(), "未分级"), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new com.fransys.workflow.WorkflowDtos.DashboardStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<com.fransys.workflow.WorkflowDtos.DashboardStat> recommendationTypeStats(SysUserDetails currentUser) {
        Set<Long> accessibleLeadIds = scopedCustomers(currentUser).stream()
                .map(CustomerLead::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Long> accessibleRecommendationIds = customerRecommendationRepository.findAll().stream()
                .filter(recommendation -> accessibleLeadIds.contains(recommendation.getLeadId()))
                .map(CustomerRecommendation::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return customerRecommendationItemRepository.findAll().stream()
                .filter(item -> accessibleRecommendationIds.contains(item.getRecommendationId()))
                .collect(Collectors.groupingBy(CustomerRecommendationItem::getItemType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new com.fransys.workflow.WorkflowDtos.DashboardStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<com.fransys.workflow.WorkflowDtos.DashboardRankingItem> customerOwnerRankings() {
        return buildRanking(
                customerLeadRepository.findAll().stream()
                        .filter(customer -> customer.getOwnerId() != null)
                        .filter(customer -> !Boolean.TRUE.equals(customer.getArchived()))
                        .collect(Collectors.groupingBy(CustomerLead::getOwnerId, Collectors.counting())),
                customerLeadRepository.findAll().stream()
                        .filter(customer -> customer.getOwnerId() != null)
                        .collect(Collectors.toMap(CustomerLead::getOwnerId, CustomerLead::getOwnerName, (left, right) -> left)));
    }

    public List<com.fransys.workflow.WorkflowDtos.DashboardRankingItem> newCustomerRankings() {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        return buildRanking(
                customerLeadRepository.findAll().stream()
                        .filter(customer -> customer.getOwnerId() != null)
                        .filter(customer -> customer.getEntryDate() != null && !customer.getEntryDate().isBefore(monthStart))
                        .collect(Collectors.groupingBy(CustomerLead::getOwnerId, Collectors.counting())),
                customerLeadRepository.findAll().stream()
                        .filter(customer -> customer.getOwnerId() != null)
                        .collect(Collectors.toMap(CustomerLead::getOwnerId, CustomerLead::getOwnerName, (left, right) -> left)));
    }

    private List<com.fransys.workflow.WorkflowDtos.DashboardRankingItem> buildRanking(Map<Long, Long> counts, Map<Long, String> ownerNames) {
        List<Map.Entry<Long, Long>> sortedEntries = counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(10)
                .toList();
        return sortedEntries.stream()
                .map(entry -> new com.fransys.workflow.WorkflowDtos.DashboardRankingItem(
                        entry.getKey(),
                        defaultIfBlank(ownerNames.get(entry.getKey()), "未知账号"),
                        entry.getValue(),
                        sortedEntries.indexOf(entry) + 1))
                .toList();
    }

    private CustomerDtos.RecommendationView toRecommendationView(CustomerRecommendation recommendation) {
        List<CustomerDtos.RecommendationItemView> items = customerRecommendationItemRepository.findByRecommendationIdOrderByPriorityNoAscIdAsc(recommendation.getId())
                .stream()
                .map(item -> new CustomerDtos.RecommendationItemView(
                        item.getId(),
                        item.getItemType(),
                        item.getItemId(),
                        item.getItemName(),
                        item.getEnterpriseName(),
                        item.getPriorityNo(),
                        item.getQuotedPrice(),
                        item.getNote()))
                .toList();
        return new CustomerDtos.RecommendationView(
                recommendation.getId(),
                recommendation.getRecommendationReason(),
                recommendation.getRemark(),
                recommendation.getOwnerName(),
                recommendation.getCreatedAt(),
                items
        );
    }

    private void fillRecommendationItem(CustomerRecommendationItem item) {
        if ("PRODUCT".equals(item.getItemType())) {
            Product product = productRepository.findById(item.getItemId())
                    .orElseThrow(() -> new BusinessException("产品不存在"));
            item.setItemName(product.getName());
            item.setEnterpriseName(product.getEnterpriseName());
            if (item.getQuotedPrice() == null) {
                item.setQuotedPrice(product.getPrice());
            }
            return;
        }
        if ("PACKAGE".equals(item.getItemType())) {
            ProductPackage productPackage = productPackageRepository.findById(item.getItemId())
                    .orElseThrow(() -> new BusinessException("套餐包不存在"));
            item.setItemName(productPackage.getName());
            item.setEnterpriseName("套餐包");
            if (item.getQuotedPrice() == null) {
                item.setQuotedPrice(productPackage.getPrice());
            }
            return;
        }
        throw new BusinessException("不支持的推荐类型");
    }

    private CustomerLead getAccessibleCustomer(Long customerId, SysUserDetails currentUser) {
        CustomerLead customer = customerLeadRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("客户不存在"));
        dataPermissionService.assertCustomerAccessible(customer, currentUser);
        return customer;
    }

    private List<CustomerLead> scopedCustomers(SysUserDetails currentUser) {
        Set<Long> accessibleUserIds = dataPermissionService.accessibleUserIds(currentUser);
        boolean includeUnassigned = dataPermissionService.canViewUnassigned(currentUser);
        return customerLeadRepository.findAll().stream()
                .filter(customer -> isAccessibleCustomer(customer, accessibleUserIds, includeUnassigned))
                .toList();
    }

    private List<CustomerLead> scopedCustomersForList(
            SysUserDetails currentUser,
            Long ownerId,
            Boolean includeUnassigned,
            Boolean unassignedOnly) {
        Set<Long> accessibleUserIds = dataPermissionService.accessibleUserIds(currentUser);
        boolean allowUnassigned = dataPermissionService.canViewUnassigned(currentUser);
        boolean showUnassigned = allowUnassigned && !Boolean.FALSE.equals(includeUnassigned);
        return customerLeadRepository.findAll().stream()
                .filter(customer -> isAccessibleCustomer(customer, accessibleUserIds, allowUnassigned))
                .filter(customer -> {
                    if (Boolean.TRUE.equals(unassignedOnly)) {
                        return allowUnassigned && customer.getOwnerId() == null;
                    }
                    if (ownerId != null) {
                        return Objects.equals(customer.getOwnerId(), ownerId);
                    }
                    if (customer.getOwnerId() == null) {
                        return showUnassigned;
                    }
                    return true;
                })
                .toList();
    }

    private boolean isAccessibleCustomer(CustomerLead customer, Set<Long> accessibleUserIds, boolean includeUnassigned) {
        if (customer.getOwnerId() == null) {
            return includeUnassigned;
        }
        return accessibleUserIds.contains(customer.getOwnerId());
    }

    private OwnerSelection resolveOwner(CustomerDtos.CustomerUpsertRequest request, CustomerLead customer, SysUserDetails currentUser) {
        if (request.id() == null) {
            if (request.ownerId() != null && dataPermissionService.canManageOwnership(currentUser)) {
                SysUser owner = dataPermissionService.requireAssignableOwner(request.ownerId(), currentUser);
                return new OwnerSelection(owner.getId(), owner.getDisplayName());
            }
            return new OwnerSelection(currentUser.getUserId(), currentUser.getDisplayName());
        }
        if (request.ownerId() == null) {
            return new OwnerSelection(customer.getOwnerId(), customer.getOwnerName());
        }
        if (!dataPermissionService.canManageOwnership(currentUser) && !Objects.equals(request.ownerId(), customer.getOwnerId())) {
            throw new BusinessException("无权修改客户负责人");
        }
        if (Objects.equals(request.ownerId(), customer.getOwnerId())) {
            return new OwnerSelection(customer.getOwnerId(), customer.getOwnerName());
        }
        SysUser owner = dataPermissionService.requireAssignableOwner(request.ownerId(), currentUser);
        return new OwnerSelection(owner.getId(), owner.getDisplayName());
    }

    private CustomerDtos.CustomerListItem toListItem(CustomerLead customer) {
        return new CustomerDtos.CustomerListItem(
                customer.getId(),
                customer.getLeadNo(),
                customer.getCustomerName(),
                customer.getGender(),
                customer.getAge(),
                customer.getContactPhone(),
                customer.getSourceChannel(),
                customer.getCustomerLevel(),
                customer.getOwnerId(),
                customer.getOwnerName(),
                customer.getCurrentStatus(),
                customer.getLastFollowUpAt(),
                customer.getFollowUpAt(),
                customer.getArchived(),
                customer.getEntryDate()
        );
    }

    private String followFrequencyHint(String level) {
        return switch (defaultIfBlank(level, "").toUpperCase(Locale.ROOT)) {
            case "A" -> "A级 · 建议每 3 天跟进 1 次";
            case "B" -> "B级 · 建议每周跟进 1 次";
            case "C" -> "C级 · 建议每 2 周跟进 1 次";
            case "D" -> "D级 · 建议季度触达或归档";
            default -> "请先设置客户等级并安排后续跟进";
        };
    }

    private Comparator<CustomerLead> customerPriorityComparator() {
        LocalDateTime now = LocalDateTime.now();
        return Comparator.comparing((CustomerLead customer) -> Boolean.TRUE.equals(customer.getArchived()))
                .thenComparing(customer -> !isDueFollow(customer, now))
                .thenComparing(CustomerLead::getFollowUpAt, Comparator.nullsLast(LocalDateTime::compareTo))
                .thenComparing(CustomerLead::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private boolean isDueFollow(CustomerLead customer, LocalDateTime now) {
        return customer.getFollowUpAt() != null && !customer.getFollowUpAt().isAfter(now);
    }

    private boolean matchesKeyword(CustomerLead customer, String keyword) {
        if (isBlank(keyword)) {
            return true;
        }
        return contains(customer.getLeadNo(), keyword)
                || contains(customer.getCustomerName(), keyword)
                || contains(customer.getContactPhone(), keyword)
                || contains(customer.getWechatNo(), keyword);
    }

    private boolean inRange(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (start != null && value.isBefore(start)) {
            return false;
        }
        return end == null || !value.isAfter(end);
    }

    private String normalizeItemType(String itemType) {
        String normalized = defaultIfBlank(itemType, "").trim().toUpperCase(Locale.ROOT);
        if (!List.of("PRODUCT", "PACKAGE").contains(normalized)) {
            throw new BusinessException("推荐类型必须为 PRODUCT 或 PACKAGE");
        }
        return normalized;
    }

    private String generateNo(String prefix) {
        return prefix + System.currentTimeMillis();
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

    private record OwnerSelection(Long ownerId, String ownerName) {
    }
}
