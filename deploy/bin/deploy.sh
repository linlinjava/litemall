#!/bin/bash

# 本脚本的作用是停止当前Spring Boot应用，然后再次部署
sudo service litemall stop
sudo ln -f -s /home/ubuntu/deploy/litemall/litemall.jar /etc/init.d/litemall
sudo update-rc.d litemall defaults
sudo update-rc.d litemall enable
sudo service litemall start