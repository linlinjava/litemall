#!/bin/bash

# 本脚本的作用是重置部署环境
# 1.重置数据库
# 2.删除storage文件夹内文件
# 3.调用deploy.sh启动服务
# 注意：由于1和2的原因，请仅在开发测试阶段使用本脚本！

# 重置数据库
# i. 请在`XXXXXX`处设置相应的root密码
# ii. 同时请注意root密码放在脚本是非常危险的，因此这里仅仅是用于开发测试阶段。
ROOT=root
PASSWORD=litemall123456

if test -z "$PASSWORD"
then
  echo "请设置云服务器MySQL的root账号密码"
  exit 1
fi

# 删除storage文件夹内文件
cd /home/ubuntu/docker/litemall/storage || exit 2
sudo rm -f ./**

cd /home/ubuntu/docker || exit 3
sudo docker-compose down
sudo docker-compose build
sudo docker image prune -f

# 删除db/data文件夹内文件重置数据
# 这样docker启动时会自动运行db/init-sql脚本，导入新的数据
cd /home/ubuntu/docker/db/data || exit 1
sudo rm -rf ./**

cd /home/ubuntu/docker || exit 3
sudo docker-compose up -d
