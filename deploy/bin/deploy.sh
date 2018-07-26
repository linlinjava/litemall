#!/bin/bash

# 请注意
# 1. 本脚本的作用是停止当前Spring Boot应用，然后再次部署
# 2. 解压dist.tar到/home/ubuntu/deploy/litemall-admin/dist，
#    而这个目录也正是tomcat或者nginx所配置静态文件目录的路径（见1.5.3.5节）

#部署litemall-admin静态文件应用
cd /home/ubuntu/deploy/litemall-admin
rm -rf dist
mkdir dist
tar -zxvf dist.tar -C dist

#部署三个Spring Boot应用
#如果服务已经启动，则尝试停止
sudo service litemall-os-api stop
sudo service litemall-wx-api stop
sudo service litemall-admin-api stop

#部署Spring Boot应用成服务
sudo ln -f -s /home/ubuntu/deploy/litemall-api/litemall-os-api.jar /etc/init.d/litemall-os-api
sudo ln -f -s /home/ubuntu/deploy/litemall-api/litemall-wx-api.jar /etc/init.d/litemall-wx-api
sudo ln -f -s /home/ubuntu/deploy/litemall-api/litemall-admin-api.jar /etc/init.d/litemall-admin-api

#启动服务
sudo service litemall-os-api restart
sudo service litemall-wx-api restart
sudo service litemall-admin-api restart

# 如果静态文件是通过tomcat来服务，则tomcat8服务最好也再启动
#sudo service tomcat8 stop
#sudo service tomcat8 start