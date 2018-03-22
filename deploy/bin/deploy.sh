#!/bin/bash

#部署litemall-admin静态文件应用
cd /home/ubuntu/deploy/litemall-admin
rm -rf dist
mkdir dist
tar -zxvf dist.tar -C dist
cd .

#部署三个Spring Boot应用
#如果服务已经启动，则尝试停止
sudo /etc/init.d/litemall-os-api stop
sudo /etc/init.d/litemall-wx-api stop
sudo /etc/init.d/litemall-admin-api stop

#部署Spring Boot应用成服务
sudo ln -f -s /home/ubuntu/deploy/litemall-os-api/litemall-os-api.jar /etc/init.d/litemall-os-api
sudo ln -f -s /home/ubuntu/deploy/litemall-wx-api/litemall-wx-api.jar /etc/init.d/litemall-wx-api
sudo ln -f -s /home/ubuntu/deploy/litemall-admin-api/litemall-admin-api.jar /etc/init.d/litemall-admin-api

#启动服务
sudo /etc/init.d/litemall-os-api restart
sudo /etc/init.d/litemall-wx-api restart
sudo /etc/init.d/litemall-admin-api restart