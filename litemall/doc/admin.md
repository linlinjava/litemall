# 4 litemall管理后台

项目技术架构：

* 管理后台前端，即litemall-admin模块
  * vue
  * vuex
  * vue-router
  * axios
  * element
  * vue-element-admin 3.9.3
  * 其他，见package.json
* 管理后台后端, 即litemall-admin-api模块
  * Spring Boot 2.x
  * Spring MVC

目前存在的问题：

* `缺失`首页中实现一些小组件，同时点击能够跳转相应页面
* `缺失`支持导出表所有数据
* `改善`管理员登录页面打开慢，优化速度
* `改善`地址优化，目前每一次点击都会请求后台，应该缓存已有的数据
* `改善`vue和vue-element-admin等及时更新
* `功能`系统数据字典功能
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

这里的安全基于Shiro。

#### 4.1.8.1 认证

#### 4.1.8.2 账号密码加盐

如果是微信登录，那么无需账号和密码。

而如果用户采用了账号和密码的形式登录，那么后端需要把用户密码加盐。

#### 4.1.8.3 权限控制

后端实现权限管理功能来支持一定的安全控制。
具体细节见4.1.9。

### 4.1.9 权限管理

权限管理只完成了操作权限的功能，而数据权限的功能未完成。

#### 4.1.9.1 权限设计

权限控制在数据库层面涉及到三个表`litemall_admin`, `litemall_role`和`litemall_permission`：
* litemall_admin表中存在roleId字段，保存角色ID数组；
* litemall_role表记录角色名称和角色介绍；
* litemall_permission表记录角色所用于的权限值。

权限控制在后端层面通过这三个表可以构建出管理员所属的角色以及所拥有的操作权限。
当管理员登录以后，访问一些受权限保护的后端地址时，后端会验证当前管理员的操作权限和后端地址需要的操作权限；
如果不匹配则会抛出异常，然后前端就会收到无操作权限的提示信息。

权限控制在前端层面可以简单地把无操作权限显示给用户即可。
但是前端可以进一步优化完成菜单权限特性和按钮权限特性：
* 菜单权限，即管理员登录以后，前端的菜单是自动生成的；
* 按钮权限，即管理员点击菜单跳转到页面，而页面中只出现当前管理员可操作的按钮。

以上内容参考了网上资料和开源项目，但是实现细节方面会存在一些出入。

##### 4.1.9.1.1 三个表，而不是五个表

通常是五个表，user, role, user_role, permission, role_permission。

但是本项目只需要三个表：
* user_role表的关联关系，可以通过user表的roles字段完成，因此可以省略；
* permission表省略，这里可能是非常奇怪的做法，但是实际上是可行的。

很多开源项目的permission表是记录当前系统的所有权限，最终呈现给管理后台的使用者；
但是数据来源则是开发者或者系统用户来进行数据输入的，但是这样真的合理吗？
* 开发者开发完系统以后，需要额外在数据库中写入权限相关内容，这样存在独立两个步骤
  可能不是很好；此外，如果系统升级，完成新的权限，那么如何添加这些权限到数据库也
  不是很好。
* 如果开发者设计权限页面，支持系统管理员手动添加新的权限，那样其实也不合适，
  因为系统管理员可能对系统的权限并不了解，例如url地址所需要的权限。
  这里很多开源项目都是采取这种方法，但是实际上管理员可能根本不会理解或者使用。

