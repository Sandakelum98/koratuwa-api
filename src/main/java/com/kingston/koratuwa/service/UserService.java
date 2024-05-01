package com.kingston.koratuwa.service;

import com.kingston.koratuwa.dto.request.user.UserLoginRequest;
import com.kingston.koratuwa.dto.request.user.UserRegisterRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    ResponseEntity register(HttpServletRequest request, UserRegisterRequest userRegisterRequest);

    ResponseEntity login(HttpServletRequest request, UserLoginRequest userLoginRequest);

}
