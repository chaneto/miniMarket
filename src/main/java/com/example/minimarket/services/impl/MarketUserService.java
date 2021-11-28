package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MarketUserService implements UserDetailsService {

    private final UserRepository userRepository;

    public MarketUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        UserEntity userEntity = this.userRepository.findByUsername(name);
        if(userEntity == null){
            throw new UsernameNotFoundException("User with" + name + " was not found!!!");
        }
        return mapToUserDetail(userEntity);
    }

    private UserDetails mapToUserDetail(UserEntity userEntity){
        GrantedAuthority authorities = new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().getUserRole().name());
       // List<GrantedAuthority> authorities = userEntity
              // .getRoles()
              // .stream()
              // .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getUserRole().name())).collect(Collectors.toList());
        UserDetails result = new User(userEntity.getUsername(), userEntity.getPassword(), Collections.singleton(authorities));
        return result;
    }
}
