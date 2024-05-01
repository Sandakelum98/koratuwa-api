package com.kingston.koratuwa.service;

import com.kingston.koratuwa.dto.request.farmer.FarmerLoginRequest;
import com.kingston.koratuwa.dto.request.farmer.FarmerRegisterRequest;
import com.kingston.koratuwa.dto.request.user.UserLoginRequest;
import com.kingston.koratuwa.dto.request.user.UserRegisterRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface FarmerService {

    ResponseEntity register(HttpServletRequest request, FarmerRegisterRequest farmerRegisterRequest);

    ResponseEntity login(HttpServletRequest request, FarmerLoginRequest farmerRegisterRequest);

    ResponseEntity update(HttpServletRequest request, FarmerRegisterRequest farmerRegisterRequest);

}
