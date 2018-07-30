# 常见问题

## 小商城

### 小程序微信登录失败

原因：

目前账号的appid是本人申请，同时小程序未上线，因此开发者如果微信登录肯定会失败。

解决方案：

1. 如果只是体验商品购买流程，开发者可以采用账号注册登录方式。
2. 开发者在litemall-wx和litemall-wx-api模块的appid等信息设置成自己申请的信息。

#### appid已经修改，微信登陆仍然失败

现象：

微信开发者工具修改了开发者自己申请的appid，后台也更新了相应信息，但是微信登录仍然报错。

原因：

这里可能是缓存问题，虽然修改了appid，但是微信开发者工具未及时跟新。

解决方案：

微信开发者工具中修改appid以后，请关闭litemall-wx项目或者微信开发者工具，重新启动导入litemall-wx。

### 手机真机测试不正常

现象：

微信开发者工具打开正常，但是手机真机扫描加载小商场以后，只有页面结构，没有数据和图片。

原因：

数据或者图片不可访问。

解决方案：

1. 确保小商场后台服务可以访问，可以通过手机浏览器访问后台服务地址测试
   * 小商场的后台服务地址是localhost，则手机不可访问；
   * 小商场的后台服务地址是局域网地址，而手机不在局域网中（例如不是相同wifi，或者手机是移动网络）
   * 小商场的后台服务未启动
2. 手机小商场的**调试功能**开启

### 第三方手机测试不正常

现象：

本人手机测试正常，而第三者手机测试不正常。

解决方案：

1. 确保小商场后台服务可以访问，可以通过第三者手机浏览器访问后台服务地址测试
2. 第三者手机小商场的**调试功能**开启
3. 在微信小程序平台设置第三者的微信账号是**体验者**

### 微信支付失败

现象：

小商场不能支付，或者点击支付总是报错

原因：

开发者必须拥有商户支付权限，然后设置好以下信息：
```
litemall.wx.app-id=
litemall.wx.app-secret=
litemall.wx.mch-id=
litemall.wx.mch-key=
litemall.wx.notify-url=
```

解决方案：

参考`3.0 小商场环境`，设置相应支付配置信息

### 支付成功，但商品仍未付款

现象：

在微信开发者工具中已经成功支付，但是返回订单页面时商品订单仍然显示`未付款`状态。

原因是：

微信平台支付成功以后，会把支付结果推送到`wx.notify-url`所指定的地址。
因此，开发者必须确定`wx.notify-url`所指向的访问链接是可以成功访问，同时
能够返回正常的响应结果。

解决方案：

1. 如果开发者是在微信开发者工具中测试支付，那么需要采用一些内网穿透工具，
把`WxOrderController.payNotify`所代表的本地地址，例如`http://localhots/wx/order/pay-notify`,
转换成外网可以访问的地址，例如`http://xxx.com/wx/order/pay-notify`，最后
设置`wx.notify-url`指向该地址。

2. 如果开发者已经上线服务，请确认`wx.notify-url`所指向的访问链接可以正常工作。

## 管理后台

### Invalid bound statement

现象：

有时（特别是采用mybatis generator重新生成代码）后台服务报错

```
org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): org.linlinjava.litemall.db.dao.XXXX
```

原因：

应该是自动生成的新的XML文件没有及时跟新到编译文件夹target中，造成了target中mybatis的Java代码和XML文件不对应。

解决方案：

采用maven命令或者插件先清理项目再重新编译打包，例如
```bash
mvn clean
mvn package
```

## 基础系统

## 项目

### 项目导入IDEA时卡顿

现象：

IDEA导入项目时，非常耗时间，或者卡断，或者一直疯狂运行。

原因：

应该是litemall-admin模块的node_modules文件夹导致的。
node_modules是litemall-admin所依赖的项目库，可能有近200M的文件。
而IDEA如果没有设置，则可能尝试对该文件夹进行解析索引，从而导致卡断。

解决方案：

右键把node_modules设置Excluded
![](./pic/excluded.png)    
