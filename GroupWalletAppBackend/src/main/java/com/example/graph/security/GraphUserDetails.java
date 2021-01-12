package com.example.graph.security;

import com.example.graph.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphUserDetails implements UserDetails {
    private final User graphUser;

    public GraphUserDetails(User graphUser) {
        this.graphUser = graphUser;
    }

    public User getGraphUser() {
        return graphUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        return authorities;
    }

    @Override
    public String getPassword() {
        return graphUser.getPassword();
    }

    @Override
    public String getUsername() {
        return graphUser.getEmail();
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
