package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.studycafe.StudyCafeDetail;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummary;
import com.seatly.seatly.dto.studycafe.StudyCafeUsage;
import com.seatly.seatly.global.exception.ForbiddenException;
import com.seatly.seatly.global.exception.UnauthorizedException;
import com.seatly.seatly.service.StudyCafeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/study-cafes")
@RequiredArgsConstructor
public class StudyCafeController {

  private final StudyCafeService studyCafeService;

  @GetMapping
  public List<StudyCafeSummary> getStudySummaries() {
    return studyCafeService.getStudySummaries();
  }

  @GetMapping("/{id}")
  public StudyCafeDetail getStudyCafeDetail(@PathVariable Long id) {
    return studyCafeService.getStudyCafeDetail(id);
  }

  @GetMapping("/admin")
  public List<StudyCafeSummary> getAdminStudyCafeSummaries(
      @AuthenticationPrincipal CustomUserDetails user) {
    validateUser(user);
    validateAdmin(user);
    return studyCafeService.getAdminStudyCafeSummaries(user.getId());
  }

  @GetMapping("/{id}/usage")
  public StudyCafeUsage getStudyCafeUsage(@PathVariable Long id) {
    return studyCafeService.getStudyCafeUsage(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Long addStudyCafe(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody StudyCafeDetailPost body) {
    validateUser(user);
    validateAdmin(user);
    return studyCafeService.addStudyCafe(user.getId(), body);
  }

  @PatchMapping("/{id}")
  public void updateStudyCafe(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id,
      @RequestBody StudyCafeDetailPost body) {
    validateUser(user);
    validateAdmin(user);
    studyCafeService.updateStudyCafe(user.getId(), id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteStudyCafe(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    validateUser(user);
    validateAdmin(user);
    studyCafeService.deleteStudyCafe(user.getId(), id);
  }

  @PostMapping("/{id}/favorite")
  public void addFavoriteStudyCafe(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    validateUser(user);
    studyCafeService.addFavoriteStudyCafe(user.getId(), id);
  }

  @DeleteMapping("/{id}/favorite")
  public void deleteFavoriteStudyCafe(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    validateUser(user);
    studyCafeService.deleteFavoriteStudyCafe(user.getId(), id);
  }

  @DeleteMapping("/{id}/users/{userId}/time")
  public void deleteUserStudyCafeTime(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id,
      @PathVariable Long userId) {
    validateUser(user);
    validateAdmin(user);
    studyCafeService.deleteUserStudyCafeTime(id, userId);
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
