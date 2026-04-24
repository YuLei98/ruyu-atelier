package icu.ruiyu.framework.integration.security.auth;

import icu.ruiyu.framework.integration.security.model.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * UserDetails 实现类，用于 Spring Security 认证
 */
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final AuthUser authUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authUser.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return authUser.getPassword();
    }

    @Override
    public String getUsername() {
        return authUser.getUsername();
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
        return true;
    }
}