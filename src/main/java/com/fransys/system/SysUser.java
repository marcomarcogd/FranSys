package com.fransys.system;

import com.fransys.common.entity.BaseEntity;
import com.fransys.common.enums.AccountLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseEntity {

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;

    @Column(name = "role_code", nullable = false, length = 64)
    private String roleCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_level", nullable = false, length = 32)
    private AccountLevel accountLevel = AccountLevel.STAFF;

    @Column(name = "manager_user_id")
    private Long managerUserId;

    @Column(nullable = false)
    private Boolean enabled = true;
}