这里本项目参考了[biu](https://github.com/CaiBaoHong/biu)项目中注解的方式，
系统的所有的权限不是通过数据库中权限表数据获取，而是通过注解自动解析生产当前系统
所有的权限。而且因为是注解，所以开发者在开发新的权限时，只需要在代码内直接书写，
不需要再次在数据库中写入。

当然，这里并意味说三个表就好或者五个表就不好，开发者可以按照自己的理解来做。

本项目具体如何实现见下文细节。

##### 4.1.9.1.2 权限只有一种类型，而不是三种类型

在[biu](https://github.com/CaiBaoHong/biu)项目中明确了存在三种权限类型，分别是
菜单权限元数据、按钮权限元数据、接口权限元数据。
很明显地，前两种权限对应了前端权限，而接口权限元数据就是后端权限。

本项目没有采用这种理念，其原因是因为对于最终管理员而言，或者说有可能是不懂IT的
普通管理员，前端权限页面存在三个权限给用户勾选，可能反而会造成困惑。实际上，
前端页面建议还是应该出现一种权限效果。

本项目对管理员而言只有一种权限，但是这个权限本身对应了菜单权限元数据、按钮权限元数据、接口权限元数据。
因此，管理员勾选一个权限以后，后台权限即授权成功，同时前端的菜单权限和按钮权限也自动调整。
具体实现细节见下文。

后端权限基于shiro来实现，相关代码见litemall-admin-api模块。

##### 4.1.9.2 基本配置

1. config子包的`ShiroConfig`引入了Shiro并配置了shirFilter、realm和sessionManager；
2. shiroFilter配置只允许少量url可以匿名访问，其他url都需要登录才能访问；
3. realm设置的是shiro子包的`AdminAuthorizingRealm`类，该类作用是认证和授权的功能；
4. sessionManager设置的是shiro子包的`AdminWebSessionManager`类，该类作用是重置会话管理器。
   默认会话管理器是基于cookie实现会话保持，而这里是基于自定义头部实现会话保持。

经过以上步骤，shiro就配置正常。
* 当管理员登录时，先认证；
* 认证成功，则授权，在后端内保存roles和permissions；同时，在响应头部写入自定义头部和sessionId；
* 认证失败，则抛出认证异常；
* 管理员再次访问页面时，shiro通过自定义头自动认证成功。

##### 4.1.9.3 权限校验

但是以上只完成授权功能，而没有完成权限校验功能。
这里开发者应该在Controller类的方法中使用`RequiresPermissions`注解。
例如:
```
@RestController
@RequestMapping("/admin/ad")
public class AdminAdController {

    @RequiresPermissions("admin:ad:list")
    @RequiresPermissionsDesc(menu={"推广管理" , "广告管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallAd> adList = adService.querySelective(name, content, page, limit, sort, order);
        int total = adService.countSelective(name, content, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adList);

        return ResponseUtil.ok(data);
    }
}
```

而具体的权限校验逻辑则由shiro自动完成：
1. 登录成功或者会话登录，shiro已经有当前用户的权限列表；
2. 访问权限保护的方法时，shiro通过`RequiresPermissions`注解得到所需操作权限列表；
3. 测试已分配的权限和操作所需权限是否一致，如果一致则可以调用方法，否则抛出无权限的异常。

##### 4.1.9.4 权限描述

在annotation子包中存在一个自定义的`RequiresPermissionsDesc`注解
```
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissionsDesc {
    String[] menu();
    String button();
}
```

这里讨论一下为什么要自定义权限描述注解？
> 当后端设计好权限以后，需要向前端发送系统的权限情况，管理员可以查看系统当前所有权限以及允许为一个角色配置权限。
> 这里就带来了实现方面的问题:
> 一些开源项目在数据库设计一个permission表，记录系统所有权限，但是4.1.9.1.1节讨论了这种方式的局限性。
> 因此本项目采用权限注解方式，要求开发者在使用权限注解（RequiresPermissions）的地方应该同时使用权限文档注解（RequiresPermissionsDesc），
> 这样系统能够通过权限注解自动生成权限列表，向前端返回可读的信息。

例如:
```
@RestController
@RequestMapping("/admin/ad")
public class AdminAdController {

    @RequiresPermissions("admin:ad:list")
    @RequiresPermissionsDesc(menu={"推广管理" , "广告管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallAd> adList = adService.querySelective(name, content, page, limit, sort, order);
        int total = adService.countSelective(name, content, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adList);

        return ResponseUtil.ok(data);
    }
}
```


本项目通过搜索`RequiresPermissions`注解和`RequiresPermissionsDesc`注解（见util子包的PermissionUtil类），
在内部生产了`Permission`类的集合:
```
public class Permission {
    private RequiresPermissions requiresPermissions;
    private RequiresPermissionsDesc requiresPermissionsDesc;
    private String api;
}
```

也就是说，对于一个权限，既有权限值（requiresPermissions的value），也有权限对应的操作API，
也有权限的描述内容（requiresPermissionsDesc的menu和button）。

前端权限所需要的一些权限内容可以从这里来间接产生，具体方式见4.2.1节。

当然，需要指出的是，这里利用注解方式可以不需要在数据库中保存权限信息，
但是在灵活性方面可能也会有问题。

### 4.1.10 定时任务

job子包存在以下定时任务：
* OrderJob类
  * checkOrderUnpaid
  * checkOrderUnconfirm
  * checkOrderComment
* CouponJob类
  * checkCouponExpired

注意：
> 虽然定时任务放在AdminOrderController类中，但是可能这里不是很合适，
> 以后需要调整或者完善。

## 4.2 litemall-admin

本章介绍管理后台的前端模块。

litemall-admin模块的代码基于[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)


#### 4.2.1 前端权限

需要再次明确的是，前端权限的作用仅仅是隐藏菜单或者页面按钮，优化用户使用体验，
而实际上没有真正的权限保护功能。

这里前端校验的思路可以参考vue-element-admin的文档:
* [路由和侧边栏](https://panjiachen.github.io/vue-element-admin-site/zh/guide/essentials/router-and-nav.html)
* [权限验证](https://panjiachen.github.io/vue-element-admin-site/zh/guide/essentials/permission.html)

##### 4.2.1.2 角色页面

管理员的角色页面用于创建角色和分配权限。

其中点击授权按钮，则会出现系统所有的权限和当前已经分配的权限,具体代码可以见AdminRoleController的getPermissions。
* 系统所有的权限，可以通过后端自动解析权限描述注解来获取；
* 当前已分配权限，可以通过数据库访问来获取。

#### 4.2.1.3  权限API

vue-element-admin中权限校验是基于role完成的权限校验；
而其他一些开源项目中权限校验是基于permission权限值完成的权限校验；
而本项目进行了调整，基于permission URL完成权限校验。

首先讨论为什么基于role的权限校验不是很好：

vue-element-admin的权限校验要求在菜单或者页面按钮中写上role信息，
这里原理是可以的。但是存在一个问题，就是角色是管理员动态创建的，
因此菜单或者页面按钮直接写死访问所需要的role，不利于后期维护升级。

接着讨论为什么基于permission权限值的权限校验不是很好：

在前后端分离项目中，前端和后端应该是不同的开发者开发。如果采用权限值的方式，
可能导致前后端耦合紧密的问题。
例如，后端开发者开发了一个API是`/admin/ad/list`，后端访问所需的权限值是`admin:ad:list`，
此时前端也需要写菜单或者页面按钮的地方写上`admin:ad:list"`。
当然，这里原理上和操作上都是可行的。但是，这里要求前端开发者必须知道自己访问后端
API所对应的操作权限值。因此如何以一种好的前后端分离的方式来做可能是一个问题。

本项目则基于permission URL的权限校验方式：

后端权限校验逻辑仍然是权限值，而前端权限校验逻辑则是URL。
例如，后端开发者开发了一个API是`/admin/ad/list`，后端访问所需的权限值是`admin:ad:list`，
此时前端则不需要考虑权限值，而是仅仅需要知道自己访问API的方式，也就是在
菜单或者页面按钮的地方写上`GET /admin/ad/list`，而不是`admin:ad:list"`。
之所以是可行的，是因为后端权限是一个（权限值，权限描述，权限访问API）构成的Permission类。

当然，这里也存在一定的局限性。

#### 4.2.1.4 菜单权限

见`src/route/index.js`代码

例如
```
  {
    path: '/promotion',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'promotionManage',
    meta: {
      title: '推广管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'ad',
        component: () => import('@/views/promotion/ad'),
        name: 'ad',
        meta: {
          perms: ['GET /admin/ad/list', 'POST /admin/ad/create', 'GET /admin/ad/read', 'POST /admin/ad/update', 'POST /admin/ad/delete'],
          title: '广告管理',
          noCache: true
        }
      }
      }
    ]
  },
```

在菜单的meta中的perms属性声明了当前菜单所需要的操作权限。
如果管理员登录以后，所分配的操作权限存在至少一个，那么该菜单就会显示；
否则该菜单就会隐藏。


##### 4.2.1.4 按钮权限

在每个页面中一些组件可以采用指令`v-permission`或者函数`checkPermission`来实现按钮权限。

例如，`src/views/promation/ad`页面中
```
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入广告标题"/>
      <el-input v-model="listQuery.content" clearable class="filter-item" style="width: 200px;" placeholder="请输入广告内容"/>
      <el-button v-permission="['GET /admin/ad/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/ad/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>
```

element的`el-button`组件声明了操作权限是`GET /admin/ad/list'`。
如果管理员登录以后，所分配的操作权限匹配，那么该按钮就会显示；
否则该按钮就会隐藏。

##### 4.1.1.5 权限局限性

前端权限这里的代码调整旨在解决一些认为不合理的地方，但是实际上也同时带来了
一定的局限性或者限制。

这里列出一些可能的问题，开发者可以自己审阅或者重新设计代码。

## 4.3 开发新组件

本章节介绍如何开发新的管理后台功能。

### 4.3.1 管理后台前端页面

### 4.3.2 前后端交互服务API

### 4.3.3 管理后台后端服务

### 4.3.4 数据库