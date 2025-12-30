package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.dto.session.SessionDto;
import com.seatly.seatly.service.SessionService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

  private final SessionService sessionService;

  @GetMapping
  public List<SessionDto> getSessions() {
    return sessionService.getSessions();
  }

  @PatchMapping("/{id}/start")
  public SessionDto startSession(@PathVariable Long id, @RequestBody SessionDto body) {
    return sessionService.startSession(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void endSession(@PathVariable Long id) {
    sessionService.endSession(id);
  }

  @PostMapping("/assign")
  public void assignSession(@RequestBody Long seatId) {
    sessionService.assignSession(seatId);
  }

  @PostMapping("/auto-assign")
  public void autossignSession(@RequestBody Long seatId) {
    sessionService.autossignSession(seatId);
  }

}
