package com.fransys.auth;

import com.fransys.common.exception.BusinessException;
import com.fransys.system.SysUser;
import com.fransys.system.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserRepository sysUserRepository;

    @Override
    public SysUserDetails loadUserByUsername(String username) {
        SysUser user = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("账号不存在"));
        return new SysUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getDisplayName(),
                user.getRoleCode(),
                user.getAccountLevel().name(),
                user.getManagerUserId(),
                user.getEnabled()
        );
    }
}
