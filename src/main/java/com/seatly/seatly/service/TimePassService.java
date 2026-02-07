package com.seatly.seatly.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.timepass.TimePassRequest;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TimePassService {

  private final UserStoreService userStoreService;
  private final StudyCafeStoreService studyCafeStoreService;
  private final UserTimePassStoreService userTimePassStoreService;

  private final RedisService redisService;

  private final AtomicLong requestId = new AtomicLong(0L);
  private final Map<Long, TimePassRequest> requests = new ConcurrentHashMap<>();

  public List<TimePassRequest> getRequests(Long studyCafeId) {
    return requests.values()
        .stream()
        .filter(it -> it.getStudyCafeId().equals(studyCafeId))
        .toList();
  }

  public void requestAddTimePass(Long userId, Long studyCafeId, Long time) {
    Long id = requestId.incrementAndGet();
    TimePassRequest request = new TimePassRequest(id, userId, studyCafeId, time);
    requests.put(id, request);
    // WebSocket으로 관리자에게 알림
  }

  @Transactional
  public void acceptRequest(Long requestId) {
    TimePassRequest request = requests.remove(requestId);

    Long studyCafeId = request.getStudyCafeId();
    Long userId = request.getUserId();
    Long time = request.getTime();

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

  public void rejectRequest(Long requestId) {
    requests.remove(requestId);
    //
  }

}
