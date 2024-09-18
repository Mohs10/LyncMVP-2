package com.example.Lync.Repository;

import com.example.Lync.Entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Type findByTypeName(String typeName);
    boolean existsByTypeName(String typeName);

}
