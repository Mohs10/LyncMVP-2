package com.example.Lync.Service;

public interface OtpService {

    public String generateRandomOTP();

    public void sendOTP(String phoneNumber, String otp);
//    boolean verifyOtp(String phoneNumber, String otp);

}
