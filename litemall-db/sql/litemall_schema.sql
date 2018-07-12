# drop database if exists litemall;
# drop user if exists 'litemall'@'localhost';
# create database litemall;
# use litemall;
# create user 'litemall'@'localhost' identified by 'litemall123456';
# grant all privileges on litemall.* to 'litemall'@'localhost';
# flush privileges;

drop database if exists beef;
drop user if exists 'beef'@'%';
create database beef;
use beef;
create user 'beef'@'%' identified by 'beef123';
grant all privileges on beef.* to 'beef'@'%';
flush privileges;