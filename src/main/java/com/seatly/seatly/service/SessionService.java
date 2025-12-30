package com.seatly.seatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.dto.session.SessionDto;

@Service
public class SessionService {

  public List<SessionDto> getSessions() {
    return null;
  }

  public SessionDto startSession(Long id, SessionDto body) {
    // session 상태 변경
    // TODO: 관리자 계정 확인 -> 관리자는 관리자의 studycafe seat의 세션만 종료할 수 있음
    // TODO: user 계정 확인 -> 본인 세션만 종료 가능해야함
    return null;
  }

  public void endSession(Long id) {
    // TODO: 관리자 계정 확인 -> 관리자는 관리자의 studycafe seat의 세션만 종료할 수 있음
    // TODO: user 계정 확인 -> 본인 세션만 종료 가능해야함
    // session에서 삭제
  }

  public void assignSession(Long seatId) {
    // TODO: user 계정 확인
    // session에 추가
  }

  public void autossignSession(Long seatId) {
    // TODO: user 계정 확인
    // session에 추가
  }
}
