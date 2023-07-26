# Spring WebFlux with R2DBC Example

## Introduction

This documentation provides an overview and step-by-step guide to building a simple application using **Spring WebFlux** and **R2DBC**. Spring WebFlux is a reactive web framework that allows building non-blocking, asynchronous applications, and R2DBC is the reactive Relational Database Connectivity framework for accessing databases. The application have 2 entities: *Student* and *Book*, whick are in a many-to-many relationship and allows to manage these 2 entities relations between them.

## WebFlux

**Spring WebFlux** is the alternative to Spring MVC module. Spring WebFlux is used to create fully asynchronous and non-blocking application built on event-loop execution model

## R2DBC

**R2DBC** stands for *Reactive Relational Database Connectivity*, a specification to integrate SQL databases using reactive drivers. Spring Data R2DBC applies familiar Spring abstractions and repository support for R2DBC. It makes it easier to build Spring-powered applications that use relational data access technologies in a reactive application stack.

Spring Data R2DBC aims at being conceptually easy. In order to achieve this, it does NOT offer caching, lazy loading, write-behind, or many other features of ORM frameworks. This makes Spring Data R2DBC a simple, limited, opinionated object mapper.

## Setup

1. **Add Dependencies**

   Add the following dependencies to your `pom.xml` or `build.gradle` file:

   ```xml
   <!-- For Spring WebFlux -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-webflux</artifactId>
   </dependency>

   <!-- For R2DBC with H2 Database (you can choose a different R2DBC driver for your database) -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-r2dbc</artifactId>
   </dependency>
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>r2dbc-postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

3. **Configure Database Connection**

   In the `application.properties` file, configure the database connection properties:

   ```
   spring.r2dbc.url=${DATABASE_URL}
   spring.r2dbc.username=${DB_USERNAME}
   spring.r2dbc.password=${DB_PASSWORD}
   ```

   You can replace the connection URL and credentials with your actual database details if you are using a different database.

4. **Create a Reactive Entity**

   Define your rective entities. For example:

    ```java
    @Table
    @NoArgsConstructor
    @Getter
    @Setter
    public class Book {
    
        @Id
        @Setter(AccessLevel.NONE)
        private Long id;
        private String title;
        private String author;
        @CreatedDate
        private LocalDateTime createdAt;
        @LastModifiedDate
        private LocalDateTime lastModifiedAt;
        @Transient
        private List<Student> students;
    }
    ```

    ```java
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
    }
    ```

   As *Student* and *Book* are in many-to-many relationship, we need to create and intermediate entity that will keep the relations. In R2DBC Data, composite keys are not allowed and ORM is not so powerful, as in JPA       for example. So we need the intermadiate entity:

    ```java
    @Table("student_book")
    @NoArgsConstructor
    @Getter
    public class StudentBook {
    
        @Id
        private Long id;
        private Long studentId;
        private Long bookId;
    }
    ```

5. **Create the Repository Interface**

   Create a repository interface that extends `R2dbcRepository`:

   ```java
   @Repository
   public interface StudentRepository extends R2dbcRepository<Student, Long> {
   }
   ```

   Note: **R2dbcRepository** is R2DBC specific `org.springframework.data.repository.Repository` interface with reactive support.

6. **Create Services**

   In services, all the business logic happens. As R2DBC is not so powerful in ORM mapping, we need to implement all the steps for getting/adding/mapping entities. All these can be done in reactive chains, as in this       example:

   ```java
   public Mono<List<StudentDto>> getAll() {
        return studentRepository.findAll()
                .flatMap(this::enrichWithBooks)
                .collectList()
                .map(studentDtoMapper::mapList);
   }

    private Mono<Student> enrichWithBooks(Student student) {
        return studentBookRepository.findBookIdsByStudentId(student.getId())
                                    .flatMap(bookRepository::findById)
                                    .collectList()
                                    .map(student::withBooks);
   }
   ```

   This method finds all students, add all mapped books for each student, maps each student to DTO and returns the final list as a Publisher. Due to reactive chains, the code looks very pretty and simple.

7. **Create REST API Endpoints**

   Create a REST controller with endpoints to perform CRUD operations:

    ```java
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
    
        @DeleteMapping("/{id}")
        public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
            return studentService.remove(id)
                                 .then(Mono.just(ResponseEntity.noContent().build()));
        }
    }
    ```

8. **Test the Service**

   Reactive methods are tested using **StepVerifier**, which provides a declarative way of creating a verifiable script for an async Publisher sequence, by expressing expectations about the events that will happen upon     subscription. A simple unit test looks like this:

    ```java
    @Test
    public void getAll_whenInvoked_expectedResult() {
        when(studentRepository.findAll()).thenReturn(Flux.just(student));
        when(studentBookRepository.findBookIdsByStudentId(1L)).thenReturn(Flux.just(2L));
        when(bookRepository.findById(2L)).thenReturn(Mono.just(book));
        when(studentDtoMapper.mapList(anyList())).thenReturn(List.of(studentDto));

        StepVerifier.create(studentService.getAll())
                    .assertNext(students -> {
                        assertThat(students).hasSize(1)
                                            .containsOnly(studentDto);
                    })
                .verifyComplete();
    }
    ```

9. **Test the Controller**

   Controllers are tested using **WebTestClient**, which is a client that can connect to any server over HTTP, or to a WebFlux application via mock request and response objects.

   ```java
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
   ```

## Conclusion

Congratulations! You have successfully built a simple Spring WebFlux application with R2DBC as the reactive database connectivity. This combination allows your application to efficiently handle concurrent requests and deliver high performance. It can serve as a basement for your more complex application, as the main features for reactive programming are shown in this application.
