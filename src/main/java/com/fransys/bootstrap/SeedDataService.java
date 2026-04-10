package com.fransys.bootstrap;

import com.fransys.common.enums.AccountLevel;
import com.fransys.common.enums.RoleCode;
import com.fransys.dict.DictItem;
import com.fransys.dict.DictItemRepository;
import com.fransys.enterprise.Enterprise;
import com.fransys.enterprise.EnterpriseRepository;
import com.fransys.product.Product;
import com.fransys.product.ProductPackage;
import com.fransys.product.ProductPackageItem;
import com.fransys.product.ProductPackageItemRepository;
import com.fransys.product.ProductPackageRepository;
import com.fransys.product.ProductRepository;
import java.math.BigDecimal;
import com.fransys.system.SysRole;
import com.fransys.system.SysRoleRepository;
import com.fransys.system.SysUser;
import com.fransys.system.SysUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeedDataService implements CommandLineRunner {

    private final SysRoleRepository sysRoleRepository;
    private final SysUserRepository sysUserRepository;
    private final DictItemRepository dictItemRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ProductRepository productRepository;
    private final ProductPackageRepository productPackageRepository;
    private final ProductPackageItemRepository productPackageItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedDicts();
        seedEnterprises();
        seedProductsAndPackages();
    }

    private void seedRoles() {
        if (sysRoleRepository.count() > 0) {
            return;
        }
        createRole(RoleCode.ROLE_ADMIN.name(), "管理员", "系统全量管理");
        createRole(RoleCode.ROLE_SALES.name(), "销售", "客户管理与推荐跟进");
        createRole(RoleCode.ROLE_OPERATIONS.name(), "运营", "供给维护与业务协同");
    }

    private void createRole(String code, String name, String description) {
        SysRole role = new SysRole();
        role.setRoleCode(code);
        role.setRoleName(name);
        role.setDescription(description);
        sysRoleRepository.save(role);
    }

    private void seedUsers() {
        SysUser admin = ensureUser("admin", "Admin@123", "系统管理员", RoleCode.ROLE_ADMIN.name(), AccountLevel.LEADER, null);
        SysUser salesLeader = ensureUser("sales_leader", "Leader@123", "销售主管", RoleCode.ROLE_SALES.name(), AccountLevel.LEADER, admin.getId());
        ensureUser("sales", "Sales@123", "销售顾问", RoleCode.ROLE_SALES.name(), AccountLevel.STAFF, salesLeader.getId());
        ensureUser("ops", "Ops@123", "运营专员", RoleCode.ROLE_OPERATIONS.name(), AccountLevel.STAFF, null);
    }

    private SysUser ensureUser(
            String username,
            String password,
            String displayName,
            String roleCode,
            AccountLevel accountLevel,
            Long managerUserId) {
        SysUser user = sysUserRepository.findByUsername(username).orElseGet(SysUser::new);
        if (user.getId() == null) {
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setDisplayName(displayName);
            user.setRoleCode(roleCode);
            user.setEnabled(true);
        }
        if (user.getAccountLevel() == null) {
            user.setAccountLevel(accountLevel);
        }
        if ("sales".equals(username) || "sales_leader".equals(username) || "admin".equals(username) || "ops".equals(username)) {
            user.setAccountLevel(accountLevel);
        }
        if ("sales".equals(username)) {
            user.setManagerUserId(managerUserId);
        } else if ("sales_leader".equals(username)) {
            user.setManagerUserId(managerUserId);
        } else if (user.getManagerUserId() == null && managerUserId != null) {
            user.setManagerUserId(managerUserId);
        }
        return sysUserRepository.save(user);
    }

    private void seedDicts() {
        seed("source_channel", List.of("科普宣传", "老客户", "转介绍", "社交媒体", "广告投放", "自然咨询"));
        seedPairs("customer_level", List.of(
                new String[]{"A", "A级高意向"},
                new String[]{"B", "B级潜力"},
                new String[]{"C", "C级普通"},
                new String[]{"D", "D级沉默"}));
        seedPairs("customer_value_level", List.of(
                new String[]{"A", "私定"},
                new String[]{"B", "尊享"},
                new String[]{"C", "基础"},
                new String[]{"D", "会员"}));
    }

    private void seed(String dictType, List<String> labels) {
        for (int i = 0; i < labels.size(); i++) {
            if (dictItemRepository.existsByDictTypeAndItemKey(dictType, labels.get(i))) {
                continue;
            }
            DictItem item = new DictItem();
            item.setDictType(dictType);
            item.setItemKey(labels.get(i));
            item.setItemLabel(labels.get(i));
            item.setSortOrder(i + 1);
            item.setEnabled(true);
            dictItemRepository.save(item);
        }
    }

    private void seedPairs(String dictType, List<String[]> pairs) {
        for (int i = 0; i < pairs.size(); i++) {
            String[] pair = pairs.get(i);
            if (dictItemRepository.existsByDictTypeAndItemKey(dictType, pair[0])) {
                continue;
            }
            DictItem item = new DictItem();
            item.setDictType(dictType);
            item.setItemKey(pair[0]);
            item.setItemLabel(pair[1]);
            item.setSortOrder(i + 1);
            item.setEnabled(true);
            dictItemRepository.save(item);
        }
    }

    private void seedEnterprises() {
        if (enterpriseRepository.count() > 0) {
            return;
        }
        createEnterprise("沪康护理服务中心", "李老师", "13800000001", "上海浦东、徐汇", "康护,上门护理", "上门", "2小时内", "已认证", "A", "康复陪护", "术后康复案例丰富", "300-800/次", "工作日+周末");
        createEnterprise("安心到家健康服务", "王经理", "13800000002", "上海闵行、静安", "陪护,家庭支持", "上门,到店", "当天响应", "已认证", "S", "长者照护", "高龄家庭服务经验", "500-1500/周期", "全天");
        createEnterprise("颐养调理馆", "周顾问", "13800000003", "上海黄浦", "调理,到店服务", "到店", "4小时内", "认证中", "B", "慢病调理", "社区案例沉淀", "200-500/次", "周一至周六");
    }

    private void createEnterprise(String name, String contactPerson, String phone, String area, String items,
                                  String modes, String speed, String certStatus, String certLevel,
                                  String expertise, String cases, String price, String serviceTime) {
        Enterprise enterprise = new Enterprise();
        enterprise.setName(name);
        enterprise.setContactPerson(contactPerson);
        enterprise.setPhone(phone);
        enterprise.setServiceArea(area);
        enterprise.setServiceItems(items);
        enterprise.setServiceModes(modes);
        enterprise.setResponseSpeed(speed);
        enterprise.setCertificationStatus(certStatus);
        enterprise.setCertificationLevel(certLevel);
        enterprise.setExpertise(expertise);
        enterprise.setCaseExperience(cases);
        enterprise.setPriceRange(price);
        enterprise.setServiceTime(serviceTime);
        enterprise.setBrandAdvantage("本地口碑稳定，服务响应机制成熟");
        enterprise.setWillingToTake(true);
        enterprise.setActive(true);
        enterpriseRepository.save(enterprise);
    }

    private void seedProductsAndPackages() {
        if (productRepository.count() > 0 || enterpriseRepository.count() == 0) {
            return;
        }
        List<Enterprise> enterprises = enterpriseRepository.findAll();
        Product productA = createProduct(enterprises.get(0), "术后康复陪护", "8小时/次", "康复护理", "术后恢复", new BigDecimal("899.00"));
        createProduct(enterprises.get(1), "高龄长者陪伴", "12小时/次", "陪护服务", "长者照护", new BigDecimal("1280.00"));
        Product productC = createProduct(enterprises.get(2), "慢病调理方案", "到店 1 次", "基础调理", "慢病管理", new BigDecimal("399.00"));

        if (productPackageRepository.count() == 0) {
            ProductPackage productPackage = new ProductPackage();
            productPackage.setName("术后恢复关怀包");
            productPackage.setApplicableScene("术后两周内家庭恢复");
            productPackage.setPrice(new BigDecimal("2280.00"));
            productPackage.setDescription("组合康复陪护与到店调理，适合术后短周期恢复客户");
            productPackage.setActive(true);
            ProductPackage saved = productPackageRepository.save(productPackage);
            createPackageItem(saved, productA, 2, "含上门康复陪护", 1);
            createPackageItem(saved, productC, 1, "含一次调理到店服务", 2);
        }
    }

    private Product createProduct(Enterprise enterprise, String name, String specification, String category, String people, BigDecimal price) {
        Product product = new Product();
        product.setEnterpriseId(enterprise.getId());
        product.setEnterpriseName(enterprise.getName());
        product.setName(name);
        product.setSpecification(specification);
        product.setCategory(category);
        product.setApplicablePeople(people);
        product.setPrice(price);
        product.setServiceProcess("初步评估 -> 服务安排 -> 服务执行 -> 回访记录");
        product.setRegulatoryInfo("平台内部备案，按规范留档");
        product.setDescription("FranSys 示例产品数据");
        product.setActive(true);
        return productRepository.save(product);
    }

    private void createPackageItem(ProductPackage productPackage, Product product, int quantity, String note, int sortOrder) {
        ProductPackageItem item = new ProductPackageItem();
        item.setPackageId(productPackage.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setEnterpriseName(product.getEnterpriseName());
        item.setQuantity(quantity);
        item.setItemNote(note);
        item.setSortOrder(sortOrder);
        productPackageItemRepository.save(item);
    }
}
