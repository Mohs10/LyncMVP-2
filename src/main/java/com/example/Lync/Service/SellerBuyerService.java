package com.example.Lync.Service;

import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.Entity.SellerBuyer;

public interface SellerBuyerService {

    SellerBuyer findbyPhoneNumber(String phoneNumber);

    void createSeller(SellerBuyerDTO sellerBuyerDTO);
    void createBuyer(SellerBuyerDTO sellerBuyerDTO);

    public boolean isPhoneNumberInCache(String phoneNumber);

}
