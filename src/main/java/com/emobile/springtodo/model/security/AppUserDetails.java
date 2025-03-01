package com.emobile.springtodo.model.security;

import com.emobile.springtodo.model.entity.User;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * {@link UserDetails} realization with {@link User} entity.
 * {@link #isAccountNonExpired()} always true.
 * {@link #isAccountNonLocked()} always true.
 * {@link #isCredentialsNonExpired()} always true.
 * {@link #isEnabled()} always true.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
public class AppUserDetails implements UserDetails {
    /**
     * Entity to implement UserDetails.
     */
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    /**
     * User.id getter.
     *
     * @return User.id
     */
    public Long getUserId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
