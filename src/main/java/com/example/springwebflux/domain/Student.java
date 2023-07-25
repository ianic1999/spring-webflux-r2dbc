package com.example.springwebflux.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Collections;
import java.util.List;

@Table
@NoArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    private String firstName;

    private String lastName;

    @Transient
    @Setter(AccessLevel.NONE)
    private List<Book> books;

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public Student withBooks(List<Book> books) {
        this.books = books;
        return this;
    }
}
