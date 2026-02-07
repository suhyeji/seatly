package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.timepass.TimePassRequest;
import com.seatly.seatly.global.exception.ForbiddenException;
import com.seatly.seatly.global.exception.UnauthorizedException;
import com.seatly.seatly.service.TimePassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/time-passes")
@RequiredArgsConstructor
public class TimePassController {

  private final TimePassService timePassService;

  @GetMapping("/request")
  public List<TimePassRequest> getRequests(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long studyCafeId) {
    validateUser(user);
    validateAdmin(user);
    return timePassService.getRequests(studyCafeId);
  }

  @PostMapping("/request")
  public void requestAddTimePass(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long studyCafeId, @RequestParam Long time) {
    validateUser(user);
    timePassService.requestAddTimePass(user.getId(), studyCafeId, time);
  }

  @PostMapping("/request/accept")
  public void acceptRequest(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long requestId) {
    validateUser(user);
    validateAdmin(user);
    timePassService.acceptRequest(requestId);
  }

  @PostMapping("/request/reject")
  public void rejectRequest(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long requestId) {
    validateUser(user);
    validateAdmin(user);
    timePassService.rejectRequest(requestId);
  }

  private void validateUser(CustomUserDetails user) {
    if (user == null) {
      throw new UnauthorizedException("Authentication token is required.");
    }
  }

  private void validateAdmin(CustomUserDetails user) {
    if (!user.isAdmin()) {
      throw new ForbiddenException("관리자 권한이 필요합니다.");
    }
  }

}
