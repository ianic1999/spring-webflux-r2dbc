package com.example.springwebflux.service;

import com.example.springwebflux.domain.Book;
import com.example.springwebflux.repository.BookRepository;
import com.example.springwebflux.repository.StudentBookRepository;
import com.example.springwebflux.service.mapper.Mapper;
import com.example.springwebflux.web.dto.BookDto;
import com.example.springwebflux.web.dto.BookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final StudentBookRepository studentBookRepository;
    private final Mapper<Book, BookDto> bookDtoMapper;

    public Mono<List<BookDto>> getAll() {
        return bookRepository.findAll()
                .map(bookDtoMapper::map)
                .collectList();
    }

    public Mono<BookDto> getById(Long id) {
        return bookRepository.findById(id)
                .map(bookDtoMapper::map);
    }

    @Transactional
    public Mono<Long> save(BookRequest request) {
        Book book = new Book(request.getTitle(), request.getAuthor());

        return bookRepository.save(book)
                .map(Book::getId);
    }

    @Transactional
    public Mono<Void> remove(Long bookId) {
        return studentBookRepository.deleteByBookId(bookId)
                .then(bookRepository.deleteById(bookId));
    }
}
