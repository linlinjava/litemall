#!/bin/bash

# 请注意
# 本脚本的作用是把本项目编译的结果保存到deploy文件夹中
# 1. 把项目数据库文件拷贝到litemall-db
# 2. 编译litemall-admin，然后打包到litemall-admin
# 3. 编译后台服务模块，然后拷贝到litemall-api

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
cd $DIR/../..
LITEMALL_HOME=$PWD
echo "LITEMALL_HOME $LITEMALL_HOME"

# 复制数据库
cp  -f ./litemall-db/sql/litemall_schema.sql ./deploy/litemall-db/litemall_schema.sql
cp  -f ./litemall-db/sql/litemall_table.sql ./deploy/litemall-db/litemall_table.sql
cp  -f ./litemall-db/sql/litemall_data.sql ./deploy/litemall-db/litemall_data.sql

cd ./litemall-admin
# 安装阿里node镜像工具
npm install -g cnpm --registry=https://registry.npm.taobao.org
# 安装node项目依赖环境
cnpm install
cnpm run build:dep
cd ..
# 压缩litemall-admin应用
tar -zcvf ./deploy/litemall-admin/dist.tar -C ./litemall-admin/dist .

# 复制三个Spring Boot应用
# 需要注意的是target目录里面存在两种jar，一种是当前模块纯编译代码的jar，另外一种是包含依赖库的可执行jar，
# 这里我们需要的是可执行jar
mvn clean
mvn package
cp  -f ./litemall-wx-api/target/litemall-wx-api-*-exec.jar ./deploy/litemall-api/litemall-wx-api.jar
cp  -f ./litemall-admin-api/target/litemall-admin-api-*-exec.jar ./deploy/litemall-api/litemall-admin-api.jar