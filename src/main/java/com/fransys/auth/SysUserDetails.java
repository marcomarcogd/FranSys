package com.fransys.auth;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class SysUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String displayName;
    private final String roleCode;
    private final boolean enabled;

    public SysUserDetails(Long userId, String username, String password, String displayName, String roleCode, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.roleCode = roleCode;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roleCode));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
