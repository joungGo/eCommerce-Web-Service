package com.example.ecommercewebservice.domain.user.controller;

import com.example.ecommercewebservice.domain.user.dto.LoginRequest;
import com.example.ecommercewebservice.domain.user.dto.SignupRequest;
import com.example.ecommercewebservice.domain.user.entity.User;
import com.example.ecommercewebservice.domain.user.entity.UserRole;
import com.example.ecommercewebservice.domain.user.repository.UserRepository;
import com.example.ecommercewebservice.domain.user.service.UserService;
import com.example.ecommercewebservice.global.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String testToken;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        User testUser = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("Test1234!"))
                .username("testuser")
                .roles(Collections.singletonList(UserRole.USER.getRole()))
                .build();
        userRepository.save(testUser);

        // 테스트용 인증 객체 생성
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(UserRole.USER.getRole())
        );
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(),
                testUser.getPassword(),
                authorities
        );

        // 테스트용 토큰 생성
        testToken = jwtTokenProvider.createToken(authentication);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("NewPass123!");
        signupRequest.setUsername("newuser");
        signupRequest.setPhoneNumber("010-1234-5678");
        signupRequest.setAddress("서울시 강남구");

        // when
        ResultActions result = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Test1234!");

        // when
        ResultActions result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("로그인이 완료되었습니다."))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_success() throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer " + testToken));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 접근 테스트")
    void unauthorized_access() throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/users/logout")
                .header("Authorization", "Bearer invalid_token"));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다. 로그인 후 다시 시도해주세요."))
                .andExpect(jsonPath("$.path").value("/api/users/logout"));
    }
}