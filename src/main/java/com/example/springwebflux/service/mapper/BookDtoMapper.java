package com.example.springwebflux.service.mapper;

import com.example.springwebflux.domain.Book;
import com.example.springwebflux.web.dto.BookDto;
import org.springframework.stereotype.Component;

@Component
class BookDtoMapper implements Mapper<Book, BookDto> {

    @Override
    public BookDto map(Book entity) {
        return new BookDto(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getCreatedAt(),
                entity.getLastModifiedAt()
        );
    }
}
