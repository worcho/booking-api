alter table rooms
add constraint chk_rooms_capacity check (capacity > 0);