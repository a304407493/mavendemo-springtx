/*
 * 1.不能使用mysql的双引号
 * 2.不能加用户/数据库前缀
 * */
INSERT INTO person (id,name, password) VALUES (1,'zhaiyz', 'Hello, world!');