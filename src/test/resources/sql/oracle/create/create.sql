/*
 * 1.不能使用mysql自动增长关键字/auto_increment
 * 2.不能加用户/数据库前缀
 * */
CREATE TABLE person (
	id int not null ,
	name varchar(32) not null,
	password varchar(32) not null,
	primary key (id)
);