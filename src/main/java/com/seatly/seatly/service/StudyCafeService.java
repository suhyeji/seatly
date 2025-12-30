package com.seatly.seatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.dto.studycafe.StudyCafeDetailDto;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummaryDto;
import com.seatly.seatly.dto.studycafe.StudyCafeUsage;

@Service
public class StudyCafeService {

  public List<StudyCafeSummaryDto> getStudySummaries() {
    return null;
  }

  public StudyCafeDetailDto getStudyCafeDetail(Long id) {
    return null;
  }

  public List<StudyCafeSummaryDto> getAdminStudySummaries() {
    // TODO: 현재 로그인 한 관리자 계정 확인
    return null;
  }

  public StudyCafeUsage getStudyCafeUsage(Long id) {
    return null;
  }

  public StudyCafeDetailDto addStudyCafe(StudyCafeDetailPost body) {
    // TODO: 관리자 계정 확인
    return null;
  }

  public StudyCafeDetailDto updateStudyCafe(Long id, StudyCafeDetailPost body) {
    // TODO: 관리자 계정 확인
    return null;
  }

  public void deleteStudyCafe(Long id) {
    // TODO: 관리자 계정 확인
  }

  public void addFavoriteStudyCafe(Long id) {
    // TODO: user 계정 확인
  }

  public void deleteFavoriteStudyCafe(Long id) {
    // TODO: user 계정 확인
  }

  public void deleteUserStudyCafeTime(Long id, Long userId) {
    // TODO: 관리자 계정 확인
  }
}
