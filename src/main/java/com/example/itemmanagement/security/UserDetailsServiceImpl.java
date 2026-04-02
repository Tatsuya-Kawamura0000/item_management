package com.example.itemmanagement.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.itemmanagement.entity.Users;
import com.example.itemmanagement.mapper.UsersMapper;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = usersMapper.findByLoginId(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new LoginUser(user);
               
    }
}