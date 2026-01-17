package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.global.exception.UnauthorizedException;
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
    validateUser(user);
    return service.startSession(user.getId(), id);
  }

  @DeleteMapping("/{id}")
  public void endSession(@AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    validateUser(user);
    service.endSession(user.getId(), user.isAdmin(), id);
  }

  @PostMapping("/assign")
  public SessionInfo assignSession(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long seatId) {
    validateUser(user);
    return service.assignSeat(user.getId(), seatId);
  }

  @PostMapping("/auto-assign")
  public SessionInfo autoAssignSession(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long studyCafeId) {
    validateUser(user);
    return service.autoAssignSeat(user.getId(), studyCafeId);
  }

  private void validateUser(CustomUserDetails user) {
    if (user == null) {
      throw new UnauthorizedException("Authentication token is required.");
    }
  }
}
