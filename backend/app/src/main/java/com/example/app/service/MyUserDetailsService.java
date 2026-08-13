package com.example.app.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.app.entity.User;
import com.example.app.entity.UserPrinciple;
import com.example.app.repo.UserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService{
    
    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException{
        User user = userRepo.findByUsernameOrEmailOrPhone(loginInput, loginInput, loginInput)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserPrinciple(user);
    }
}
