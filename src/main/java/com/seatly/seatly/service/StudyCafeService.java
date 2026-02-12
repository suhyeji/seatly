package com.seatly.seatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.studycafe.StudyCafeDetail;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummary;
import com.seatly.seatly.dto.studycafe.StudyCafeUsage;
import com.seatly.seatly.global.exception.ForbiddenException;
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
    List<UserStudyCafeLink> links = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        user.getId(),
        UserCafeLinkType.ADMIN);
    return links.stream()
        .map(UserStudyCafeLink::getStudyCafe)
        .map(StudyCafeSummary::new)
        .toList();
  }

  public StudyCafeUsage getStudyCafeUsage(Long studyCafeId) {
    // 전체 seat 개수 / 전체 세션 개수
    return new StudyCafeUsage(
        seatStoreService.getCountByStudyCafeId(studyCafeId),
        sessionStoreService.getSessionCountByStudyCafeId(studyCafeId));
  }

  public Long addStudyCafe(Long userId, StudyCafeDetailPost body) {
    User user = userStoreService.findByIdOrNull(userId);
    StudyCafe result = storeService.save(body.insert());

    UserStudyCafeLink link = new UserStudyCafeLink();
    link.setStudyCafe(result);
    link.setUser(user);
    link.setLinkType(UserCafeLinkType.ADMIN);
    linkStoreService.save(link);

    return result.getId();
  }

  public void updateStudyCafe(Long userId, Long studyCafeId, StudyCafeDetailPost body) {
    UserStudyCafeLink link = linkStoreService.getUserStudyCafeLink(studyCafeId, userId);
    if (link == null || !UserCafeLinkType.ADMIN.equals(link.getLinkType())) {
      throw new ForbiddenException("삭제 권한이 없습니다.");
    }
    StudyCafe entity = storeService.findByIdOrNull(studyCafeId);
    storeService.save(body.update(entity));
  }

  public void deleteStudyCafe(Long userId, Long id) {
    UserStudyCafeLink link = linkStoreService.getUserStudyCafeLink(id, userId);
    if (link == null || !UserCafeLinkType.ADMIN.equals(link.getLinkType())) {
      throw new ForbiddenException("삭제 권한이 없습니다.");
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
    UserStudyCafeLink link = linkStoreService.getUserStudyCafeLink(id, userId);
    if (link == null || !UserCafeLinkType.FAVORITE.equals(link.getLinkType())) {
      throw new ForbiddenException("삭제 권한이 없습니다.");
    }
    linkStoreService.deleteByStudyCafeIdAndUserId(id, user.getId());
  }

  // 관리자가 사용자의 studycafe 남은 시간 삭제
  public void deleteUserStudyCafeTime(Long id, Long userId, Long adminId) {
    UserTimePassId timePassId = new UserTimePassId(id, userId);
    UserStudyCafeLink link = linkStoreService.getUserStudyCafeLink(timePassId.getStudyCafeId(), adminId);
    if (link == null || !UserCafeLinkType.ADMIN.equals(link.getLinkType())) {
      throw new ForbiddenException("삭제 권한이 없습니다.");
    }
    userTimePassStoreService.deleteById(timePassId);
  }
}
