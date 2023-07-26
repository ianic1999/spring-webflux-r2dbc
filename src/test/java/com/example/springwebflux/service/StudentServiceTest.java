package com.example.springwebflux.service;

import com.example.springwebflux.domain.Book;
import com.example.springwebflux.domain.Student;
import com.example.springwebflux.repository.BookRepository;
import com.example.springwebflux.repository.StudentBookRepository;
import com.example.springwebflux.repository.StudentRepository;
import com.example.springwebflux.service.mapper.Mapper;
import com.example.springwebflux.web.dto.BookDto;
import com.example.springwebflux.web.dto.StudentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private StudentBookRepository studentBookRepository;
    @Mock
    private Mapper<Student, StudentDto> studentDtoMapper;

    private StudentService studentService;

    private StudentDto studentDto;

    private Student student;
    private Book book;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        studentService = new StudentService(studentRepository,
                                            bookRepository,
                                            studentBookRepository,
                                            studentDtoMapper);

        student = new Student("John", "Davis");
        book = new Book("Clean Code", "Robert C. Martin");

        Field field = student.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(student, 1L);
        field.setAccessible(false);

        field = book.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(book, 10L);
        field.setAccessible(false);

        BookDto bookDto = new BookDto(2L, "Clean Code", "Robert C. Martin", LocalDateTime.now(), LocalDateTime.now());
        studentDto = new StudentDto(1L, "John", "Davis", List.of(bookDto));
    }

    @Test
    public void getAll_whenInvoked_expectedResult() {
        lenient().when(studentRepository.findAll()).thenReturn(Flux.just(student));
        lenient().when(studentBookRepository.findBookIdsByStudentId(1L)).thenReturn(Flux.just(2L));
        lenient().when(bookRepository.findById(2L)).thenReturn(Mono.just(book));
        lenient().when(studentDtoMapper.mapList(anyList())).thenReturn(List.of(studentDto));

        StepVerifier.create(studentService.getAll())
                    .assertNext(students -> {
                        assertThat(students).hasSize(1)
                                            .containsOnly(studentDto);
                    })
                .verifyComplete();
    }

    @Test
    public void getAll_whenNoBooks_expectedResult() {
        lenient().when(studentRepository.findAll()).thenReturn(Flux.just(student));
        lenient().when(studentBookRepository.findBookIdsByStudentId(1L)).thenReturn(Flux.empty());
        lenient().when(studentDtoMapper.mapList(anyList())).thenReturn(List.of(studentDto));

        StepVerifier.create(studentService.getAll())
                    .assertNext(students -> {
                        assertThat(students).hasSize(1)
                                            .containsOnly(studentDto);
                    })
                .verifyComplete();

    }

    @Test
    public void getAll_whenInvoked_enrichesWithBooks() {
        lenient().when(studentRepository.findAll()).thenReturn(Flux.just(student));
        lenient().when(studentBookRepository.findBookIdsByStudentId(1L)).thenReturn(Flux.just(2L));
        lenient().when(bookRepository.findById(2L)).thenReturn(Mono.just(book));
        lenient().when(studentDtoMapper.mapList(anyList())).thenReturn(List.of(studentDto));
        ArgumentCaptor<List<Student>> studentCaptor = ArgumentCaptor.forClass(List.class);

        studentService.getAll().block();

        verify(studentDtoMapper).mapList(studentCaptor.capture());

        assertThat(studentCaptor.getValue()).hasSize(1)
                .extracting(Student::getBooks)
                .allSatisfy(books -> assertThat(books).hasSize(1).containsExactly(book));
    }

    @Test
    public void addBook_whenNoBookFound_errorResult() {
        when(studentRepository.findById(1L)).thenReturn(Mono.just(student));
        when(bookRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(studentService.addBook(1L, 1L))
                .expectErrorMatches(err -> err.getMessage().equals("No student/book with given id's"))
                .verify();
    }

    @Test
    public void addBook_whenNoStudentFound_errorResult() {
        when(studentRepository.findById(1L)).thenReturn(Mono.empty());
        when(bookRepository.findById(1L)).thenReturn(Mono.just(book));

        StepVerifier.create(studentService.addBook(1L, 1L))
                    .expectErrorMatches(err -> err.getMessage().equals("No student/book with given id's"))
                    .verify();
    }
}
