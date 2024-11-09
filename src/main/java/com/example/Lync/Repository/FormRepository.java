package com.example.Lync.Repository;

import com.example.Lync.Entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Long> {
    Form findByFormName(String formName);
    boolean existsByFormName(String formName);
}
