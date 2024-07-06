package com.events.service;

import com.events.DTO.RegistrationUserDto;
import com.events.DTO.UserDto;
import com.events.JWT.*;
import com.events.entity.User;
import com.events.exceptions.AppError;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService<JwtAuthentication> {
    private final UserService userService;

    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        final User user;
        try {
            user = userService.findByLogin(authRequest.getLogin())
                    .orElseThrow(() -> new AuthException("Пользователь не найден"));
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
        user.getPassword();

        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getLogin(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            try {
                throw new AuthException("Неправильный пароль");
            } catch (AuthException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user;
                try {
                    user = userService.findByLogin(login)
                            .orElseThrow(() -> new AuthException("Пользователь не найден"));
                } catch (AuthException e) {
                    throw new RuntimeException(e);
                }
                final String accessToken = jwtProvider.generateAccessToken(user);
                System.out.println("Отдаем новый акцес токен = " + accessToken);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user;
                try {
                    user = userService.findByLogin(login)
                            .orElseThrow(() -> new AuthException("Пользователь не найден"));
                } catch (AuthException e) {
                    throw new RuntimeException(e);
                }
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        try {
            throw new AuthException("Невалидный JWT токен");
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }


    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if(userService.findByLogin(registrationUserDto.getLogin()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Такой пользователь уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getLogin(), user.getPassword(), user.getRoles()));
    }


}
