package com.example.springwebflux.service;

import com.example.springwebflux.domain.Student;
import com.example.springwebflux.domain.StudentBook;
import com.example.springwebflux.repository.BookRepository;
import com.example.springwebflux.repository.StudentBookRepository;
import com.example.springwebflux.repository.StudentRepository;
import com.example.springwebflux.service.mapper.Mapper;
import com.example.springwebflux.web.dto.StudentDto;
import com.example.springwebflux.web.dto.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final StudentBookRepository studentBookRepository;
    private final Mapper<Student, StudentDto> studentDtoMapper;

    public Mono<List<StudentDto>> getAll() {
        return studentRepository.findAll()
                .flatMap(this::enrichWithBooks)
                .collectList()
                .map(studentDtoMapper::mapList);
    }

    public Mono<StudentDto> getById(Long id) {
        return studentRepository.findById(id)
                .flatMap(this::enrichWithBooks)
                .map(studentDtoMapper::map);
    }

    private Mono<Student> enrichWithBooks(Student student) {
        return studentBookRepository.findBookIdsByStudentId(student.getId())
                                    .flatMap(bookRepository::findById)
                                    .collectList()
                                    .map(student::withBooks);
    }

    @Transactional
    public Mono<Long> save(StudentRequest request) {
        Student student = new Student(request.getFirstName(), request.getLastName());
        return studentRepository.save(student)
                .map(Student::getId);
    }

    @Transactional
    public Mono<Long> addBook(Long studentId, Long bookId) {
        return Mono.zip(studentRepository.findById(studentId), bookRepository.findById(bookId))
                .switchIfEmpty(Mono.error(new RuntimeException("No student/book with given id's")))
                .flatMap(tuple -> studentBookRepository.save(new StudentBook(tuple.getT1().getId(), tuple.getT2().getId())))
                .map(StudentBook::getId);
    }

    @Transactional
    public Mono<Void> removeBook(Long studentId, Long bookId) {
        return Mono.zip(studentRepository.findById(studentId), bookRepository.findById(bookId))
                   .switchIfEmpty(Mono.error(new RuntimeException("No student/book with given id's")))
                   .flatMap(tuple -> studentBookRepository.deleteByStudentIdAndBookId(tuple.getT1().getId(), tuple.getT2().getId()));

    }

    @Transactional
    public Mono<Void> remove(Long id) {
        return studentBookRepository.deleteByStudentId(id)
                .then(studentRepository.deleteById(id));
    }
}
