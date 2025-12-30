package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.repository.UserTimePassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTimePassStoreService {

  private final UserTimePassRepository store;

  public List<TimePass> getTimePassesByUserId(Long userId) {
    return store.findByUserId(userId).stream()
        .map(entity -> new TimePass(
            entity.getStudyCafe().getId(),
            entity.getUser().getId(),
            entity.getLeftTime(),
            entity.getTotalTime()))
        .toList();
  }

  public List<UserTimePassInfo> getTimePasseInfosByStudyCafeId(Long studyCafeId) {
    return store.findTimePassInfosByUserId(studyCafeId);
  }

  public void deleteByTimePassId(UserTimePassId timePassId) {
    store.deleteById(timePassId);
  }

}
