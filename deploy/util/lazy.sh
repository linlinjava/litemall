#!/bin/bash

# 本脚本的作用是
# 1. 项目打包
# 2. 上传云主机
# 3. 远程登录云主机并执行reset脚本

# 请设置云主机的IP地址和账户
# 例如 ubuntu@118.24.0.153
REMOTE=root@47.94.211.247
# 请设置本地SSH私钥文件id_rsa路径
# 例如 /home/litemall/id_rsa
ID_RSA=/Users/kingsley/.ssh

if test -z "$REMOTE"
then
  echo "请设置云主机登录IP地址和账户"
  exit 1
fi

if test -z "$ID_RSA"
then
  echo "请设置云主机登录IP地址和账户"
  exit 1
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
cd $DIR/../.. || exit 2
LITEMALL_HOME=$PWD
echo "LITEMALL_HOME $LITEMALL_HOME"

# 项目打包
cd $LITEMALL_HOME || exit 2
./deploy/util/package.sh

# 上传云主机
cd $LITEMALL_HOME || exit 2
scp -i $ID_RSA -r  ./deploy $REMOTE -p 8015:/usr/litemall/

# 远程登录云主机并执行reset脚本
ssh $REMOTE -p 8015 -i $ID_RSA << eeooff
cd /usr/litemall/
./bin/reset.sh
exit
eeooff