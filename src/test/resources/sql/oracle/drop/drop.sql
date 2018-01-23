/*
1.不能使用下面的游标判断是否存在，存在再删除；
2.不能加用户/数据库前缀
declare
      num   number;
begin
    select count(1) into num from user_tables where table_name = upper('person') ;
    if num > 0 then
        execute immediate 'drop table person' ;
    end if;
end;

*/
drop table person;