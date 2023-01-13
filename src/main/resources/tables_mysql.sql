create table track_listen (
    id int not null auto_increment,
    username varchar(256),
    artist varchar(256),
    track varchar(256),
    album varchar(256),
    date timestamp,
    artist_id varchar(256),
    album_id varchar(256),
    primary key(id)
);