package com.example.Lync.Service;

import com.example.Lync.Entity.Type;

import java.util.List;

public interface TypeService {
    String addType(Type type);
    List<Type> getAllTypes();
}

