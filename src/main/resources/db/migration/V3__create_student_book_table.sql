create table student_book
(
    id         bigserial primary key,
    student_id bigint not null references student (id),
    book_id    bigint not null references book (id),
    constraint student_book_unique unique (student_id, book_id)
);
