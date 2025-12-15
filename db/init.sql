create database tank_battle;

use tank_battle;

create table user
(
    id          bigint primary key auto_increment,
    username    varchar(256) not null comment '用户名',
    password    varchar(256) not null comment '密码',
    nickname    varchar(256) not null comment '昵称',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间',
    UNIQUE KEY uk_username (username)
) comment '用户表';
