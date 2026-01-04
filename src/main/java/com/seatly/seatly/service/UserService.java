package com.seatly.seatly.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserInfoDetail;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserStudyCafeLinkStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserStoreService userStoreService;
  private final UserStudyCafeLinkStoreService userStudyCafeLinkStoreService;
  private final SessionStoreService sessionStoreService;
  private final UserTimePassStoreService userTimePassStoreService;
  private final StudyCafeStoreService studyCafeStoreService;

  private final RedisService redisService;

  private final PasswordEncoder passwordEncoder;

  public void signUp(UserPost model) {
    User user = new User();
    user.setName(model.getName());
    user.setEmail(model.getEmail());
    user.setPhone(model.getPhone());
    user.setRole(model.getRole());
    user.setPassword(passwordEncoder.encode(model.getPassword()));
    user.setImageUrl(model.getImageUrl());
    userStoreService.save(user);
  }

  @Transactional
  public UserInfoDetail getUserInfoDetail(Long id) {
    UserInfoDetail result = new UserInfoDetail();

    User user = userStoreService.findByIdOrThrow(id);
    result.setEmail(user.getEmail());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setImageUrl(user.getImageUrl());
    result.setRole(user.getRole());

    List<Long> favoriteCafeIds = userStudyCafeLinkStoreService.getStudyCafeIdsByUserIdAndLinkType(id,
        UserCafeLinkType.FAVORITE);
    result.setFavoriteCafeIds(favoriteCafeIds);

    List<SessionInfo> sessions = sessionStoreService.findSessionInfosByUserId(id);
    result.setSessions(sessions);

    List<TimePass> timePasses = userTimePassStoreService.findAllByUserId(id);
    result.setTimePasses(timePasses);

    return result;
  }

  public UserInfo getUserInfo(Long id) {
    UserInfo result = new UserInfo();
    User user = userStoreService.findByIdOrThrow(id);
    result.setEmail(user.getEmail());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setImageUrl(user.getImageUrl());
    return result;
  }

  public List<UserTimePassInfo> getUsersTimeInfo(Long studyCafeId) {
    return userTimePassStoreService.getTimePasseInfosByStudyCafeId(studyCafeId);
  }

  @Transactional
  public void addUserTimePass(Long userId, Long studyCafeId, Long time) {
    UserTimePassId id = new UserTimePassId(studyCafeId, userId);
    UserTimePass timePass = userTimePassStoreService.findById(id);
    if (timePass == null) {
      timePass = new UserTimePass();
      timePass.setId(id);
      timePass.setUser(userStoreService.findByIdOrThrow(userId));
      timePass.setStudyCafe(studyCafeStoreService.findByIdOrThrow(studyCafeId));
      timePass.setLeftTime(time);
      timePass.setTotalTime(time);
    } else {
      timePass.setLeftTime(timePass.getLeftTime() + time);
      timePass.setTotalTime(timePass.getTotalTime() + time);
    }
    userTimePassStoreService.save(timePass);

    if (redisService.hasSessionByUserId(userId)) {
      redisService.extendSessionTimeByUserId(userId, time);
    }
  }

  public void updateUserInfo(Long id, UserPatch userPatch) {
    User user = userStoreService.findByIdOrThrow(id);
    userStoreService.save(userPatch.patch(user));
  }

  @Transactional
  public void updatePassword(Long id, UserPasswordPut passwordPut) {
    User user = userStoreService.findByIdOrThrow(id);
    if (!passwordEncoder.matches(passwordPut.getCurrentPassword(), user.getPassword())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "현재 비밀번호가 일치하지 않습니다.");
    }
    user.setPassword(passwordEncoder.encode(passwordPut.getNewPassword()));
    userStoreService.save(user);
  }

  public void deleteUser(Long id) {
    userStoreService.deleteById(id);
  }

}
