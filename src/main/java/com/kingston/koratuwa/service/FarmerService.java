package com.kingston.koratuwa.service;

import com.kingston.koratuwa.dto.request.farmer.FarmerLoginRequest;
import com.kingston.koratuwa.dto.request.farmer.FarmerRegisterRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FarmerService {

    ResponseEntity register(HttpServletRequest request, FarmerRegisterRequest farmerRegisterRequest);

    ResponseEntity login(HttpServletRequest request, FarmerLoginRequest farmerRegisterRequest);

    ResponseEntity getFarmerById(HttpServletRequest request, Integer farmerId);

    ResponseEntity update(HttpServletRequest request, FarmerRegisterRequest farmerRegisterRequest);

}
