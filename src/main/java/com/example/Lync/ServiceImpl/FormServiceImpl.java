package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.Form;
import com.example.Lync.Repository.FormRepository;
import com.example.Lync.Service.FormService;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    public FormServiceImpl(FormRepository formRepository) {
        this.formRepository = formRepository;
    }


    @Override
    public String addForm(Form form) {
        if (formRepository.existsByFormName(form.getFormName())) {
            return "Form with this name already exists";
        }
        formRepository.save(form);
        return "Form added successfully";
    }

    @Override
    public List<Form> getAllForm() {
        return formRepository.findAll();
    }
}

