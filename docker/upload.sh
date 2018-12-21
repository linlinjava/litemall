#!/bin/bash

## 该脚本上传jar包

if [[ "$1" == '1' ]]; then

scp -P $PORT $LITEMALL_HOME/litemall-wx-api/target/litemall-wx-api-exec.jar $USERNAME@$IP:/opt1/litemall/litemall-wx-api-exec.jar
scp -P $PORT $LITEMALL_HOME/litemall-admin-api/target/litemall-admin-api-exec.jar $USERNAME@$IP:/opt1/litemall/litemall-admin-api-exec.jar

elif [[ "$1" == '2' ]]; then
scp -P $PORT -r $LITEMALL_HOME/litemall-admin/dist/* $USERNAME@$IP:/opt1/htmladmin
else
echo '请输入上传类型， 1 代表后台服务，2 代表后台管理页面。 如 sh upload.sh 1 '
fi;
