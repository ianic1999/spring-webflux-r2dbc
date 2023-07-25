package com.example.springwebflux.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
