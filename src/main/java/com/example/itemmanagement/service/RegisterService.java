package com.example.itemmanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemmanagement.entity.Users;
import com.example.itemmanagement.form.RegisterForm;
import com.example.itemmanagement.mapper.UsersMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {
	
	    private final UsersMapper usersMapper;
	    private final PasswordEncoder passwordEncoder;

	    @Transactional
	    public void register(RegisterForm form) {
	        // パスワード暗号化
	        String encodedPassword = passwordEncoder.encode(form.getPassword());

	        // Entityにセット
	        Users user = new Users();
	        user.setLoginId(form.getLoginId());
	        user.setPassword(encodedPassword);

	        // DB保存
	        usersMapper.insertUser(user);
	    }

}
