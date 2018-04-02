#!/bin/bash

# 本脚本的作用是
# 1. 编译打包Spring Boot应用
# 2. 编译litemall-adminy应用
# 3. 调用upload.sh上传
# 注意：运行脚本必须是在litemall主目录下,类似如下命令
# cd litemall
# ./deploy/util/lazy.sh

echo $PWD
mvn clean
mvn package

cd ./litemall-admin
cnpm run build:prod

cd ..
echo $PWD
./deploy/util/upload.sh