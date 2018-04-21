#!/bin/bash

# 请注意
# 1. 本脚本的作用是把本项目编译的结果保存到deploy文件夹中，然后上传到云主机
# 2. 运行本脚本前，请确认Spring Boot模块已经编译，同时litemall-admin模块也已经便宜
# 3. util/upload.sh脚本是运行在开发机中，bin/deploy.sh脚本是运行在云主机中
# 4. 这是一个简单的脚本，用户可以按照自己需求修改

# 请设置云主机的IP地址
CVM=XXX.XXX.XXX.XXX
# 请设置本地SSH私钥文件id_rsa
ID_RSA=/XXX/id_rsa

# 复制三个Spring Boot应用
# 需要注意的是target目录里面存在两种jar，一种是当前模块纯编译代码的jar，另外一种是包含依赖库的可执行jar，
# 这里我们需要的是可执行jar
cp  -f ./litemall-os-api/target/litemall-os-api-*-exec.jar ./deploy/litemall-os-api/litemall-os-api.jar
cp  -f ./litemall-wx-api/target/litemall-wx-api-*-exec.jar ./deploy/litemall-wx-api/litemall-wx-api.jar
cp  -f ./litemall-admin-api/target/litemall-admin-api-*-exec.jar ./deploy/litemall-admin-api/litemall-admin-api.jar

# 压缩litemall-admin应用
tar -zcvf ./deploy/litemall-admin/dist.tar -C ./litemall-admin/dist .

# 复制数据库
cp  -f ./litemall-db/sql/litemall_schema.sql ./deploy/litemall-db/litemall_schema.sql
cp  -f ./litemall-db/sql/litemall.sql ./deploy/litemall-db/litemall.sql

# 上传云主机
scp -i $ID_RSA -r  ./deploy ubuntu@$CVM:/home/ubuntu/
