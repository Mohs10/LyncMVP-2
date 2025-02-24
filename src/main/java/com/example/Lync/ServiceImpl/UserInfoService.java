package com.example.Lync.ServiceImpl;


import com.example.Lync.Config.UserInfoDetails;
import com.example.Lync.DTO.UserInfoDTO;
import com.example.Lync.Entity.UserInfo;
import com.example.Lync.Repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    @Lazy
    private PasswordEncoder encoder;
    private static final AtomicInteger counter = new AtomicInteger(0); // Sequential number generator

    public String generateUserId() {
        String prefix = "LYNC";
        String datePart = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int uniqueNumber = counter.incrementAndGet(); // Increment to get a new unique number

        return String.format("%s%s%03d", prefix, datePart, uniqueNumber); // LYNCYYYYMMDDXXX
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userDetail = repository.findByEmail(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Custom method to load user by mobile number for OTP authentication
    public UserDetails loadUserByMobileNumber(String mobileNumber) {
        Optional<UserInfo> userInfo = repository.findByMobileNumber(mobileNumber);

        // Converting userInfo to UserDetails
        return userInfo.map(user -> new UserInfoDetails(user, true)) // Pass OTP authenticated flag as true
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + mobileNumber));
    }

    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "User Added Successfully";
    }

    public UserInfoDTO findByEmail(String email)
    {
        UserInfo userDetail = repository.findByEmail(email).orElse(null);
        assert userDetail != null;
        return userInfoDtoConverter(userDetail);

    }


    private UserInfoDTO userInfoDtoConverter(UserInfo userInfo)
    {
        UserInfoDTO userInfoDTO =  new UserInfoDTO();
        userInfoDTO.setId(userInfo.getId());
        userInfoDTO.setName(userInfo.getName());
        userInfoDTO.setEmail(userInfo.getEmail());
        userInfoDTO.setMobileNumber(userInfo.getMobileNumber());
        userInfoDTO.setRoles(userInfo.getRoles());

        return userInfoDTO;
    }
}

