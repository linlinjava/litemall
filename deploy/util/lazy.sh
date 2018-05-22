#!/bin/bash

# 本脚本的作用是
# 1. 编译打包Spring Boot应用
# 2. 编译litemall-admin应用
# 3. 调用upload.sh上传
# 4. ssh远程登录云主机，运行deploy/bin/deploy.sh脚本
# 注意：运行脚本必须是在litemall主目录下,类似如下命令
# cd litemall
# ./deploy/util/lazy.sh


# 请设置云主机的IP地址和账户
# 例如 ubuntu@122.152.206.172
REMOTE=
# 请设置本地SSH私钥文件id_rsa路径
# 例如 /home/litemall/id_rsa
ID_RSA=

if test -z "$REMOTE"
then
  echo "请设置云主机登录IP地址和账户"
  exit -1
fi

if test -z "$ID_RSA"
then
  echo "请设置云主机登录IP地址和账户"
  exit -1
fi

echo $PWD
mvn clean
mvn package

cd ./litemall-admin
# 安装阿里node镜像工具
npm install -g cnpm --registry=https://registry.npm.taobao.org
# 安装node项目依赖环境
cnpm install
cnpm run build:dep

cd ..
echo $PWD
./deploy/util/upload.sh

# 远程登录云主机并执行deploy脚本
ssh $REMOTE -i $ID_RSA << eeooff
sudo ./deploy/bin/deploy.sh
exit
eeooff