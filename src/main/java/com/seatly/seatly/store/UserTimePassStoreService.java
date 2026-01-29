package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.global.exception.ForbiddenException;
import com.seatly.seatly.repository.UserTimePassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTimePassStoreService {

  private final UserTimePassRepository store;

  public UserTimePass findById(UserTimePassId id) {
    return store.findById(id).orElse(null);
  }

  public UserTimePass findByIdOrThrow(UserTimePassId id) {
    return store.findById(id).orElseThrow(
        () -> new ForbiddenException("UserTimePass not found: " + id));
  }

  public List<TimePass> findAllByUserId(Long userId) {
    return store.findByUserId(userId).stream()
        .map(entity -> new TimePass(
            entity.getStudyCafe().getId(),
            entity.getUser().getId(),
            entity.getLeftTime(),
            entity.getTotalTime()))
        .toList();
  }

  public List<UserTimePassInfo> getTimePasseInfosByStudyCafeId(Long studyCafeId) {
    return store.findTimePassInfosByStudyCafeId(studyCafeId);
  }

  public UserTimePass save(UserTimePass timePass) {
    return store.save(timePass);
  }

  public void deleteById(UserTimePassId id) {
    store.deleteById(id);
  }

  public void deleteByUserIdAndStudyCafeId(Long userId, Long studyCafeId) {
    store.deleteByUserIdAndStudyCafeId(userId, studyCafeId);
  }

}
