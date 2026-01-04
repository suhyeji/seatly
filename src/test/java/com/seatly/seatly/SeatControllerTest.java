package com.seatly.seatly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.dto.seat.SeatPatch;
import com.seatly.seatly.dto.seat.SeatPost;
import com.seatly.seatly.global.security.JwtTokenProvider;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeatControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  SeatStoreService seatStoreService;

  @Autowired
  StudyCafeStoreService studyCafeStoreService;

  @Autowired
  UserStoreService userStoreService;

  @Autowired
  JwtTokenProvider jwtProvider;

  private User admin;
  private String adminToken;
  private User normalUser;
  private String normalToken;

  @BeforeEach
  void setUp() {
    admin = userStoreService.save(RandomUtil.randomAdminUser());
    adminToken = jwtProvider.createAccessToken(
        admin.getId(), admin.getName(), admin.getRole().name());

    normalUser = userStoreService.save(RandomUtil.randomNormalUser());
    normalToken = jwtProvider.createAccessToken(
        normalUser.getId(), normalUser.getName(), normalUser.getRole().name());
  }

  // 1. GET /api/study-cafes/{studyCafeId}/seats
  @Test
  void getSeats_success_when_admin() throws Exception {
    // given
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    seatStoreService.save(RandomUtil.randomSeat(cafe));
    seatStoreService.save(RandomUtil.randomSeat(cafe));
    seatStoreService.save(RandomUtil.randomSeat(cafe));

    // when & then
    mockMvc.perform(get("/api/study-cafes/" + cafe.getId() + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void getSeats_forbidden_when_not_admin() throws Exception {
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    mockMvc.perform(get("/api/study-cafes/" + cafe.getId() + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void addSeat_success_when_admin() throws Exception {
    // given
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    Seat seat1 = RandomUtil.randomSeat(cafe);
    Seat seat2 = RandomUtil.randomSeat(cafe);
    List<SeatPost> body = List.of(
        new SeatPost(seat1.getName(), seat1.getStatus(), seat1.getPosition()),
        new SeatPost(seat2.getName(), seat2.getStatus(), seat2.getPosition()));

    // when
    mockMvc.perform(post("/api/study-cafes/" + cafe.getId() + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk());

    // then
    List<Seat> seats = seatStoreService.findAllByStudyCafeId(cafe.getId());
    assertThat(seats).hasSize(2);
    assertThat(seats.get(0).getName()).isEqualTo(seat1.getName());
    assertThat(seats.get(1).getName()).isEqualTo(seat2.getName());

  }

  @Test
  void addSeat_forbidden_when_not_admin() throws Exception {
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    mockMvc.perform(post("/api/study-cafes/" + cafe.getId() + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content("[]"))
        .andExpect(status().isForbidden());
  }

  // =========================
  // PATCH /seats
  // =========================
  @Test
  void updateSeats_success_when_admin() throws Exception {
    // given
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    Seat seat = seatStoreService.save(RandomUtil.randomSeat(cafe));

    SeatPatch patch = new SeatPatch(seat.getId(), "변경된 좌석", seat.getStatus(), seat.getPosition());

    // when
    mockMvc.perform(patch("/api/study-cafes/" + cafe.getId() + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(List.of(patch))))
        .andExpect(status().isOk());

    // then
    Seat updated = seatStoreService.findByIdOrNull(seat.getId());
    assertThat(updated.getName()).isEqualTo("변경된 좌석");
  }

  // =========================
  // DELETE /seats/{id}
  // =========================
  @Test
  void deleteSeat_success_when_admin() throws Exception {
    // given
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());
    Seat seat = seatStoreService.save(RandomUtil.randomSeat(cafe));

    // sanity
    assertThat(seatStoreService.findByIdOrNull(seat.getId())).isNotNull();

    // when
    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/seats/" + seat.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isOk());

    // then
    assertThat(seatStoreService.findByIdOrNull(seat.getId())).isNull();
  }

  @Test
  void deleteSeat_forbidden_when_not_admin() throws Exception {
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());
    Seat seat = seatStoreService.save(RandomUtil.randomSeat(cafe));

    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/seats/" + seat.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isForbidden());
  }

}
