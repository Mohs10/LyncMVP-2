package com.example.Lync.Service;

import com.example.Lync.Entity.Form;
import com.example.Lync.Entity.Type;

import java.util.List;

public interface FormService {

    String addForm(Form form);
    List<Form> getAllForm();
}
