package com.example.springwebflux.service.mapper;

import com.example.springwebflux.domain.Book;
import com.example.springwebflux.domain.Student;
import com.example.springwebflux.web.dto.BookDto;
import com.example.springwebflux.web.dto.StudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class StudentDtoMapper implements Mapper<Student, StudentDto> {

    private final Mapper<Book, BookDto> bookDtoMapper;
    @Override
    public StudentDto map(Student entity) {
        return new StudentDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                bookDtoMapper.mapList(entity.getBooks())
        );
    }
}
