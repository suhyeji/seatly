package com.seatly.seatly.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserInfoDetail;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserStudyCafeLinkStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserStoreService userStoreService;
  private final UserStudyCafeLinkStoreService userStudyCafeLinkStoreService;
  private final SessionStoreService sessionStoreService;
  private final UserTimePassStoreService userTimePassStoreService;

  private final PasswordEncoder passwordEncoder;

  public void signUp(UserPost model) {
    User user = new User();
    user.setName(model.getName());
    user.setEmail(model.getEmail());
    user.setPhone(model.getPhone());
    user.setRole(model.getRole());
    user.setPassword(passwordEncoder.encode(model.getPassword()));
    user.setImageUrl(model.getImageUrl());
    user.setCreatedAt(LocalDateTime.now());
    userStoreService.save(user);
  }

  @Transactional
  public UserInfoDetail getUserInfoDetail(Long id) {
    UserInfoDetail result = new UserInfoDetail();

    User user = userStoreService.getUserInfoOrThrow(id);
    result.setEmail(user.getEmail());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setImageUrl(user.getImageUrl());

    List<Long> favoriteCafeIds = userStudyCafeLinkStoreService.getStudyCafeIdsByUserIdAndLinkType(id,
        UserCafeLinkType.FAVORITE);
    result.setFavoriteCafeIds(favoriteCafeIds);

    List<SessionInfo> sessions = sessionStoreService.findSessionInfosByUserId(id);
    result.setSessions(sessions);

    List<TimePass> timePasses = userTimePassStoreService.getTimePassesByUserId(id);
    result.setTimePasses(timePasses);

    return result;
  }

  // 관리자만 가능
  public UserInfo getUserInfo(Long id) {
    UserInfo result = new UserInfo();
    User user = userStoreService.getUserInfoOrThrow(id);
    result.setEmail(user.getEmail());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setImageUrl(user.getImageUrl());
    return result;
  }

  // 관리자만 가능
  public List<UserTimePassInfo> getUsersTimeInfo(Long studyCafeId) {
    return userTimePassStoreService.getTimePasseInfosByStudyCafeId(studyCafeId);
  }

  public UserInfoDetail updateUserInfo(Long id, UserPatch userPatch) {
    UserInfoDetail result = new UserInfoDetail();
    User user = userStoreService.getUserInfoOrThrow(id);
    userStoreService.save(userPatch.patch(user));
    return result;
  }

  @Transactional
  public void updatePassword(Long id, UserPasswordPut passwordPut) {
    User user = userStoreService.getUserInfoOrThrow(id);
    if (!passwordEncoder.matches(passwordPut.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
    }
    user.setPassword(passwordEncoder.encode(passwordPut.getNewPassword()));
    userStoreService.save(user);
  }

  public void deleteUser(Long id) {
    userStoreService.deleteById(id);
  }

}
