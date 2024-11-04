package com.example.Lync.ServiceImpl;

import com.example.Lync.Entity.Testimonial;
import com.example.Lync.Repository.TestimonialRepository;
import com.example.Lync.Service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TestimonialServiceImpl implements TestimonialService {

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Override
    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    @Override
    public Optional<Testimonial> getTestimonialById(Long id) {
        return testimonialRepository.findById(id);
    }

    @Override
    public Testimonial createTestimonial(Testimonial testimonial) {
        testimonial.setDate(LocalDate.now());
        return testimonialRepository.save(testimonial);
    }

    @Override
    public Testimonial updateTestimonial(Long id, Testimonial testimonial) {
        return testimonialRepository.findById(id).map(existingTestimonial -> {
            existingTestimonial.setName(testimonial.getName());
            existingTestimonial.setDate(testimonial.getDate());
            existingTestimonial.setContent(testimonial.getContent());
            return testimonialRepository.save(existingTestimonial);
        }).orElseThrow(() -> new RuntimeException("Testimonial not found with id " + id));
    }

    @Override
    public void deleteTestimonial(Long id) {
        testimonialRepository.deleteById(id);
    }
}

