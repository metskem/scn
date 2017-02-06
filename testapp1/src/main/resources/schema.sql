-- setup DB stuff, this gets executed by spring. Ths dialect should match your DB engine (mysql in this case).
drop table if exists city;

create table city (
id integer auto_increment primary key,
name varchar(128) not null,
state varchar(128) not null,
country varchar(128) not null
)