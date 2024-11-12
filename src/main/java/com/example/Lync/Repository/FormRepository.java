package com.example.Lync.Repository;

import com.example.Lync.Entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    Form findByFormName(String formName);
    boolean existsByFormName(String formName);
    Optional<String> findFormNameByFormId(Long formId);


}
