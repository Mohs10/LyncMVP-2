package com.example.Lync.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardOperatingProcedureDTO {

    private Long id;
    private LocalDate date;
    private String content;
    private String documentPath;
    private  String forRole;
}

