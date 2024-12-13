package com.example.Lync.Service;

import com.example.Lync.Entity.AdminAddress;
import com.example.Lync.Entity.SellerBuyerAddress;

import java.util.List;

public interface AdminAddressService {

    AdminAddress addAdminAddress(AdminAddress adminAddress);

    List<AdminAddress> getAllAdminAddresses();

    SellerBuyerAddress adminGetsSellerBuyerAddress(Long uaId);
}
