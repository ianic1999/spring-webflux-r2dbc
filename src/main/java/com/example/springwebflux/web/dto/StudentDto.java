package com.example.springwebflux.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class StudentDto {
    private Long id;
    private String firstName;
    private String lastName;
    private List<BookDto> books;
}
