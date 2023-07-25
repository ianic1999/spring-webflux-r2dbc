package com.example.springwebflux.web.controller;

import com.example.springwebflux.service.StudentService;
import com.example.springwebflux.web.dto.StudentDto;
import com.example.springwebflux.web.dto.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public Mono<ResponseEntity<List<StudentDto>>> get() {
        return studentService.getAll()
                             .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<StudentDto>> getById(@PathVariable Long id) {
        return studentService.getById(id)
                             .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Long>> add(@RequestBody StudentRequest request) {
        return studentService.save(request)
                             .map(id -> new ResponseEntity<>(id, HttpStatus.CREATED));
    }

    @PostMapping("/{studentId}/books/{bookId}")
    public Mono<ResponseEntity<Long>> addBook(@PathVariable Long studentId,
                                              @PathVariable Long bookId) {
        return studentService.addBook(studentId, bookId)
                             .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{studentId}/books/{bookId}")
    public Mono<ResponseEntity<Void>> removeBook(@PathVariable Long studentId,
                                                 @PathVariable Long bookId) {
        return studentService.removeBook(studentId, bookId)
                             .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
        return studentService.remove(id)
                             .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
