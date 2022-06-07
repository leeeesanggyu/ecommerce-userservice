package com.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.domain.dto.LoginReq;
import com.userservice.domain.dto.UserDto;
import com.userservice.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Environment env;
    private final UserService userService;

    public AuthenticationFilter(Environment env, UserService userService, AuthenticationManager authenticationManager) {
        this.env = env;
        this.userService = userService;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            final LoginReq creds = new ObjectMapper().readValue(request.getInputStream(), LoginReq.class);
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>());
            return getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) {
        final String username = ((User) authResult.getPrincipal()).getUsername();
        final UserDto userDetails = userService.getUserDetailsByEmail(username);
        final String token = tokenBuilder(userDetails.getUserId());

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());
        log.info("login success ! -> {}, {}", username, token);
    }

    private Date tokenExpiration() {
        return new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration")));
    }

    private String tokenBuilder(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(tokenExpiration())
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
    }
}
