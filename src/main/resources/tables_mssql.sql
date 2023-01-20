create table track_listen (
    id int not null IDENTITY PRIMARY KEY,
    username varchar(256),
    artist varchar(256),
    track varchar(256),
    album varchar(256),
    date datetime,
    artist_id varchar(256),
    album_id varchar(256)
);