package com.example.springwebflux.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentRequest {
    private String firstName;
    private String lastName;
}
