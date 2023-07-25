create table book
(
    id               bigserial primary key,
    title            varchar(500) not null,
    author           varchar(500) not null,
    created_at       timestamp,
    last_modified_at timestamp
);
