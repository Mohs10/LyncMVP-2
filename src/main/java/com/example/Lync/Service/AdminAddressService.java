package com.example.Lync.Service;

import com.example.Lync.Entity.AdminAddress;

import java.util.List;

public interface AdminAddressService {

    AdminAddress addAdminAddress(AdminAddress adminAddress);

    List<AdminAddress> getAllAdminAddresses();
}
