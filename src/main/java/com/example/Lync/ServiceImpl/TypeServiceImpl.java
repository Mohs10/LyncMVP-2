package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.Type;
import com.example.Lync.Repository.TypeRepository;
import com.example.Lync.Service.TypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {

    private final TypeRepository typeRepository;

    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public String addType(Type type) {
        if (typeRepository.existsByTypeName(type.getTypeName())) {
            return "Type already exists!";
        }
        typeRepository.save(type);
        return "Type added successfully!";
    }

    @Override
    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }
}

