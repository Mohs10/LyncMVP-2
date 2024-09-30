package com.example.Lync.Service;
import com.example.Lync.Entity.Variety;

import java.util.List;

public interface VarietyService {
    String addVariety(Variety variety);
    List<Variety> getAllVarieties();
}

