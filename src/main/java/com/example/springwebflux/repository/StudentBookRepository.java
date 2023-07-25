package com.example.springwebflux.repository;

import com.example.springwebflux.domain.StudentBook;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentBookRepository extends R2dbcRepository<StudentBook, Long> {

    @Modifying
    Mono<Long> deleteByBookId(Long bookId);

    @Modifying
    Mono<Long> deleteByStudentId(Long studentId);

    @Modifying
    Mono<Void> deleteByStudentIdAndBookId(Long studentId, Long bookId);

    @Query("select sb.book_id from student_book sb where sb.student_id = :studentId")
    Flux<Long> findBookIdsByStudentId(@Param("studentId") Long studentId);
}
