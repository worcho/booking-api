create table bookings (
    id bigserial primary key,
    room_id bigint not null,
    start_date date not null,
    end_date date not null,
    status varchar(32) not null,
    created_at timestamp not null default now(),

    constraint fk_bookings_room
        foreign key (room_id) references rooms(id),

    constraint chk_booking_dates
        check (start_date < end_date)
);

create index ix_bookings_room_id on bookings(room_id);
create index ix_bookings_room_dates on bookings(room_id, start_date, end_date);