package com.seatly.seatly;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.global.security.JwtTokenProvider;
import com.seatly.seatly.store.UserStoreService;

import lombok.extern.slf4j.Slf4j;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class ImageControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @Autowired
  JwtTokenProvider jwtProvider;

  @Autowired
  UserStoreService userStoreService;

  private String token;
  private String imageId;

  private static final String FILE_PATH = "/images/jeonghyolee.jpeg";

  @BeforeAll
  void setup() throws Exception {
    signUpUser();
    uploadImage();
  }

  @AfterAll
  void clear() throws Exception {
    deleteImage();
    deleteUser();
  }

  void uploadImage() throws Exception {
    byte[] imageBytes = getClass().getResourceAsStream(FILE_PATH)
        .readAllBytes();

    MockMultipartFile file = new MockMultipartFile(
        "file", // @RequestParam 이름
        FILE_PATH, // 파일명
        MediaType.IMAGE_JPEG_VALUE,
        imageBytes);

    MvcResult result = mockMvc.perform(
        multipart("/api/images/upload")
            .file(file)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andReturn();
    imageId = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    assertNotNull(imageId);
  }

  @Test
  void testGetImage() throws Exception {
    MvcResult result = mockMvc.perform(
        get("/api/images/{id}", imageId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG))
        .andReturn();

    byte[] responseBytes = result.getResponse().getContentAsByteArray();
    assertTrue(responseBytes.length > 0);

    byte[] original = getClass().getResourceAsStream(FILE_PATH).readAllBytes();
    assertArrayEquals(original, responseBytes);
  }

  void deleteImage() throws Exception {
    mockMvc.perform(
        delete("/api/images/{id}", imageId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk());

    mockMvc.perform(
        get("/api/images/{id}", imageId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  void signUpUser() throws Exception {
    UserPost userPost = new UserPost();
    String email = RandomUtil.randomEmail();
    userPost.setEmail(email);
    userPost.setPassword(RandomUtil.randomPassword());
    String username = RandomUtil.randomName();
    userPost.setName(username);
    userPost.setPhone(RandomUtil.randomPhoneNumber());
    userPost.setRole(UserRole.USER);

    mockMvc.perform(
        post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPost)))
        .andExpect(status().isCreated());

    User user = userStoreService.findByEmailOrNull(email);
    assertNotNull(user);
    assertNotNull(user.getId());

    token = jwtProvider.createAccessToken(user.getId(), username, UserRole.USER.name());
  }

  void deleteUser() throws Exception {
    mockMvc.perform(
        delete("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isNoContent());
  }

}
