package com.example.springwebflux.web.controller;

import com.example.springwebflux.service.BookService;
import com.example.springwebflux.web.dto.BookDto;
import com.example.springwebflux.web.dto.BookRequest;
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
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Mono<ResponseEntity<List<BookDto>>> get() {
        return bookService.getAll()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookDto>> getById(@PathVariable Long id) {
        return bookService.getById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Long>> add(@RequestBody BookRequest request) {
        return bookService.save(request)
                .map(id -> new ResponseEntity<>(id, HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
        return bookService.remove(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
