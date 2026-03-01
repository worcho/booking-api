create table users (
    id       bigserial primary key,
    username varchar(32) not null,
    password varchar(32) not null,
    role     varchar(32) not null
);