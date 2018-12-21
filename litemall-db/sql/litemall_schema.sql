drop database if exists litemall;
drop user if exists 'litemall'@'%';
create database litemall;
use litemall;
create user 'litemall'@'%' identified by 'litemall123456';
grant all privileges on litemall.* to 'litemall'@'%';
flush privileges;
