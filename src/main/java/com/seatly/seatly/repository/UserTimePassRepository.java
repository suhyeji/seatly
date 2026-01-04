package com.seatly.seatly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.user.UserTimePassInfo;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserTimePassRepository extends JpaRepository<UserTimePass, UserTimePassId> {

    List<UserTimePass> findByUserId(Long userId);

    Optional<UserTimePass> findById(UserTimePassId id);

    @Query("""
                select new com.seatly.seatly.dto.user.UserTimePassInfo(
                    u.id,
                    u.name,
                    utp.studyCafe.id,
                    utp.leftTime,
                    utp.totalTime
                )
                from UserTimePass utp
                join utp.user u
                where utp.studyCafe.id = :studyCafeId
            """)
    List<UserTimePassInfo> findTimePassInfosByStudyCafeId(
            @Param("studyCafeId") Long studyCafeId);

}
