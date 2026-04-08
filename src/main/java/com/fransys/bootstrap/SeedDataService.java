package com.fransys.bootstrap;

import com.fransys.common.enums.RoleCode;
import com.fransys.dict.DictItem;
import com.fransys.dict.DictItemRepository;
import com.fransys.enterprise.Enterprise;
import com.fransys.enterprise.EnterpriseRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedDicts();
        seedEnterprises();
    }

    private void seedRoles() {
        if (sysRoleRepository.count() > 0) {
            return;
        }
        createRole(RoleCode.ROLE_ADMIN.name(), "管理员", "系统全量管理");
        createRole(RoleCode.ROLE_SALES.name(), "销售", "线索和需求评估");
        createRole(RoleCode.ROLE_OPERATIONS.name(), "运营", "匹配、交付、售后");
    }

    private void createRole(String code, String name, String description) {
        SysRole role = new SysRole();
        role.setRoleCode(code);
        role.setRoleName(name);
        role.setDescription(description);
        sysRoleRepository.save(role);
    }

    private void seedUsers() {
        if (sysUserRepository.count() > 0) {
            return;
        }
        createUser("admin", "Admin@123", "系统管理员", RoleCode.ROLE_ADMIN.name());
        createUser("sales", "Sales@123", "销售顾问", RoleCode.ROLE_SALES.name());
        createUser("ops", "Ops@123", "运营专员", RoleCode.ROLE_OPERATIONS.name());
    }

    private void createUser(String username, String password, String displayName, String roleCode) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setRoleCode(roleCode);
        user.setEnabled(true);
        sysUserRepository.save(user);
    }

    private void seedDicts() {
        if (dictItemRepository.count() > 0) {
            return;
        }
        seed("source_channel", List.of("熟人推荐", "银行渠道", "私域转介绍", "合作方引荐", "自然咨询"));
        seed("channel_trust_level", List.of("强信任", "半信任", "弱信任"));
        seed("service_object", List.of("本人", "父母", "长辈", "家庭成员"));
        seed("need_type", List.of("调理", "康护", "陪护", "上门服务", "到店服务", "其他"));
        seed("service_preference", List.of("上门", "到店", "均可"));
        seed("urgency", List.of("高", "中", "低"));
        seed("customer_level", List.of("S", "A", "B"));
        seed("current_status", List.of("待联系", "已沟通", "待评估", "已成交", "暂缓", "交付中", "已回访"));
        seed("decision_speed", List.of("快", "中", "慢"));
        seed("price_sensitivity", List.of("高", "中", "低"));
        seed("certification_interest", List.of("强", "中", "弱"));
        seed("goal_type", List.of("缓解", "调理", "康复", "日常支持", "周期管理"));
        seed("risk_concern", List.of("不专业", "不稳定", "不安全", "无法持续", "无人负责"));
        seed("decision_mode", List.of("本人拍板", "家庭共同决策", "其他"));
        seed("start_mode", List.of("先试单", "直接周期服务", "先咨询"));
        seed("service_mode", List.of("上门", "到店", "组合"));
        seed("certification_status", List.of("已认证", "认证中"));
        seed("satisfaction_score", List.of("1", "2", "3", "4", "5"));
        seed("repurchase_possibility", List.of("高", "中", "低"));
        seed("customer_lifecycle_status", List.of("首单客户", "活跃客户", "复购客户", "沉默客户", "流失客户"));
        seed("enterprise_performance_level", List.of("优秀", "良好", "一般", "较差"));
    }

    private void seed(String dictType, List<String> labels) {
        for (int i = 0; i < labels.size(); i++) {
            DictItem item = new DictItem();
            item.setDictType(dictType);
            item.setItemKey(labels.get(i));
            item.setItemLabel(labels.get(i));
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
        enterprise.setWillingToTake(true);
        enterprise.setActive(true);
        enterpriseRepository.save(enterprise);
    }
}
