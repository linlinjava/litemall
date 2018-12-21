#!/bin/bash

## 该脚本将docker所使用的配置文件打包，一并上传到云主机

cd $LITEMALL_HOME/docker
echo '复制mysql文件...'
cat $LITEMALL_HOME/litemall-db/sql/litemall_schema.sql $LITEMALL_HOME/litemall-db/sql/litemall_table.sql $LITEMALL_HOME/litemall-db/sql/litemall_data.sql >> mysql/init-sql/init.sql
echo '打包上传文件...'
rm -rf litemall.tar.gz
tar czf litemall.tar.gz nginx mysql litemall htmladmin docker-compose.yml

cd -
echo '上传文件...'
scp -P $PORT $LITEMALL_HOME/docker/litemall.tar.gz $USERNAME@$IP:/opt1
