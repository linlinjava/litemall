# 4 litemall管理后台

项目技术架构：

* 后台管理前端，即litemall-admin模块
  * vue
  * vuex
  * vue-router
  * axios
  * element
  * vue-element-admin 3.9.3
  * 其他，见package.json
* 后台管理后端, 即litemall-admin-api模块
  * Spring Boot 2.x
  * Spring MVC

目前存在的问题：

* `缺失`首页中实现一些小组件，同时点击能够跳转相应页面
* `缺失`支持导出表所有数据
* `改善`管理员登录页面打开慢，优化速度
* `改善`地址优化，目前每一次点击都会请求后台，应该缓存已有的数据
* `改善`vue和vue-element-admin等及时更新
* `功能`系统角色和权限
* `功能`系统日志功能
* `功能`系统数据字典功能
* `功能`系统栏目管理功能
* `功能`支持数据库备份

## 4.1 litemall-admin-api

本节介绍管理后台的后台服务模块。

### 4.1.1 授权服务

见AdminAuthController类

### 4.1.2 用户管理服务

用户管理服务进一步分成：
* 会员管理服务，见AdminUserController类
* 收货地址服务，见AdminAddressController类
* 会员收藏服务，见AdminCollectController类
* 会员足迹服务，见AdminFootprintController类
* 搜索历史服务，见AdminHistoryController类
* 意见反馈服务，见AdminFeedbackController类

### 4.1.3 商场管理服务

商城管理服务进一步分成：
* 行政区域服务，见AdminRegionController类
* 品牌制造商服务，见AdminBrandController类
* 商品类目服务，见AdminCategoryController类
* 订单管理服务，见AdminOrderController类
* 通用问题服务，见AdminIssueController类
* 关键词服务，见AdminKeywordController类

### 4.1.4 商品管理服务

商品服务，见AdminAdminController类

### 4.1.5 推广管理服务

推广管理服务进一步分成：

* 广告服务，见AdminAdController类
* 专题服务，见AdminTopicController类
* 团购服务，见AdminGrouponController类

### 4.1.6 系统管理服务

系统管理服务进一步分成：
* 管理员服务，见AdminAdminController类
* 对象存储服务，见见AdminStorageController类

### 4.1.7 其他服务

* 统计服务，见AdminStatController类
* 个人服务，见AdminProfileController类

### 4.1.8 安全

#### 4.1.8.1 Token

用户登录成功以后，后端会返回`token`，之后用户的请求都会携带token。

目前token的失效和跟新机制没有涉及。

#### 4.1.8.2 CROS

如果litemall-admin-api不配置CROS，则Spring Boot会失败。

#### 4.1.8.3 账号密码加盐

如果是微信登录，那么无需账号和密码。

而如果用户采用了账号和密码的形式登录，那么后端需要把用户密码加盐。

#### 4.1.8.4 限制登录

如果采用账号密码登录，那么登录失败一定次数，应该限制登录。

进一步地，如果项目启用了短信功能，应该短信提醒用户，防止他人登录。

目前这里没有实现，仅列出。

### 4.1.9 定时任务

AdminOrderController类存在以下三个方法，其实是三个定时任务：
* checkOrderUnpaid
* checkOrderUnconfirm
* checkOrderComment

注意：
> 虽然定时任务放在AdminOrderController类中，但是可能这里不是很合适，
> 以后需要调整或者完善。

### 4.1.10 事务管理


## 4.2 litemall-admin

本节介绍管理后台的前端模块。

litemall-admin模块的代码基于[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)

## 4.3 开发新组件

这里介绍开发一个新的组件的流程。