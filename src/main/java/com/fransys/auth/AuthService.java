package com.fransys.auth;

import com.fransys.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException ex) {
            throw new BusinessException("用户名或密码错误");
        }
        SysUserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        return new AuthDtos.LoginResponse(
                jwtService.generateToken(userDetails),
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getDisplayName(),
                userDetails.getRoleCode());
    }

    public AuthDtos.CurrentUserResponse me(SysUserDetails userDetails) {
        return new AuthDtos.CurrentUserResponse(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getDisplayName(),
                userDetails.getRoleCode());
    }
}
