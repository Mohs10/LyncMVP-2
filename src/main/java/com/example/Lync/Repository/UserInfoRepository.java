package com.example.Lync.Repository;

import com.example.Lync.Entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    @Query("SELECT u FROM UserInfo u WHERE u.email = :email")
    Optional<UserInfo> findByEmail(String email);
    @Query("SELECT u FROM UserInfo u WHERE u.mobileNumber = :mobileNumber")
    Optional<UserInfo> findByMobileNumber(String mobileNumber);

}

