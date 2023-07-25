package com.example.springwebflux.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("student_book")
@NoArgsConstructor
@Getter
public class StudentBook {

    @Id
    private Long id;

    private Long studentId;

    private Long bookId;

    public StudentBook(Long studentId, Long bookId) {
        this.studentId = studentId;
        this.bookId = bookId;
    }
}
