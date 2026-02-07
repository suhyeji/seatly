package com.seatly.seatly.service;

import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.login.LoginRequest;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.store.SessionStoreService;
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

  public Pair<Long, UserInfo> login(LoginRequest request) {
    User user = userStoreService.findByEmailOrNull(request.getEmail());
    if (user == null ||
        !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
    UserInfo result = new UserInfo();
    setDto(result, user);
    return Pair.of(user.getId(), result);
  }

  public List<SessionInfo> getSessions(Long id) {
    return sessionStoreService.findSessionInfosByUserId(id);
  }

  public List<Long> getFavoriteStudyCafeIds(Long id) {
    return userStudyCafeLinkStoreService.getStudyCafeIdsByUserIdAndLinkType(id,
        UserCafeLinkType.FAVORITE);
  }

  public List<TimePass> getTimePasses(Long id) {
    return userTimePassStoreService.findAllByUserId(id);
  }

  private void setDto(UserInfo result, User user) {
    result.setEmail(user.getEmail());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setImageUrl(user.getImageUrl());
    result.setRole(user.getRole());
  }

  public UserInfo getUserInfo(Long id) {
    UserInfo result = new UserInfo();
    User user = userStoreService.findByIdOrThrow(id);
    result.setEmail(user.getEmail());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setImageUrl(user.getImageUrl());
    result.setRole(user.getRole());
    return result;
  }

  public List<UserTimePassInfo> getUsersTimeInfo(Long studyCafeId) {
    return userTimePassStoreService.getTimePasseInfosByStudyCafeId(studyCafeId);
  }

  public void deleteUser(Long id) {
    userStoreService.deleteById(id);
  }

}
