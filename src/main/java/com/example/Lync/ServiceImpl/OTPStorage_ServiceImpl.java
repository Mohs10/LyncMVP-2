package com.example.Lync.ServiceImpl;

import com.example.Lync.Service.OTPStorageService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPStorage_ServiceImpl implements OTPStorageService {
    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @Override
    public void storeOTP(String identifier, String otp) {
        otpStorage.put(identifier, otp);
    }
    @Override

    // Retrieve the stored OTP for a user identifier
    public String getStoredOTP(String identifier) {
        return otpStorage.get(identifier);
    }

    // Remove the stored OTP for a user identifier (e.g., after successful verification)
    @Override
    public void removeOTP(String identifier) {
        otpStorage.remove(identifier);
    }

    @Override
    public Map<String, String> getStoredOTPs() {
        return Collections.unmodifiableMap(otpStorage);
    }
    @Override
    public boolean validateOtp(String mobileNumber, String otp) {
        return otp.equals(otpStorage.get(mobileNumber));
    }


    public Map<String, String> allStoredOTPs(){

        return otpStorage;
    }
}
