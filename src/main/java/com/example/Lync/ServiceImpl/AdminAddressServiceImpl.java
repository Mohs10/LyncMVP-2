package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.AdminAddress;
import com.example.Lync.Repository.AdminAddressRepository;
import com.example.Lync.Service.AdminAddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminAddressServiceImpl implements AdminAddressService {

    private AdminAddressRepository adminAddressRepository;

    @Override
    public AdminAddress addAdminAddress(AdminAddress adminAddress) {
        return adminAddressRepository.save(adminAddress);
    }

    @Override
    public List<AdminAddress> getAllAdminAddresses() {
        return adminAddressRepository.findAll();
    }
}
