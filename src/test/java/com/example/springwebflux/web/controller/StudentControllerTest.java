package com.example.springwebflux.web.controller;

import com.example.springwebflux.common.AbstractTestContainersTest;
import com.example.springwebflux.service.StudentService;
import com.example.springwebflux.web.dto.StudentDto;
import com.example.springwebflux.web.dto.StudentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentControllerTest extends AbstractTestContainersTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StudentService studentService;

    @Test
    public void get_whenInvoked_expectedResponse() {
        StudentDto student = new StudentDto(1L, "John", "White", List.of());
        when(studentService.getAll()).thenReturn(Mono.just(List.of(student)));

        webTestClient.get()
                .uri("/api/students")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StudentDto.class)
                .hasSize(1).contains(student);
    }

    @Test
    public void add_whenInvoked_expectedResponse() {
        StudentRequest request = new StudentRequest("John", "White");
        when(studentService.save(request)).thenReturn(Mono.just(1L));

        webTestClient.post()
                .uri("/api/students")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Long.class)
                .isEqualTo(1L);
    }

    @Test
    public void remove_whenInvoked_expectedResponse() {
        when(studentService.remove(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/students/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
