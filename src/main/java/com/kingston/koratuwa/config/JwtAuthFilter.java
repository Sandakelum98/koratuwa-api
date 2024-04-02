package com.kingston.koratuwa.config;

import com.kingston.koratuwa.constants.Constants;
import com.kingston.koratuwa.utility.ResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            jwt = authHeader.substring(7);
            userName = jwtService.extractUserName(jwt);

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);


                if (jwtService.tokenValidate(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException eje) {
            logger.error("Expired JWT Exception: {}", eje.getMessage());
            handleErrorResponse(response, HttpStatus.UNAUTHORIZED, Constants.TOKEN_EXPIRED);
            return;
        } catch (SignatureException se) {
            logger.error("JWT Signature Exception: {}", se.getMessage());
            handleErrorResponse(response, HttpStatus.UNAUTHORIZED, Constants.INVALID_TOKEN_SIGNATURE);
            return;
        } catch (UsernameNotFoundException unfe) {
            logger.error("User Not Found Exception: {}", unfe.getMessage());
            handleErrorResponse(response, HttpStatus.UNAUTHORIZED, Constants.USER_NOT_FOUND);
            return;
        } catch (AccessDeniedException ade) {
            logger.error("Access Denied Exception: {}", ade.getMessage());
            handleErrorResponse(response, HttpStatus.FORBIDDEN, Constants.USER_FORBIDDEN);
            return;
        } catch (JwtException je) {
            logger.error("JWT Exception: {}", je.getMessage());
            handleErrorResponse(response, HttpStatus.UNAUTHORIZED, Constants.INVALID_JWT_TOKEN);
            return;
        } catch (Exception ex) {
            logger.error("Exception: {}", ex.getMessage());
            handleErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, Constants.INTERNAL_SERVER_ERROR);
            return;
        }


    }

    private void handleErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        // use responseWrapper to format the error response
        ResponseWrapper<String> errorResponse = new ResponseWrapper<String>().responseFail(message);
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
