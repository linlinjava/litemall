
1. 项目进一步打包到deploy文件夹中：
   * litemall-os-api模块编译得到的litemall-os-api-0.1.0-exec.jar 保存到deploy的litemall-api文件夹中，同时重命名成litemall-os-api.jar
   * litemall-wx-api模块编译得到的litemall-wx-api-0.1.0-exec.jar 保存到deploy的litemall-api文件夹中，同时重命名成litemall-wx-api.jar
   * litemall-admin-api模块编译得到的litemall-admin-api-0.1.0-exec.jar 保存到deploy的litemall-api文件夹中，同时重命名成litemall-admin-api.jar
   * litemall-admin模块编译以后，把dist文件夹压缩，然后放到deploy的litemall-admin文件夹中。
   * litemall-db模块的sql文件拷贝到deploy的litemall-db文件夹中。

2. 使用FileZilla把deploy整个文件夹上传到云主机的/home/ubuntu文件夹中

3. 使用PuTTY登录云主机

4. 如果开发者没有部署litemall数据库，可以运行以下命令：

    ```bash
    cd deploy
    mysql -h localhost -u root -p123456 
    source ./litemall-db/litemall_schema.sql
    source ./litemall-db/litemall_table.sql
    source ./litemall-db/litemall_data.sql
    ```
    注意，123456是开发者所设置的MySQL管理员密码
    警告：
    > litemall_schema.sql会尝试删除litemall数据库然后重建一个新的数据。

5. 运行脚本部署运行

    ```bash
    sudo ./deploy/bin/deploy.sh
    ```

6. 测试部署是否成功
  
    请确保云主机的安全组已经允许相应的端口（见1.5.3.1）；
    然后测试是否部署成功(xxx.xxx.xxx.xxx是云主机IP）：

    ```
    http://xxx.xxx.xxx.xxx:8081/os/index/index
    http://xxx.xxx.xxx.xxx:8082/wx/index/index
    http://xxx.xxx.xxx.xxx:8083/admin/index/index
    http://xxx.xxx.xxx.xxx:8080/#/login
    ```

7. 部署脚本

    为了简化步骤1和步骤2，完成了deploy/util/package.sh上传脚本和deploy/util/lazy.sh部署脚本，
    
    注意：
    > * 开发者需要在deploy/util/package.sh和deploy/util/lazy.sh中设置相应的云主机登录账号和密钥文件路径。
    > * 开发者需要在deploy/util/reset.sh设置云主机的MySQL的root登录账户。
    > * 请先执行上述1-6步骤，确保部署环境成功。
    
    * 上传脚本
    
    该脚本会自动把当前项目不同模块下的最终部署文件复制到deploy文件夹中，然后上传到云主机。
    该上传脚本没有自动做Spring Boot项目打包和Vue项目打包工作
    
    * 部署脚本
    
    该脚本会编译项目，再上传deploy文件，最后ssh登录远程主机执行bin下面的deploy.sh脚本。
    
    注意，运行命令必须在项目主目录中，类似如下命令：
    ```bash
    cd litemall
    ./deploy/util/lazy.sh
    ```
