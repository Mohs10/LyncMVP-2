package com.example.Lync.Service;

import com.example.Lync.Entity.Testimonial;

import java.util.List;
import java.util.Optional;

public interface TestimonialService {
    List<Testimonial> getAllTestimonials();
    Optional<Testimonial> getTestimonialById(Long id);
    Testimonial createTestimonial(Testimonial testimonial);
    Testimonial updateTestimonial(Long id, Testimonial testimonial);
    void deleteTestimonial(Long id);
}

