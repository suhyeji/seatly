package com.seatly.seatly.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.studycafe.StudyCafeDetail;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummary;
import com.seatly.seatly.dto.studycafe.StudyCafeUsage;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserStudyCafeLinkStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyCafeService {

  private final StudyCafeStoreService storeService;
  private final SeatStoreService seatStoreService;
  private final SessionStoreService sessionStoreService;
  private final UserStoreService userStoreService;
  private final UserStudyCafeLinkStoreService linkStoreService;
  private final UserTimePassStoreService userTimePassStoreService;

  public List<StudyCafeSummary> getStudySummaries() {
    return storeService.findAll().stream()
        .map(StudyCafeSummary::new)
        .toList();
  }

  public StudyCafeDetail getStudyCafeDetail(Long id) {
    return new StudyCafeDetail(storeService.findByIdOrNull(id));
  }

  public List<StudyCafeSummary> getAdminStudyCafeSummaries(Long userId) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      // 403 forbidden 에러 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    List<UserStudyCafeLink> links = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        user.getId(),
        UserCafeLinkType.ADMIN);
    return links.stream()
        .map(UserStudyCafeLink::getStudyCafe)
        .map(StudyCafeSummary::new)
        .toList();
  }

  public StudyCafeUsage getStudyCafeUsage(Long studyCafeId) {
    // 전체 seat 갯수 / 전체 세션 갯수
    return new StudyCafeUsage(
        seatStoreService.getCountByStudyCafeId(studyCafeId),
        sessionStoreService.getSessionCountByStudyCafeId(studyCafeId));
  }

  public Long addStudyCafe(Long userId, StudyCafeDetailPost body) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    StudyCafe result = storeService.save(body.insert());
    return result.getId();
  }

  public void updateStudyCafe(Long userId, Long studyCafeId, StudyCafeDetailPost body) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    StudyCafe entity = storeService.findByIdOrNull(studyCafeId);
    storeService.save(body.update(entity));
  }

  public void deleteStudyCafe(Long userId, Long id) {
    User user = userStoreService.findByIdOrNull(userId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    storeService.deleteById(id);
  }

  // 즐겨찾는 스터디카페 추가
  public void addFavoriteStudyCafe(Long userId, Long id) {
    User user = userStoreService.findByIdOrNull(userId);
    StudyCafe studyCafe = storeService.findByIdOrNull(id);

    UserStudyCafeLink link = new UserStudyCafeLink();
    link.setStudyCafe(studyCafe);
    link.setUser(user);
    link.setLinkType(UserCafeLinkType.FAVORITE);

    linkStoreService.save(link);
  }

  // 즐겨찾는 스터디카페 삭제
  public void deleteFavoriteStudyCafe(Long userId, Long id) {
    User user = userStoreService.findByIdOrNull(userId);
    linkStoreService.deleteByStudyCafeIdAndUserId(id, user.getId());
  }

  // 관리자가 사용자의 studycafe 남은 시간 삭제
  public void deleteUserStudyCafeTime(Long adminUserId, Long id, Long userId) {
    User user = userStoreService.findByIdOrNull(adminUserId);

    if (user.getRole() != UserRole.ADMIN) {
      // 현재 로그인 한 사용자가 관리자가 아닌 경우 예외 발생
      throw new AccessDeniedException("관리자 권한이 필요합니다.");
    }

    UserTimePassId timePassId = new UserTimePassId(id, userId);
    userTimePassStoreService.deleteById(timePassId);
  }
}
