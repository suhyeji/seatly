package com.seatly.seatly.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seatly.seatly.domain.QSeat;
import com.seatly.seatly.domain.QSession;
import com.seatly.seatly.domain.Session;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionRepositoryCustomImpl implements SessionRepositoryCustom {

  private static final QSeat seat = QSeat.seat;
  private static final QSession session = QSession.session;
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Session> findByStudyCafeId(Long studyCafeId) {
    return queryFactory
        .selectFrom(session)
        .join(session.seat, seat).fetchJoin()
        .where(seat.studyCafe.id.eq(studyCafeId))
        .fetch();
  }

}
