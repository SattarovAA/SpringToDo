--
-- PostgreSQL Database: rlabs_flyway_mvn (UTF-8)
-- Initial SQL Script
--
CREATE TABLE users
(
    id       bigserial    not null primary key,
    username varchar(255) not null,
    password varchar(255) not null,
    email    varchar(255) not null,
    role     varchar(255) not null
);

create unique index user_email_uindex
    on users (email);

create unique index user_username_uindex
    on users (username);
CREATE TABLE tasks
(
    id          bigserial    not null primary key,
    name        varchar(255) not null,
    description varchar(255) not null,
    status      varchar(255) not null,
    created_at  timestamp    not null,
    updated_at  timestamp    not null,
    author_id   integer      not null
        constraint tasks_users_id_fk
            references users
);