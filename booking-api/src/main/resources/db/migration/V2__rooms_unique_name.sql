drop index if exists idx_rooms_name;
create unique index ux_rooms_name on rooms(name);