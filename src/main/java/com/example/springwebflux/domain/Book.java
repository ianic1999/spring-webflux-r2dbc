package com.example.springwebflux.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Table
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    private String title;

    private String author;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Transient
    private List<Student> students;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }
}
