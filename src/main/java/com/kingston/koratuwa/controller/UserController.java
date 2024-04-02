package com.kingston.koratuwa.controller;

import com.kingston.koratuwa.dto.request.user.UserLoginRequest;
import com.kingston.koratuwa.dto.request.user.UserRegisterRequest;
import com.kingston.koratuwa.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity register(HttpServletRequest request,
                                   @RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(request, userRegisterRequest);
    }

    @PostMapping("/login")
    public ResponseEntity login(HttpServletRequest request,
                                @RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(request, userLoginRequest);
    }

}
