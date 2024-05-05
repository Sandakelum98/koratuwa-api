package com.kingston.koratuwa.service.impl;

import com.kingston.koratuwa.config.JwtService;
import com.kingston.koratuwa.constants.Constants;
import com.kingston.koratuwa.dto.request.farmer.FarmerLoginRequest;
import com.kingston.koratuwa.dto.request.farmer.FarmerRegisterRequest;
import com.kingston.koratuwa.dto.response.farmer.FarmerRegisterResponse;
import com.kingston.koratuwa.entity.FarmerDetailBean;
import com.kingston.koratuwa.entity.UserBean;
import com.kingston.koratuwa.repo.FarmerRepo;
import com.kingston.koratuwa.repo.UserRepo;
import com.kingston.koratuwa.service.FarmerService;
import com.kingston.koratuwa.utility.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.kingston.koratuwa.enums.RoleUtils.getRoleFromInt;


@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final UserRepo userRepo;
    private final FarmerRepo farmerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final Logger logger = LoggerFactory.getLogger(FarmerServiceImpl.class);

    @Override
    public ResponseEntity register(HttpServletRequest request, FarmerRegisterRequest farmerRegisterRequest) {
        logger.info("Received request: {}", request.getRequestURL());

        if (farmerRegisterRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail(Constants.INVALID_REQUEST_BODY));
        }
        if (farmerRegisterRequest.getFirstName() == null || farmerRegisterRequest.getFirstName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("First name cannot be empty"));
        }
        if (farmerRegisterRequest.getLastName() == null || farmerRegisterRequest.getLastName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("Last name cannot be empty"));
        }
        if (farmerRegisterRequest.getUserName() == null || farmerRegisterRequest.getUserName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("Username cannot be empty"));
        }
        if (farmerRegisterRequest.getEmail() == null || farmerRegisterRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("Email cannot be empty"));
        }
        if (farmerRegisterRequest.getPassword() == null || farmerRegisterRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("Password cannot be empty"));
        }

        try {

            // save farmer on user table
            var user = UserBean.builder()
                    .firstName(farmerRegisterRequest.getFirstName())
                    .lastName(farmerRegisterRequest.getLastName())
                    .userName(farmerRegisterRequest.getUserName())
                    .email(farmerRegisterRequest.getEmail())
                    .password(passwordEncoder.encode(farmerRegisterRequest.getPassword()))
                    .role(getRoleFromInt(farmerRegisterRequest.getType()))
                    .build();
            UserBean savedUser = userRepo.save(user);

            // save the farmer details
            FarmerDetailBean farmerDetailBean = new FarmerDetailBean(0, savedUser);
            farmerRepo.save(farmerDetailBean);

            // response
            var jwtToken = jwtService.generateToken(user);
            FarmerRegisterResponse farmerRegisterResponse = new FarmerRegisterResponse(savedUser, jwtToken);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseWrapper<>().responseOk(Constants.RESPONSE_OK, farmerRegisterResponse));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseWrapper<>().responseFail(Constants.USER_ALREADY_EXISTS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail(Constants.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public ResponseEntity login(HttpServletRequest request, FarmerLoginRequest farmerLoginRequest) {
        logger.info("Received request: {}", request.getRequestURL());

        if (farmerLoginRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail(Constants.INVALID_REQUEST_BODY));
        }
        if (farmerLoginRequest.getUserName() == null || farmerLoginRequest.getUserName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("Username cannot be empty"));
        }
        if (farmerLoginRequest.getPassword() == null || farmerLoginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseWrapper<>().responseFail("Password cannot be empty"));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(farmerLoginRequest.getUserName(), farmerLoginRequest.getPassword())
            );

            UserBean user = userRepo.getByUserName(farmerLoginRequest.getUserName());

            if (user == null) {
                throw new UsernameNotFoundException("User Not Found");
            }

            var jwtToken = jwtService.generateToken(user);

            FarmerRegisterResponse farmerRegisterResponse = FarmerRegisterResponse.builder()
                    .token(jwtToken)
                    .user(user)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseWrapper<>().responseOk(Constants.RESPONSE_OK, farmerRegisterResponse));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>().responseFail(Constants.USER_NOT_FOUND));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseWrapper<>().responseFail(Constants.INVALID_CREDENTIALS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail(Constants.INTERNAL_SERVER_ERROR));
        }
    }

    @PreAuthorize("hasAnyAuthority('FARMER')")
    @Override
    public ResponseEntity getFarmerById(HttpServletRequest request, Integer farmerId) {
        logger.info("Received request: {}", request.getRequestURL());

        try {

            FarmerDetailBean farmerDetail = farmerRepo.getFarmerById(farmerId);
            System.out.println(farmerDetail.toString());

            if (farmerDetail == null) {
                return new ResponseEntity<>("Farmer not found with the id", HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseWrapper<>().responseOk(Constants.RESPONSE_OK, farmerDetail));

        } catch (AccessDeniedException ade) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>().responseFail(Constants.USER_FORBIDDEN));
        } catch (AuthenticationException ae) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseWrapper<>().responseFail(Constants.USER_UNAUTHORIZED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail(Constants.INTERNAL_SERVER_ERROR));
        }

    }

    @PreAuthorize("hasAnyAuthority('FARMER')")
    @Override
    public ResponseEntity update(HttpServletRequest request, FarmerRegisterRequest farmerRegisterRequest) {
        logger.info("Received request: {}", request.getRequestURL());

        try {

            System.out.println("4 XXXX");

            return null;

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>().responseFail(Constants.USER_NOT_FOUND));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseWrapper<>().responseFail(Constants.INVALID_CREDENTIALS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail(Constants.INTERNAL_SERVER_ERROR));
        }
    }

}
