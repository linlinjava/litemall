这里的数据库由三个sql文件组成：

1. litemall_schema.sql
  
   作用是创建空数据库、创建用户、设置访问权限。
   
   开发者开发测试阶段可以使用，但是部署生产阶段一定要注意修改这里的默认用户名和密码。

2. litemall_table.sql

   作用是创建数据库表，但是没有创建任何数据。
   
   因此，开发者可以在部署生产阶段直接使用。
 
3. litemall_data.sql

   作用是创建测试数据。
   
   这里的测试数据来自开源项目[nideshop-mini-program](https://github.com/tumobi/nideshop-mini-program)
   
   开发者开发测试阶段可以使用，但是部署开发阶段应该使用自己的数据。