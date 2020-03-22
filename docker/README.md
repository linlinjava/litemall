## docker

### 项目打包

1. 在服务器或者开发机打包项目到docker；
    ```
    cd litemall
    cat ./litemall-db/sql/litemall_schema.sql > ./docker/db/init-sql/litemall.sql
    cat ./litemall-db/sql/litemall_table.sql >> ./docker/db/init-sql/litemall.sql
    cat ./litemall-db/sql/litemall_data.sql >> ./docker/db/init-sql/litemall.sql
    
    cd ./litemall-admin
    cnpm install
    cnpm run build:dep
    
    cd ..
    mvn clean package
    cp -f ./litemall-all/target/litemall-all-*-exec.jar ./docker/litemall/litemall.jar
    ```
    这里的工作是：
    1. 把数据库文件拷贝到docker/db文件夹
    2. 编译litemall-admin项目
    3. 编译litemall-all模块，同时把litemall-admin编译得到的静态文件拷贝到
       litemall-all模块的static目录
       
2. 修改litemall文件夹下面的*.yml外部配置文件，当litemall-all模块启动时会
    加载外部配置文件，而覆盖默认jar包内部的配置文件。
    例如，配置文件中一些地方需要设置成远程服务器的IP地址
    
此时docker部署包结构如下：

* bin

存放远程服务器运行的脚本，包括deploy.sh脚本和reset.sh脚本

* db

存放litemall数据库文件

* litemall

存放远程服务器运行的代码，包括litemall-all二进制可执行包和litemall外部配置文件

* util

存放开发服务器运行的脚本，包括package.sh脚本和lazy.sh脚本。
由于是本地开发服务器运行，因此开发者可以不用上传到远程服务器。

* docker-compose.yml

docker-compose配置脚本，运行docker-compose命令会

### 项目部署

1. 云服务器环境安装docker和docker-compose（MySQL和JDK1.8无需安装，因为使用docker自动安装）。
   此外请确保云服务器的安全组已经允许相应的端口。

2. 运行docker-compose
    ```bash
    cd /home/ubuntu/docker
   sudo docker-compose
    ```

3. 测试是否部署成功(xxx.xxx.xxx.xxx是云服务器IP）：
    ```
    http://xxx.xxx.xxx.xxx:8080/wx/index/index
    http://xxx.xxx.xxx.xxx:8080/admin/index/index
    http://xxx.xxx.xxx.xxx:8080/#/login
    ```

### 项目辅助脚本

在前面的项目打包和项目部署中都是采用手动命令来部署。
这里可以写一些脚本简化：

* util/packet.sh

在开发服务器运行可以自动项目打包

* util/lazy.sh

在开发服务器运行可以自动项目打包、项目上传远程服务器、自动登录系统执行项目部署脚本。
    
注意：
> 1. 开发者需要在util/lazy.sh中设置相应的远程服务器登录账号和密钥文件路径。
> 2. 开发者需要在bin/reset.sh设置远程服务器的MySQL的root登录账户。
    
* bin/deploy.sh

在远程服务器运行可以自动部署服务

* bin/reset.sh

在远程服务器运行可以自动项目导入数据、删除本地上传图片、再执行bin/deploy.sh部署服务。

注意：
> 开发者需要在bin/reset.sh设置远程服务器的MySQL的root登录账户。

总结，当开发者设置好配置信息以后，可以在本地运行lazy.sh脚本自动一键部署:
```bash
cd litemall
./docker/util/lazy.sh
```

不过由于需要设置的信息会包含敏感安全信息，强烈建议开发者参考这里的docker文件夹，
然后实现自己的docker文件夹，妥善处置外部配置文件和脚本中的敏感安全信息!!!