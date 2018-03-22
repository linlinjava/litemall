
1. 项目进一步打包到deploy文件夹中：
   * litemall-os-api模块编译得到的litemall-os-api-0.1.0.jar 保存到deploy的litemall-os-api文件夹中，同时重命名成litemall-os-api.jar
   * litemall-wx-api模块编译得到的litemall-wx-api-0.1.0.jar 保存到deploy的litemall-wx-api文件夹中，同时重命名成litemall-wx-api.jar
   * litemall-admin-api模块编译得到的litemall-admin-api-0.1.0.jar 保存到deploy的litemall-admin-api文件夹中，同时重命名成litemall-admin-api.jar
   * litemall-admin模块编译以后，把dist文件夹压缩，然后放到deploy的litemall-admin文件夹中。
   
2. 使用FileZilla把deploy整个文件夹上传到云主机的/home/ubuntu文件夹中

3. 使用PuTTY登陆云主机

4. 运行脚本部署运行

    ```bash
    sudo ./deploy/bin/deploy.sh
    ```

5. 测试部署是否成功
请确保litemall的Spring Boot应用模块所对应的端口已经打开；
然后测试是否能够访问(xxx.xxx.xxx.xxx是云主机IP）：

  > http://xxx.xxx.xxx.xxx:8081/storage/index/index
  > http://xxx.xxx.xxx.xxx:8082/wx/index/index
  > http://xxx.xxx.xxx.xxx:8083/admin/index/index