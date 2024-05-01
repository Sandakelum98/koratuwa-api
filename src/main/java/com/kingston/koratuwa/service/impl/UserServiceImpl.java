package com.kingston.koratuwa.service.impl;

import com.kingston.koratuwa.config.JwtService;
import com.kingston.koratuwa.dto.request.user.UserLoginRequest;
import com.kingston.koratuwa.dto.request.user.UserRegisterRequest;
import com.kingston.koratuwa.dto.response.user.UserRegisterResponse;
import com.kingston.koratuwa.constants.Constants;
import com.kingston.koratuwa.entity.UserBean;
import com.kingston.koratuwa.repo.UserRepo;
import com.kingston.koratuwa.service.UserService;
import com.kingston.koratuwa.utility.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;

import javax.servlet.http.HttpServletRequest;

import static com.kingston.koratuwa.enums.RoleUtils.getRoleFromInt;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity register(HttpServletRequest request, UserRegisterRequest userRegisterRequest) {
        logger.info("Received request: {}", request.getRequestURL());

        try {

            var user = UserBean.builder()
                    .firstName(userRegisterRequest.getFirstName())
                    .lastName(userRegisterRequest.getLastName())
                    .userName(userRegisterRequest.getUserName())
                    .email(userRegisterRequest.getEmail())
                    .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                    .role(getRoleFromInt(userRegisterRequest.getType()))
                    .build();

            UserBean savedUser = userRepo.save(user);

            var jwtToken = jwtService.generateToken(user);

            UserRegisterResponse userRegisterResponse = new UserRegisterResponse(savedUser, jwtToken);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseWrapper<>().responseOk(Constants.RESPONSE_OK, userRegisterResponse));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseWrapper<>().responseFail(Constants.USER_ALREADY_EXISTS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail(Constants.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public ResponseEntity login(HttpServletRequest request, UserLoginRequest userLoginRequest) {
        logger.info("Received request: {}", request.getRequestURL());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getUserName(), userLoginRequest.getPassword())
            );

            UserBean user = userRepo.getByUserName(userLoginRequest.getUserName());

            if (user == null) {
                throw new UsernameNotFoundException("User Not Found");
            }

            var jwtToken = jwtService.generateToken(user);

            UserRegisterResponse userRegisterResponse = UserRegisterResponse.builder()
                    .token(jwtToken)
                    .user(user)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseWrapper<>().responseOk(Constants.RESPONSE_OK, userRegisterResponse));

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
