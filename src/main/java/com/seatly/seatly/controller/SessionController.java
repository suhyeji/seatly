package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.service.SessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

  private final SessionService service;

  @GetMapping
  public List<SessionInfo> getSessions(@RequestParam Long studyCafeId) {
    return service.getSessions(studyCafeId);
  }

  @PatchMapping("/{id}/start")
  public SessionInfo startSession(@AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    return service.startSession(user.getId(), id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void endSession(@AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    service.endSession(user.getId(), user.isAdmin(), id);
  }

  @PostMapping("/assign")
  public void assignSession(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long seatId) {
    service.assignSeat(user.getId(), seatId);
  }

  @PostMapping("/auto-assign")
  public void autossignSession(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long studyCafeId) {
    service.autoAssignSeat(user.getId(), studyCafeId);
  }

}
