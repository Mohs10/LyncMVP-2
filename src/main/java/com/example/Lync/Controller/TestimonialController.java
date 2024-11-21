package com.example.Lync.Controller;

import com.example.Lync.Entity.Testimonial;
import com.example.Lync.Service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:5173", "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com", "https://another-domain.com", "http://buyerwebportal.s3-website.ap-south-1.amazonaws.com"})

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialService testimonialService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Testimonial>> getAllTestimonials() {
        return new ResponseEntity<>(testimonialService.getAllTestimonials(), HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Testimonial> getTestimonialById(@PathVariable Long id) {
        return testimonialService.getTestimonialById(id)
                .map(testimonial -> new ResponseEntity<>(testimonial, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/addTestimonial")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<Testimonial> createTestimonial(@RequestBody Testimonial testimonial) {
        return new ResponseEntity<>(testimonialService.createTestimonial(testimonial), HttpStatus.CREATED);
    }

    @PutMapping("/updateTestimonial/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<Testimonial> updateTestimonial(@PathVariable Long id, @RequestBody Testimonial testimonial) {
        return new ResponseEntity<>(testimonialService.updateTestimonial(id, testimonial), HttpStatus.OK);
    }

    @DeleteMapping("/deleteTestimonial/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    public ResponseEntity<Void> deleteTestimonial(@PathVariable Long id) {
        testimonialService.deleteTestimonial(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

