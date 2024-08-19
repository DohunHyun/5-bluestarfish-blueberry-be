package com.bluestarfish.blueberry.auth.controller;

import com.bluestarfish.blueberry.auth.dto.LoginRequest;
import com.bluestarfish.blueberry.auth.dto.LoginSuccessResult;
import com.bluestarfish.blueberry.auth.dto.MailAuthRequest;
import com.bluestarfish.blueberry.auth.service.AuthService;
import com.bluestarfish.blueberry.common.dto.ApiSuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.bluestarfish.blueberry.common.handler.ResponseHandler.handleSuccessResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiSuccessResponse<?> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) throws UnsupportedEncodingException {

        LoginSuccessResult loginSuccessResult = authService.login(loginRequest);

        Cookie accessTokenCookie = new Cookie("Authorization", URLEncoder.encode(loginSuccessResult.getAccessToken(), "UTF-8"));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 24);

        Cookie userIdCookie = new Cookie("user-id", String.valueOf(loginSuccessResult.getUserId()));
        userIdCookie.setHttpOnly(false);
        userIdCookie.setSecure(true);
        userIdCookie.setPath("/");
        userIdCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(accessTokenCookie);
        response.addCookie(userIdCookie);

        return handleSuccessResponse(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ApiSuccessResponse<?> logout(
            @CookieValue("user-id") Long userId
    ) {
        authService.logout(userId);
        return handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/mail")
    public ApiSuccessResponse<?> sendMail(
            @RequestBody MailAuthRequest mailAuthRequest
    ) {
        authService.sendMail(mailAuthRequest);
        return handleSuccessResponse(HttpStatus.CREATED);
    }
}
