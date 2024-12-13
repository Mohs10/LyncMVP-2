package com.example.Lync.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "standard_operating_procedure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardOperatingProcedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String documentPath;

    @Column(nullable = false, updatable = false, unique = true)
    private  String forRole;
}

