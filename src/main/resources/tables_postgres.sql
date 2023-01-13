create table track_listen (
    id serial primary key,
    username varchar,
    artist varchar,
    track varchar,
    album varchar,
    date timestamp,
    artist_id varchar,
    album_id varchar
);
