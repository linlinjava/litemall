#!/bin/bash

# 本脚本的作用是停止当前Spring Boot应用，然后再次部署
service litemall stop
ln -f -s /usr/litemall/deploy/litemall/litemall.jar /etc/init.d/litemall
chmod a+x /etc/init.d/litemall
service litemall start