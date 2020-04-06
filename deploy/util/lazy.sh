#!/bin/bash

# 本脚本的作用是
# 1. 项目打包
# 2. 上传云服务器
# 3. 远程登录云服务器并执行reset脚本

# 请设置云服务器的IP地址和账户
# 例如 ubuntu@122.51.199.160
REMOTE=
# 请设置本地SSH私钥文件id_rsa路径
# 例如 /home/litemall/id_rsa
ID_RSA=

if test -z "$REMOTE"
then
  echo "请设置云服务器登录IP地址和账户"
  exit 1
fi

if test -z "$ID_RSA"
then
  echo "请设置云服务器登录IP地址和账户"
  exit 1
fi

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
cd $DIR/../.. || exit 2
LITEMALL_HOME=$PWD
echo "LITEMALL_HOME $LITEMALL_HOME"

# 项目打包
cd $LITEMALL_HOME || exit 2
./deploy/util/package.sh

# 上传云服务器
cd $LITEMALL_HOME || exit 2
scp -i $ID_RSA -r  ./deploy $REMOTE:/home/ubuntu/

# 远程登录云服务器并执行reset脚本
# 这里使用tr命令，因为有可能deploy.sh和reset.sh的换行格式是CRLF，而LINUX环境应该是LF
ssh $REMOTE -i $ID_RSA << eeooff
cd /home/ubuntu/deploy/bin
cat deploy.sh | tr -d '\r' > deploy2.sh
mv deploy2.sh deploy.sh
chmod +x deploy.sh
cat reset.sh | tr -d '\r' > reset2.sh
mv reset2.sh reset.sh
chmod +x reset.sh
sudo ./reset.sh
exit
eeooff