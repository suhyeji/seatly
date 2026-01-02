package com.seatly.seatly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.user.UserInfoDetail;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.global.security.JwtTokenProvider;
import com.seatly.seatly.store.UserStoreService;

import lombok.extern.slf4j.Slf4j;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @Autowired
  JwtTokenProvider jwtProvider;

  @Autowired
  UserStoreService userStoreService;

  private Long userId;
  private String username;
  private String email;
  private String password;
  private String phone;
  private String token;

  @BeforeAll
  void setup() throws Exception {
    testSignUpUser();
  }

  @AfterAll
  void clear() throws Exception {
    testDeleteUser();
    SecurityContextHolder.clearContext();
  }

  void testSignUpUser() throws Exception {
    UserPost userPost = new UserPost();
    email = randomEmail();
    userPost.setEmail(email);
    password = randomPassword();
    userPost.setPassword(password);
    username = randomName();
    userPost.setName(username);
    phone = randomPhoneNumber();
    userPost.setPhone(phone);
    userPost.setRole(UserRole.USER);

    mockMvc.perform(
        post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPost)))
        .andExpect(status().isCreated());

    User user = userStoreService.findByEmailOrNull(email);
    assertNotNull(user);
    assertEquals(username, user.getName());
    assertEquals(email, user.getEmail());
    assertEquals(phone, user.getPhone());
    assertNotNull(user.getId());
    userId = user.getId();

    token = jwtProvider.createAccessToken(userId, username, UserRole.USER.name());
  }

  void testDeleteUser() throws Exception {
    mockMvc.perform(
        delete("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(1)
  void testGetUserInfoDetail() throws Exception {
    MvcResult result = mockMvc.perform(
        get("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();

    UserInfoDetail response = mapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
        UserInfoDetail.class);

    assertNotNull(response);
    assertEquals(email, response.getEmail());
    assertEquals(username, response.getName());
    assertEquals(phone, response.getPhone());
    assertNotNull(response.getPhone());
    assertNotNull(response.getFavoriteCafeIds());
    assertNotNull(response.getSessions());
    assertNotNull(response.getTimePasses());
    assertEquals(UserRole.USER, response.getRole());
  }

  @Test
  @Order(2)
  void testUpdateUserInfo() throws Exception {
    UserPatch userPatch = new UserPatch();
    username = randomName();
    userPatch.setName(username);
    phone = randomPhoneNumber();
    userPatch.setPhone(phone);

    mockMvc.perform(
        patch("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPatch)))
        .andExpect(status().isOk());

    testGetUserInfoDetail();
  }

  private String randomEmail() {
    return "test+" + UUID.randomUUID() + "@example.com";
  }

  private String randomPassword() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    int length = 12;
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  private String randomName() {
    String[] lastNames = {
        "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"
    };
    String[] firstNames = {
        "서준", "민준", "도윤", "예준", "시우",
        "서연", "지우", "하윤", "지민", "채원"
    };
    SecureRandom random = new SecureRandom();
    return lastNames[random.nextInt(lastNames.length)]
        + firstNames[random.nextInt(firstNames.length)];
  }

  private String randomPhoneNumber() {
    SecureRandom random = new SecureRandom();

    int mid = random.nextInt(9000) + 1000; // 1000~9999
    int last = random.nextInt(9000) + 1000; // 1000~9999

    return "010-" + mid + "-" + last;
  }

}
