package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.Variety;
import com.example.Lync.Repository.VarietyRepository;
import com.example.Lync.Service.VarietyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VarietyServiceImpl implements VarietyService {

    private final VarietyRepository varietyRepository;

    public VarietyServiceImpl(VarietyRepository varietyRepository) {
        this.varietyRepository = varietyRepository;
    }

    @Override
    public String addVariety(Variety variety) {

        if (varietyRepository.existsByVarietyName(variety.getVarietyName())) {
            return "Variety already exists!";
        }
        varietyRepository.save(variety);
        return "Variety added successfully!";
    }

    @Override
    public List<Variety> getAllVarieties() {
        return varietyRepository.findAll();
    }
}
