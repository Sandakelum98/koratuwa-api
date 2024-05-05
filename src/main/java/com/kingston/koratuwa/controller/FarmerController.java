package com.kingston.koratuwa.controller;

import com.kingston.koratuwa.dto.request.farmer.FarmerLoginRequest;
import com.kingston.koratuwa.dto.request.farmer.FarmerRegisterRequest;
import com.kingston.koratuwa.service.FarmerService;
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

    @GetMapping("/get-farmer-by-id/{farmerId}")
    public ResponseEntity getFarmerById(HttpServletRequest request,
                                        @PathVariable Integer farmerId) {
        System.out.println("getFarmerById - controller");
        return farmerService.getFarmerById(request, farmerId);
    }

    @PostMapping("/update")
    public ResponseEntity update(HttpServletRequest request,
                                 @RequestBody FarmerRegisterRequest registerRequest) {
        return farmerService.update(request, registerRequest);
    }

}
