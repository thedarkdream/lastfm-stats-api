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

create table users (
    id serial primary key,
    username varchar
)


create table artists (
    id serial primary key,
    name varchar,
    lastfm_id varchar
)

create table artist_aliases (
    id serial primary key,
    artist_id integer,
    name varchar
)

create table artist_tags (
    id serial primary key,
    artist_id integer,
    tag varchar
)