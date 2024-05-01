package com.kingston.koratuwa.controller;

import com.kingston.koratuwa.dto.request.farmer.FarmerLoginRequest;
import com.kingston.koratuwa.dto.request.farmer.FarmerRegisterRequest;
import com.kingston.koratuwa.dto.request.user.UserLoginRequest;
import com.kingston.koratuwa.dto.request.user.UserRegisterRequest;
import com.kingston.koratuwa.service.FarmerService;
import com.kingston.koratuwa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @PostMapping("/register")
    public ResponseEntity register(HttpServletRequest request,
                                   @RequestBody FarmerRegisterRequest registerRequest) {
        System.out.println("register - controller");
        return farmerService.register(request, registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity login(HttpServletRequest request,
                                @RequestBody FarmerLoginRequest farmerLoginRequest) {
        return farmerService.login(request, farmerLoginRequest);
    }

    @PostMapping("/update")
    public ResponseEntity update(HttpServletRequest request,
                                @RequestBody FarmerRegisterRequest registerRequest) {
        return farmerService.update(request, registerRequest);
    }

}
