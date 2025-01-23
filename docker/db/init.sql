CREATE DATABASE IF NOT EXISTS announcement;

create table if not exists announcement.user
(
    id       bigint       not null auto_increment,
    email    varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null,
    role     enum ('ADMIN','MANAGER','USER'),
    primary key (id)
) engine = InnoDB

