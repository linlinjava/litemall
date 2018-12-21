## 使用docker部署
### 安装docker
参考https://docs.docker.com/install/

### 设置环境变量

在当前工程目录下打开终端,windows 使用Git Bash, linux下任选终端。输入
```
export LITEMALL_HOME=$PWD   # 设置根目录
export USERNAME=            # 设置登录云主机的用户名
export IP=                  # 设置云主机的ip
export PORT=22              # 设置云主机的ssh端口
```

### 上传docker配置文件

`sh $LITEMALL_HOME/docker/init.sh`

登录云主机
```
ssh -p $PORT  $USERNAME@$IP << EOF
cd /opt1 && tar xzvf litemall.tar.gz -C /opt1 
EOF
```

### 打包

1.后台服务打包

```
## 指定profile为生产环境，跳过测试
cd $LITEMALL_HOME && mvn clean package -Pprod -DskipTests
sh $LITEMALL_HOME/docker/upload.sh 1
```
2.后台管理页面打包

```
cd $LITEMALL_HOME/litemall-admin && npm run build:prod # 如果没有安装依赖，那么执行npm install 安装依赖
sh $LITEMALL_HOME/docker/upload.sh 2
```


### 运行
登录云主机`ssh -p $PORT  $USERNAME@$IP`
```

# 运行
cd /opt1 && docker-compose up -d
# 查看状态
docker-compose ps

# 如果litemall_admin_api_1的状态一直是restarting,那么可以
docker-compose restart 
```
