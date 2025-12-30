package com.seatly.seatly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.user.UserTimePassInfo;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserTimePassRepository extends JpaRepository<UserTimePass, UserTimePassId> {

  List<UserTimePass> findByUserId(Long userId);

  @Query("""
          select new com.seatly.seatly.domain.timepass.dto.UserTimePassInfo(
              u.id,
              u.name,
              utp.studyCafe.id,
              utp.leftTime,
              utp.totalTime
          )
          from UserTimePass utp
          join utp.user u
          where u.id = :userId
      """)
  List<UserTimePassInfo> findTimePassInfosByUserId(
      @Param("userId") Long userId);

}
