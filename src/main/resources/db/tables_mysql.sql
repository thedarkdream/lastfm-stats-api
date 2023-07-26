create table track_listens (
    id int not null auto_increment,
    user_id int,
    artist_id int,
    track varchar(256),
    album varchar(256),
    date timestamp,
    album_id varchar(256),
    primary key(id)
);

create table users (
    id int not null auto_increment,
    username varchar(256),
    primary key(id)
)

create table artists (
    id int not null auto_increment,
    name varchar(256),
    lastfm_id varchar(256)
)

create table artist_aliases (
    id int not null auto_increment,
    artist_id int,
    name varchar(256)
)

create table artist_tags (
    id int not null auto_increment,
    artist_id int,
    tag varchar(256)
)