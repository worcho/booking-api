create table rooms (
    id bigserial primary key,
    name varchar(100) not null,
    capacity integer not null,
    created_at timestamp not null default now()
);

create index idx_rooms_name on rooms(name);