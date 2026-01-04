package com.seatly.seatly.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.seat.SeatInfo;
import com.seatly.seatly.dto.seat.SeatPatch;
import com.seatly.seatly.dto.seat.SeatPost;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

  private final SeatStoreService storeService;
  private final UserStoreService userStoreService;
  private final StudyCafeStoreService studyCafeStoreService;

  public Seat getSeatById(Long id) {
    return storeService.findByIdOrNull(id);
  }

  public List<SeatInfo> getSeats(Long userId, Long studyCafeId) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    return storeService.findAllByStudyCafeId(studyCafeId).stream()
        .map(seat -> new SeatInfo(seat, studyCafeId))
        .toList();
  }

  public void addSeat(Long userId, Long studyCafeId, List<SeatPost> body) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    StudyCafe studyCafe = studyCafeStoreService.findByIdOrNull(studyCafeId);

    body.stream().forEach(seatPost -> {
      Seat seat = seatPost.insert();
      seat.setStudyCafe(studyCafe);
      storeService.save(seat);
    });
  }

  public void updateSeats(Long userId, List<SeatPatch> body) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    body.stream().forEach(seatPatch -> {
      Seat seat = storeService.findByIdOrNull(seatPatch.getId());
      if (seat == null)
        return;
      storeService.save(seatPatch.patch(seat));
    });
  }

  public void deleteSeat(Long userId, Long id) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    storeService.deleteById(id);
  }

}
