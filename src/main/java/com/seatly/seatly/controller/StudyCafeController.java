package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.dto.studycafe.StudyCafeDetailDto;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummaryDto;
import com.seatly.seatly.dto.studycafe.StudyCafeUsage;
import com.seatly.seatly.service.StudyCafeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/study-cafes")
@RequiredArgsConstructor
public class StudyCafeController {

  private final StudyCafeService studyCafeService;

  @GetMapping
  public List<StudyCafeSummaryDto> getStudySummaries() {
    return studyCafeService.getStudySummaries();
  }

  @GetMapping("/{id}")
  public StudyCafeDetailDto getStudyCafeDetail(@PathVariable Long id) {
    return studyCafeService.getStudyCafeDetail(id);
  }

  @GetMapping("/admin")
  public List<StudyCafeSummaryDto> getAdminStudySummaries() {
    return studyCafeService.getAdminStudySummaries();
  }

  @GetMapping("/{id}/usage")
  public StudyCafeUsage getStudyCafeUsage(@PathVariable Long id) {
    return studyCafeService.getStudyCafeUsage(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StudyCafeDetailDto addStudyCafe(@RequestBody StudyCafeDetailPost body) {
    return studyCafeService.addStudyCafe(body);
  }

  @PatchMapping("/{id}")
  public StudyCafeDetailDto updateStudyCafe(
      @PathVariable Long id,
      @RequestBody StudyCafeDetailPost body) {
    return studyCafeService.updateStudyCafe(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteStudyCafe(@PathVariable Long id) {
    studyCafeService.deleteStudyCafe(id);
  }

  @PostMapping("/{id}/favorite")
  public void addFavoriteStudyCafe(@PathVariable Long id) {
    studyCafeService.addFavoriteStudyCafe(id);
  }

  @DeleteMapping("/{id}/favorite")
  public void deleteFavoriteStudyCafe(@PathVariable Long id) {
    studyCafeService.deleteFavoriteStudyCafe(id);
  }

  @DeleteMapping("/{id}/users/{userId}/time")
  public void deleteUserStudyCafeTime(@PathVariable Long id, @PathVariable Long userId) {
    studyCafeService.deleteUserStudyCafeTime(id, userId);
  }

}
