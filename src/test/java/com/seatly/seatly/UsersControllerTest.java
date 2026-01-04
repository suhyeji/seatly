package com.seatly.seatly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.global.security.JwtTokenProvider;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;

import lombok.extern.slf4j.Slf4j;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class UsersControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @Autowired
  JwtTokenProvider jwtProvider;

  @Autowired
  UserStoreService userStoreService;

  @Autowired
  StudyCafeStoreService studyCafeStoreService;

  @Autowired
  UserTimePassStoreService userTimePassStoreService;

  private String adminToken;

  private User user1;
  private User user2;
  private Long studyCafeId1;
  private Long studyCafeId2;
  private Long time1;
  private Long time2;

  @BeforeAll
  void setup() throws Exception {
    signUpAdminUser();

    user1 = signUpUser();
    user2 = signUpUser();

    studyCafeId1 = createStudyCafe();
    studyCafeId2 = createStudyCafe();

    time1 = addUserTimePass(user1.getId(), studyCafeId1);
    time2 = addUserTimePass(user2.getId(), studyCafeId1);
    addUserTimePass(user2.getId(), studyCafeId2);
  }

  @AfterAll
  void clear() throws Exception {
    deleteAdminUser();
    userTimePassStoreService.deleteById(new UserTimePassId(studyCafeId1, user1.getId()));
    userTimePassStoreService.deleteById(new UserTimePassId(studyCafeId1, user2.getId()));
    userTimePassStoreService.deleteById(new UserTimePassId(studyCafeId2, user2.getId()));
    userStoreService.deleteById(user1.getId());
    userStoreService.deleteById(user2.getId());
    studyCafeStoreService.deleteById(studyCafeId1);
    studyCafeStoreService.deleteById(studyCafeId2);
  }

  void signUpAdminUser() throws Exception {
    UserPost userPost = new UserPost();
    String email = RandomUtil.randomEmail();
    userPost.setEmail(email);
    userPost.setPassword(RandomUtil.randomPassword());
    String username = RandomUtil.randomName();
    userPost.setName(username);
    userPost.setPhone(RandomUtil.randomPhoneNumber());
    userPost.setRole(UserRole.ADMIN);

    mockMvc.perform(
        post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPost)))
        .andExpect(status().isCreated());

    User user = userStoreService.findByEmailOrNull(email);
    assertNotNull(user);
    assertNotNull(user.getId());

    adminToken = jwtProvider.createAccessToken(user.getId(), username, UserRole.ADMIN.name());
  }

  @Test
  void testGetUserTimesInfo() throws Exception {
    MvcResult result = mockMvc.perform(get("/api/users")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .param("studyCafeId", studyCafeId1.toString()))
        .andExpect(status().isOk())
        .andReturn();

    String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    List<UserTimePassInfo> actual = mapper.readValue(body,
        new TypeReference<List<UserTimePassInfo>>() {
        });

    UserTimePassInfo timePass1 = new UserTimePassInfo();
    timePass1.setUserId(user1.getId());
    timePass1.setUserName(user1.getName());
    timePass1.setStudyCafeId(studyCafeId1);
    timePass1.setLeftTime(time1);
    timePass1.setTotalTime(time1);

    UserTimePassInfo timePass2 = new UserTimePassInfo();
    timePass2.setUserId(user2.getId());
    timePass2.setUserName(user2.getName());
    timePass2.setStudyCafeId(studyCafeId1);
    timePass2.setLeftTime(time2);
    timePass2.setTotalTime(time2);

    List<UserTimePassInfo> expected = List.of(timePass1, timePass2);

    assertTrue(
        actual.containsAll(expected) && expected.containsAll(actual));
  }

  User signUpUser() throws Exception {
    UserPost userPost = new UserPost();
    userPost.setEmail(RandomUtil.randomEmail());
    userPost.setPassword(RandomUtil.randomPassword());
    userPost.setName(RandomUtil.randomName());
    userPost.setPhone(RandomUtil.randomPhoneNumber());
    userPost.setRole(UserRole.USER);

    mockMvc.perform(
        post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPost)))
        .andExpect(status().isCreated());
    User user = userStoreService.findByEmailOrNull(userPost.getEmail());
    assertNotNull(user);
    return user;
  }

  @Test
  void testGetUserInfo() throws Exception {
    MvcResult result = mockMvc.perform(get("/api/users/" + user1.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andReturn();

    String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    UserInfo actual = mapper.readValue(body, UserInfo.class);

    UserInfo expected = new UserInfo();
    expected.setEmail(user1.getEmail());
    expected.setName(user1.getName());
    expected.setPhone(user1.getPhone());
    expected.setImageUrl(user1.getImageUrl());

    assertEquals(expected, actual);
  }

  Long createStudyCafe() throws Exception {
    StudyCafeDetailPost studyCafe = new StudyCafeDetailPost();
    studyCafe.setName(RandomUtil.randomStudyCafeName());
    studyCafe.setAddress(RandomUtil.randomAddress());
    studyCafe.setPhoneNumber(RandomUtil.randomPhoneNumber());
    studyCafe.setFacilities(List.of(Facility.AIR_CONDITIONING, Facility.WIFI));
    studyCafe.setOpeningHours("08:00 - 23:00");

    MvcResult result = mockMvc.perform(post("/api/study-cafes")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(studyCafe)))
        .andExpect(status().isCreated())
        .andReturn();

    String body = result.getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    return mapper.readValue(body, Long.class);
  }

  Long addUserTimePass(Long userId, Long studyCafeId) throws Exception {
    Long time = ThreadLocalRandom.current().nextLong(1800, 10800);
    mockMvc.perform(post("/api/users/" + userId + "/time")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .param("studyCafeId", studyCafeId.toString())
        .param("time", time.toString()))
        .andExpect(status().isOk());
    return time;
  }

  void deleteAdminUser() throws Exception {
    mockMvc.perform(
        delete("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isNoContent());
  }

}
