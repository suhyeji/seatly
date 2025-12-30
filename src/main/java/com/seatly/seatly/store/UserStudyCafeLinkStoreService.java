package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.repository.UserStudyCafeLinkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStudyCafeLinkStoreService {

  private final UserStudyCafeLinkRepository store;

  public List<UserStudyCafeLink> getUserStudyCafeLinkByUserId(Long userId) {
    return store.findAllByUserId(userId);
  }

  public List<UserStudyCafeLink> getUserStudyCafeLinkByUserIdAndLinkType(
      Long userId,
      UserCafeLinkType linkType) {
    return store.findAllByUserIdAndLinkType(userId, linkType);
  }

  public UserStudyCafeLink getUserStudyCafeLink(Long studyCafeId, Long userId) {
    return store.findByStudyCafeIdAndUserId(studyCafeId, userId)
        .orElse(null);
  }

  public void save(UserStudyCafeLink userStudyCafeLink) {
    store.save(userStudyCafeLink);
  }

  public void deleteByStudyCafeIdAndUserId(Long studyCafeId, Long userId) {
    UserStudyCafeLink link = store.findByStudyCafeIdAndUserId(studyCafeId, userId).orElse(null);

    if (link == null) {
      return;
    }

    store.deleteById(link.getId());
  }

}
