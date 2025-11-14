package com.example.test.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import com.example.test.core.error.BadRequestException;
import com.example.test.core.error.UnauthorizedException;
import com.example.test.domain.User;
import com.example.test.domain.request.RequestLoginDTO;
import com.example.test.domain.request.user.RegisterUserDTO;
import com.example.test.domain.response.ResponseLoginDTO;
import com.example.test.service.AuthService;
import com.example.test.service.JwtService;
import com.example.test.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

        private final JwtService jwtService;

        private final UserService userService;

        private final AuthService authService;

        public AuthController(JwtService jwtService,
                        UserService userService, AuthService authService) {
                this.jwtService = jwtService;
                this.userService = userService;
                this.authService = authService;

        }

        @PostMapping("/login")
        public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody RequestLoginDTO loginDto) {

                ResponseLoginDTO response = this.authService.login(loginDto);
                User current = this.userService.getUserByEmail(loginDto.getEmail());

                String refreshToken = this.jwtService.createRefreshToken(current.getEmail());
                this.userService.updateUserToken(loginDto.getEmail(), refreshToken);

                ResponseCookie resCookie = ResponseCookie.from("refresh_token", refreshToken).httpOnly(true)
                                .path("/")
                                .maxAge(60 * 60 * 24 * 30).build();

                ResponseCookie accessTokenCookie = ResponseCookie
                                .from("access_token", String.valueOf(response.getAccessToken()))
                                .path("/")
                                .maxAge(60 * 60 * 24 * 30)
                                .build();

                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                                .body(response);

        }

        @PostMapping("/register")
        public ResponseEntity<RegisterUserDTO> register(@RequestBody @Valid RegisterUserDTO entity)
                        throws BadRequestException {

                return new ResponseEntity<>(this.userService.register(entity), HttpStatus.OK);
        }

        @GetMapping("/account")
        public ResponseEntity<ResponseLoginDTO.GetAccount> getAccountUser() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User current = this.userService.getUserByEmail(email);
                ResponseLoginDTO.GetAccount response = new ResponseLoginDTO.GetAccount();
                response.setUser(current.convertResponseUserDto());
                return ResponseEntity.ok().body(response);
        }

        @GetMapping("/refresh")
        public ResponseEntity<ResponseLoginDTO> refreshToken(
                        @CookieValue(name = "refresh_token", required = false) String refreshToken)
                        throws UnauthorizedException {
                if (refreshToken == null) {
                        throw new UnauthorizedException("Invalid refresh token");
                }

                Jwt decoded = this.jwtService.checkRefreshToken(refreshToken);
                String email = decoded.getSubject();
                User current = this.userService.getUserByEmail(email);

                if (current == null) {
                        throw new UnauthorizedException("Invalid refresh token");
                }

                if (current.getRefreshToken().equals(refreshToken) == false) {
                        throw new UnauthorizedException("Invalid refresh token");
                }

                String accessToken = this.jwtService.createAccessToken(email, current);

                ResponseLoginDTO response = new ResponseLoginDTO(accessToken, current.convertResponseUserDto());

                String newRefreshToken = this.jwtService.createRefreshToken(email);
                this.userService.updateUserToken(email, newRefreshToken);

                ResponseCookie resCookie = ResponseCookie.from("refresh_token", newRefreshToken).httpOnly(true)
                                .path("/")
                                .maxAge(60 * 60 * 24 * 30).build();

                return ResponseEntity

                                .ok().header(HttpHeaders.SET_COOKIE, resCookie.toString())
                                .body(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                this.userService.updateUserToken(email, null);

                ResponseCookie resCookie = ResponseCookie.from("refresh_token", null).httpOnly(true).path("/")
                                .maxAge(0).build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(null);

        }

}
