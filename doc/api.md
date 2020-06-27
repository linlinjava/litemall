# 前后端接口文档

本项目前后端接口规范和接口文档。

注意：
> 1. 以下API部分基于nideshop开源项目的API设计；
> 2. 以下API是参考API，可能不是很合理，欢迎开发者交流。
> 3. 接口文档处于开发中，如果发现接口描述和接口实际不对应，欢迎PR或者报告。

## 1 前后端接口规范

### 1.1 请求格式

这里没有采用RESTful风格的接口，而是定义具体语义的接口。
目前只使用`GET`和`POST`来表示请求内容和更新内容两种语义。

#### 1.1.1 GET请求

    GET API_URL?params

例如
    
    GET /home/index

或者
    
    GET /goods/list?page=1&limit=10

#### 1.1.2 POST更新
    
    POST API_URL
    {
        body
    }

例如
    
    POST /cart/clear

或者    
    
    POST /goods/star
    {
        id: 1
    }

#### 1.1.3 分页请求参数

当GET请求后端获取数组数据时，需要传递分页参数。

例如

    GET /goods/list?page=1&limit=10&sort=add_time&order=desc
    
本项目的通用分页请求参数统一传递四个：

    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序

* page, 和通常计算机概念中数组下标从0开始不同，这里的page参数应该从1开始，1即代表第一页数据;
* limit, 分页大小；
* sort, 例如"add_time"或者"id";
* order, 只能是"desc"或者"asc"。

此外，这里四个参数是可选的，后端应该设置默认参数，因此即使前端不设置，
后端也会自动返回合适的对象数组响应数据。

注意:
> 这里的参数是需要后端支持的，在一些场景下，例如数组对象是组装而成，
> 有可能sort和order不支持。

讨论：
> 有些请求后端是所有数据，这里page和limit可能设置是无意义的。但是
> 仍然建议加上两个参数，例如page=1, limit=1000。

也就是说，请求后端数组数据时，同一传递四个分页参数，可能是比较良好的做法。

### 1.2 响应格式

    Content-Type: application/json;charset=UTF-8
    
    {
        body
    }
    

而body是存在一定格式的json内容：
    
    {
        errno: xxx,
        errmsg: xxx,
        data: {}
    }

#### 1.2.1 失败异常

    {
        errno: xxx,
        errmsg: xxx
    }

* errno是错误码，具体语义见1.3节。
* errmsg是错误信息。
    
#### 1.2.2 操作成功

    {
        errno: 0,
        errmsg: "成功",
    }

#### 1.2.3 普通对象

    {
        errno: 0,
        errmsg: "成功",
        data: {}
    }
    
#### 1.2.4 数组对象

    {
        errno: 0,
        errmsg: "成功",
        data: {
            list: [],
            total: XX，
            page: XX,
            limit: XX,
            pages: XX
        }
    }

list是对象数组，total是总的数量。

### 1.3 错误码

#### 1.3.1 系统通用错误码

系统通用错误码包括4XX和5XX

* 4xx，前端错误，说明前端开发者需要重新了解后端接口使用规范：
  * 401，参数错误，即前端没有传递后端需要的参数；
  * 402，参数值错误，即前端传递的参数值不符合后端接收范围。
  
* 5xx，后端系统错误，除501外，说明后端开发者应该继续优化代码，尽量避免返回后端系统错误码：
  * 501，验证失败，即后端要求用户登录；
  * 502，系统内部错误，即没有合适命名的后端内部错误；
  * 503，业务不支持，即后端虽然定义了接口，但是还没有实现功能；
  * 504，更新数据失效，即后端采用了乐观锁更新，而并发更新时存在数据更新失效；
  * 505，更新数据失败，即后端数据库更新失败（正常情况应该更新成功）。

#### 1.3.2 商场业务错误码

* AUTH_INVALID_ACCOUNT = 700
* AUTH_CAPTCHA_UNSUPPORT = 701
* AUTH_CAPTCHA_FREQUENCY = 702
* AUTH_CAPTCHA_UNMATCH = 703
* AUTH_NAME_REGISTERED = 704
* AUTH_MOBILE_REGISTERED = 705
* AUTH_MOBILE_UNREGISTERED = 706
* AUTH_INVALID_MOBILE = 707
* AUTH_OPENID_UNACCESS = 708
* AUTH_OPENID_BINDED = 709
* GOODS_UNSHELVE = 710
* GOODS_NO_STOCK = 711
* GOODS_UNKNOWN = 712
* GOODS_INVALID = 713
* ORDER_UNKNOWN = 720
* ORDER_INVALID = 721
* ORDER_CHECKOUT_FAIL = 722
* ORDER_CANCEL_FAIL = 723
* ORDER_PAY_FAIL = 724
* ORDER_INVALID_OPERATION = 725
* ORDER_COMMENTED = 726
* ORDER_COMMENT_EXPIRED = 727
* GROUPON_EXPIRED = 730
* COUPON_EXCEED_LIMIT = 740
* COUPON_RECEIVE_FAIL= 741
* COUPON_CODE_INVALID= 742
    
#### 1.3.3 管理后台业务错误码

* ADMIN_INVALID_NAME = 601
* ADMIN_INVALID_PASSWORD = 602
* ADMIN_NAME_EXIST = 602
* ADMIN_ALTER_NOT_ALLOWED = 603
* ADMIN_DELETE_NOT_ALLOWED = 604
* ADMIN_INVALID_ACCOUNT = 605
* GOODS_UPDATE_NOT_ALLOWED = 610
* GOODS_NAME_EXIST = 611
* ORDER_CONFIRM_NOT_ALLOWED = 620
* ORDER_REFUND_FAILED = 621
* ORDER_REPLY_EXIST = 622
* USER_INVALID_NAME = 630
* USER_INVALID_PASSWORD = 631
* USER_INVALID_MOBILE = 632
* USER_NAME_EXIST = 633
* USER_MOBILE_EXIST = 634
* ROLE_NAME_EXIST = 640
* ROLE_SUPER_SUPERMISSION = 641
* ROLE_USER_EXIST = 642

### 1.4 Token

前后端采用token来验证访问权限。

#### 1.4.1 Header&Token

前后端Token交换流程如下：

1. 前端访问商场登录API或者管理后台登录API;

2. 成功以后，前端会接收后端响应的一个token，保存在本地；
    
3. 请求受保护API则，则采用自定义头部携带此token

4. 后端检验Token，成功则返回受保护的数据。

#### 1.4.2 商场自定义Header

访问受保护商场API采用自定义`X-Litemall-Token`头部

1. 小商城（或轻商场）前端访问小商城后端登录API`/wx/auth/login`

        POST /wx/auth/login
    
        {
            "username": "user123",
            "password": "user123"
        }

2. 成功以后，前端会接收后端响应的一个token，

        {
          "errno": 0,
          "data": {
            "userInfo": {
              "nickName": "user123",
              "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            },
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0aGlzIGlzIGxpdGVtYWxsIHRva2VuIiwiYXVkIjoiTUlOSUFQUCIsImlzcyI6IkxJVEVNQUxMIiwiZXhwIjoxNTU3MzI2ODUwLCJ1c2VySWQiOjEsImlhdCI6MTU1NzMxOTY1MH0.XP0TuhupV_ttQsCr1KTaPZVlTbVzVOcnq_K0kXdbri0"
          },
          "errmsg": "成功"
        }    
    
3. 请求受保护API则，则采用自定义头部携带此token

        GET http://localhost:8080/wx/address/list
        X-Litemall-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0aGlzIGlzIGxpdGVtYWxsIHRva2VuIiwiYXVkIjoiTUlOSUFQUCIsImlzcyI6IkxJVEVNQUxMIiwiZXhwIjoxNTU3MzM2ODU0LCJ1c2VySWQiOjIsImlhdCI6MTU1NzMyOTY1NH0.JY1-cqOnmi-CVjFohZMqK2iAdAH4O6CKj0Cqd5tMF3M

#### 1.4.3 管理后台自定义Header

访问受保护管理后台API则是自定义`X-Litemall-Admin-Token`头部。

1. 管理后台前端访问管理后台后端登录API`/admin/auth/login`

        POST /admin/auth/login
    
        {
            "username": "admin123",
            "password": "admin123"
        }

2. 成功以后，管理后台前端会接收后端响应的一个token，

        {
            "errno": 0,
            "data": {
                "adminInfo": {
                    "nickName": "admin123",
                    "avatar": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
                },
                "token": "f2dbcae8-6e25-4f8e-bc58-aa81d512c952"
            },
            "errmsg": "成功"
        }
    
3. 请求受保护API时，则采用自定义头部携带此token

        GET http://localhost:8080/wx/address/list
        X-Litemall-Admin-Token: f2dbcae8-6e25-4f8e-bc58-aa81d512c952

### 1.5 版本控制

API应该存在版本控制，以保证兼容性。

由于仍处于开发中，因此目前未引入版本控制。

### 1.6 API格式

这里定义一个API的格式：

应用场景

    xxx
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx

### 1.7 API预览

接下来会分别从用户层面和管理员层面构建商场API服务和管理后台API服务。

商场API服务涉及

* 安全服务
* 首页服务
* 类目服务
* 商品服务
* 购物车服务
* 订单服务
* 会员服务
* 收货地址服务
* 品牌商服务
* 收藏服务
* 评论服务
* 优惠券服务
* 反馈服务
* 足迹服务
* 团购服务
* 帮助服务
* 搜索服务
* 专题服务
* 对象存储服务


管理后台API服务涉及:
* 略

### 1.8 API测试

本节以GET、POST两种方式以及是否需要登录举例说明如何测试和使用本项目API。

开发者可以使用各种API测试命令或者工具，这里以Postman作为工具。

#### 1.8.1 GET 示例

如果一个API是GET方法，那么请求参数需要在访问链接后面：

例如测试2.4.2节商品详情API

![](./pics/admin/get.png)

#### 1.8.2 GET & Token 示例

如果需要登录才能访问数据，则需要先向后端请求登录，得到token，然后请求时携带token。

例如测试2.8.1节收货地址列表API

如果没有登录，则返回未登录信息

![](./pics/admin/get_no_token.png)

因此测试这些API，需要先登录

![](./pics/admin/login.png)

然后，采用自定义`X-Litemall-Token`来携带token访问商场API

![](./pics/admin/get_with_token.png)

注意：
> 访问受保护商场API是采用自定义`X-Litemall-Token`头部；
> 而访问受保护管理后台API则是自定义`X-Litemall-Admin-Token`头部。

#### 1.8.3 POST 示例

通常POST请求后端时，都需要先登录才能有权限上传数据，因此这里不举例说明。

#### 1.8.4 POST & Token 示例

如果需要登录才能提交数据，则需要先向后端请求登录，得到token，然后请求时携带token。

![](./pics/admin/post_no_token.png)

因此测试这些API，需要先登录

![](./pics/admin/login.png)

然后，采用自定义`X-Litemall-Token`来携带token访问商场API

![](./pics/admin/get_with_token.png)

注意：
> 访问受保护商场API是采用自定义`X-Litemall-Token`头部；
> 而访问受保护管理后台API则是自定义`X-Litemall-Admin-Token`头部。

### 1.9 API保护

为了保护API不被滥用，通常API需要引入保护机制，例如OAuth2。

本项目暂时无保护机制，因此实际上一旦开发者知道服务器，就很容易访问API。

### 1.10 API局限性

当前API还存在一些问题，后面需要继续优化和完善。

* 无意义的通用字段

* 团购API完善

### 1.11 Not Like Swagger

本项目不是很接受Swagger，基于以下考虑：

* 前后端中立

在前后端分离项目中，依赖后端的Swagger来生成项目API似乎不是很理想，
这实际上把项目API设计工作过多地压在后端，同时前端也被迫依赖后端，
因为后端如果没有写好文档注解，前端不可能了解API的输入输出。

可能一种合理的做法应该这样：
项目初期前后端一起完成一个完整基本的API文档，定义好交互规范和具体API的行为，然后双方同时开始开发工作；
某个开发阶段，前端需要更多的数据或者新的API支持，此时也不需要立即联系后端（除非API产生破坏性变更），
而是暂时基于mock和自定义mock数据独立开发；之后，在合适阶段（可以按照项目规定，例如三天或者周五），
前后端再次沟通API的变更，后端了解需求后则可以接受、拒绝或者调整，当然变更必须要在API文档中体现和更新；
下一个开发阶段，前端和后端能够再次基于最新的API文档来调整自己代码。
最后项目测试时，只要前端对照API文档，后端也是对照API文档。

* 后端代码简洁

如果使用Swagger，为了得到完整的文档，需要在每一个方法前面加上多个文档注解，文档越是详尽，则注解越多，
造成代码不是很简洁。特别是具备代码属性的注解和Swagger文档注解混杂在一起，可能不是很好。

当然，本项目也简单地配置了Swagger(见`WxSwagger2Configuration`和`AdminSwagger2Configuration`)，
* 在线Swagger文档链接：http://122.51.199.160:8080/swagger-ui.html
* 本地Swagger文档链接：http://localhost:8080/swagger-ui.html

此外，也使用了swagger-bootstrap-ui对Swagger进一步增强了使用效果。
* 在线swagger-bootstrap-ui文档链接：http://122.51.199.160:8080/doc.html
* 本地swagger-bootstrap-ui文档链接：http://localhost:8080/doc.html

当然正如上文讨论，本项目不是很接受Swagger的理念，所以后端没有使用Swagger的相关文档注解，
这也导致了Swagger接口文档的不具可读性。如果开发者需要，可以自行在后端补充Swagger注解。

需要注意的是：
> 这里接口默认是公开的，因此项目一旦需要上线，请及时删除swagger和swagger-bootstrap-ui依赖和配置，
> 或者采取其他手段，防止接口对外暴露造成**安全隐患**。

例如
```
swagger:
  production: false
```

## 2 商城API服务

### 2.1 安全服务

#### 2.1.1 小程序微信登录

应用场景

    小程序环境下微信登录。
      
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.2 账号登录

应用场景

    基于用户名和密码的账号登录
    
接口链接

    POST /wx/auth/login
    
请求参数

    {
        "username": "user123",
        "password": "user123"
    }    
    
响应内容

    {
      "errno": 0,
      "data": {
        "userInfo": {
          "nickName": "user123",
          "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
        },
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0aGlzIGlzIGxpdGVtYWxsIHRva2VuIiwiYXVkIjoiTUlOSUFQUCIsImlzcyI6IkxJVEVNQUxMIiwiZXhwIjoxNTU3MzI2ODUwLCJ1c2VySWQiOjEsImlhdCI6MTU1NzMxOTY1MH0.XP0TuhupV_ttQsCr1KTaPZVlTbVzVOcnq_K0kXdbri0"
      },
      "errmsg": "成功"
    }   
    
错误码

    略
    
#### 2.1.3 注册

应用场景

    xxx
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.4 退出

应用场景

    账号退出
    
接口链接

    POST /wx/auth/logout
    
请求参数

    {
        "username": "user123",
        "password": "user123"
    }    
    
响应内容

    {
        "errno": 0,
        "errmsg": "成功"
    }
    
错误码

    略
    
#### 2.1.5 注册验证码

应用场景

    用户未登录情况下，请求后端发送注册验证码用于注册。
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.6 操作验证码

应用场景

    用户已登录情况下，请求后端发送操作验证码用于相关操作。
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.7 账号密码修改

应用场景

    账号密码修改
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.8 微信手机号码绑定

应用场景

    微信手机号码绑定，仅用于小程序环境。
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.9 手机号码修改

应用场景

    手机号码修改
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.10 账号信息

应用场景

    账号信息
    
接口链接

    GET /wx/auth/info
    
请求参数

    无
    
响应内容

    {
        "errno": 0,
        "data": {
            "gender": 1,
            "nickName": "user123",
            "mobile": "",
            "avatar": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
        },
        "errmsg": "成功"
    }   
    
错误码

    略
    
#### 2.1.11 账号信息更新

应用场景

    账号信息更新。
    
接口链接

    POST /wx/auth/profile
    
请求参数

    {
        "gender": 1,
        "nickName": "user123",
        "avatar": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
    }
    
响应内容

    {
        "errno": 0,
        "errmsg": "成功"
    }
    
错误码

    略
    
### 2.2 首页服务

#### 2.2.1 首页数据

应用场景

    首页数据
    
接口链接

    GET /wx/home/index
    
请求参数

    无
    
响应内容

    {
      "errno": 0,
      "data": {
        "newGoodsList": [
          {
            "id": 1181000,
            "name": "母亲节礼物-舒适安睡组合",
            "brief": "安心舒适是最好的礼物",
            "picUrl": "http://yanxuan.nosdn.127.net/1f67b1970ee20fd572b7202da0ff705d.png",
            "isNew": true,
            "isHot": false,
            "counterPrice": 2618.00,
            "retailPrice": 2598.00
          },
          {
            "id": 1116011,
            "name": "蔓越莓曲奇 200克",
            "brief": "酥脆奶香，甜酸回味",
            "picUrl": "http://yanxuan.nosdn.127.net/767b370d07f3973500db54900bcbd2a7.png",
            "isNew": true,
            "isHot": true,
            "counterPrice": 56.00,
            "retailPrice": 36.00
          },
          {
            "id": 1127047,
            "name": "趣味粉彩系列笔记本",
            "brief": "粉彩色泽，记录生活",
            "picUrl": "http://yanxuan.nosdn.127.net/6c03ca93d8fe404faa266ea86f3f1e43.png",
            "isNew": true,
            "isHot": false,
            "counterPrice": 49.00,
            "retailPrice": 29.00
          },
          {
            "id": 1135002,
            "name": "宫廷奢华真丝四件套",
            "brief": "100%桑蚕丝，丝滑润肤",
            "picUrl": "http://yanxuan.nosdn.127.net/45548f26cfd0c7c41e0afc3709d48286.png",
            "isNew": true,
            "isHot": false,
            "counterPrice": 2619.00,
            "retailPrice": 2599.00
          },
          {
            "id": 1152161,
            "name": "竹语丝麻印花四件套",
            "brief": "3重透气，清爽柔滑",
            "picUrl": "http://yanxuan.nosdn.127.net/977401e75113f7c8334c4fb5b4bf6215.png",
            "isNew": true,
            "isHot": false,
            "counterPrice": 479.00,
            "retailPrice": 459.00
          },
          {
            "id": 1166008,
            "name": "Carat钻石 不粘厨具组合",
            "brief": "钻石涂层，不粘锅锅具组",
            "picUrl": "http://yanxuan.nosdn.127.net/615a16e899e01efb780c488df4233f48.png",
            "isNew": true,
            "isHot": false,
            "counterPrice": 479.00,
            "retailPrice": 459.00
          }
        ],
        "couponList": [
          {
            "id": 2,
            "name": "限时满减券",
            "desc": "全场通用",
            "tag": "无限制",
            "discount": 10.00,
            "min": 99.00,
            "days": 10
          }
        ],
        "channel": [
          {
            "id": 1005000,
            "name": "居家",
            "iconUrl": "http://yanxuan.nosdn.127.net/a45c2c262a476fea0b9fc684fed91ef5.png"
          },
          {
            "id": 1005001,
            "name": "餐厨",
            "iconUrl": "http://yanxuan.nosdn.127.net/ad8b00d084cb7d0958998edb5fee9c0a.png"
          },
          {
            "id": 1005002,
            "name": "饮食",
            "iconUrl": "http://yanxuan.nosdn.127.net/c9280327a3fd2374c000f6bf52dff6eb.png"
          },
          {
            "id": 1008000,
            "name": "配件",
            "iconUrl": "http://yanxuan.nosdn.127.net/11abb11c4cfdee59abfb6d16caca4c6a.png"
          },
          {
            "id": 1010000,
            "name": "服装",
            "iconUrl": "http://yanxuan.nosdn.127.net/28a685c96f91584e7e4876f1397767db.png"
          },
          {
            "id": 1011000,
            "name": "婴童",
            "iconUrl": "http://yanxuan.nosdn.127.net/1ba9967b8de1ac50fad21774a4494f5d.png"
          },
          {
            "id": 1012000,
            "name": "杂货",
            "iconUrl": "http://yanxuan.nosdn.127.net/c2a3d6349e72c35931fe3b5bcd0966be.png"
          },
          {
            "id": 1013001,
            "name": "洗护",
            "iconUrl": "http://yanxuan.nosdn.127.net/9fe068776b6b1fca13053d68e9c0a83f.png"
          },
          {
            "id": 1019000,
            "name": "志趣",
            "iconUrl": "http://yanxuan.nosdn.127.net/7093cfecb9dde1dd3eaf459623df4071.png"
          }
        ],
        "grouponList": [
          {
            "id": 1109008,
            "name": "云端沙发组合",
            "brief": "MUJI供应商携手打造",
            "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
            "counterPrice": 4019.00,
            "retailPrice": 3999.00,
            "grouponPrice": 3949.00,
            "grouponDiscount": 50,
            "grouponMember": 5
          },
          {
            "id": 1039051,
            "name": "多功能午睡枕",
            "brief": "放松自在的午后时光",
            "picUrl": "http://yanxuan.nosdn.127.net/c8ca0600fa7ba11ca8be6a3173dd38c9.png",
            "counterPrice": 99.00,
            "retailPrice": 79.00,
            "grouponPrice": 59.00,
            "grouponDiscount": 20,
            "grouponMember": 20
          }
        ],
        "banner": [
          {
            "id": 1,
            "name": "合作 谁是你的菜",
            "link": "",
            "url": "http://yanxuan.nosdn.127.net/65091eebc48899298171c2eb6696fe27.jpg",
            "position": 1,
            "content": "合作 谁是你的菜",
            "enabled": true,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 2,
            "name": "活动 美食节",
            "link": "",
            "url": "http://yanxuan.nosdn.127.net/bff2e49136fcef1fd829f5036e07f116.jpg",
            "position": 1,
            "content": "活动 美食节",
            "enabled": true,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 3,
            "name": "活动 母亲节",
            "link": "",
            "url": "http://yanxuan.nosdn.127.net/8e50c65fda145e6dd1bf4fb7ee0fcecc.jpg",
            "position": 1,
            "content": "活动 母亲节5",
            "enabled": true,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          }
        ],
        "brandList": [
          {
            "id": 1001000,
            "name": "MUJI制造商",
            "desc": "严选精选了MUJI制造商和生产原料，\n用几乎零利润的价格，剔除品牌溢价，\n让用户享受原品牌的品质生活。",
            "picUrl": "http://yanxuan.nosdn.127.net/1541445967645114dd75f6b0edc4762d.png",
            "floorPrice": 12.90
          },
          {
            "id": 1001002,
            "name": "内野制造商",
            "desc": "严选从世界各地挑选毛巾，最终选择了为日本内野代工的工厂，追求毛巾的柔软度与功能性。品质比肩商场几百元的毛巾。",
            "picUrl": "http://yanxuan.nosdn.127.net/8ca3ce091504f8aa1fba3fdbb7a6e351.png",
            "floorPrice": 29.00
          },
          {
            "id": 1001003,
            "name": "Adidas制造商",
            "desc": "严选找到为Adidas等品牌制造商，\n选取优质原材料，与厂方一起设计，\n为你提供好的理想的运动装备。",
            "picUrl": "http://yanxuan.nosdn.127.net/335334d0deaff6dc3376334822ab3a2f.png",
            "floorPrice": 49.00
          },
          {
            "id": 1001007,
            "name": "优衣库制造商",
            "desc": "严选找到日本知名服装UNIQLO的制造商，\n选取优质长绒棉和精梳工艺，\n与厂方一起设计，为你提供理想的棉袜。",
            "picUrl": "http://yanxuan.nosdn.127.net/0d72832e37e7e3ea391b519abbbc95a3.png",
            "floorPrice": 29.00
          }
        ],
        "hotGoodsList": [
          {
            "id": 1152008,
            "name": "魔兽世界 部落 护腕 一只",
            "brief": "吸汗、舒适、弹性、防护、耐用",
            "picUrl": "http://yanxuan.nosdn.127.net/203cb83d93606865e3ddde57b69b9e9a.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 49.00,
            "retailPrice": 29.00
          },
          {
            "id": 1152009,
            "name": "魔兽世界 联盟 护腕 一只",
            "brief": "吸汗、舒适、弹性、防护、耐用",
            "picUrl": "http://yanxuan.nosdn.127.net/ae6d41117717387b82dcaf1dfce0cd97.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 49.00,
            "retailPrice": 29.00
          },
          {
            "id": 1152031,
            "name": "魔兽世界-伊利丹颈枕眼罩套装",
            "brief": "差旅好伴侣",
            "picUrl": "http://yanxuan.nosdn.127.net/fd6e78a397bd9e9804116a36f0270b0a.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 119.00,
            "retailPrice": 99.00
          },
          {
            "id": 1022000,
            "name": "意式毛线绣球四件套",
            "brief": "浪漫毛线绣球，简约而不简单",
            "picUrl": "http://yanxuan.nosdn.127.net/5350e35e6f22165f38928f3c2c52ac57.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 319.00,
            "retailPrice": 299.00
          },
          {
            "id": 1011004,
            "name": "色织精梳AB纱格纹空调被",
            "brief": "加大加厚，双色精彩",
            "picUrl": "http://yanxuan.nosdn.127.net/0984c9388a2c3fd2335779da904be393.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 219.00,
            "retailPrice": 199.00
          },
          {
            "id": 1084003,
            "name": "纯棉美式绞花针织盖毯",
            "brief": "美式提花，温暖舒适",
            "picUrl": "http://yanxuan.nosdn.127.net/cf40c167e7054fe184d49f19121f63c7.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 219.00,
            "retailPrice": 199.00
          }
        ],
        "topicList": [
          {
            "id": 264,
            "title": "设计师们推荐的应季好物",
            "subtitle": "原创设计春款系列上新",
            "price": 29.90,
            "readCount": "77.7k",
            "picUrl": "https://yanxuan.nosdn.127.net/14918201901050274.jpg"
          },
          {
            "id": 266,
            "title": "一条丝巾就能提升时髦度",
            "subtitle": "不知道大家对去年G20时，严选与国礼制造商一起推出的《凤凰于飞》等几款丝巾是否还...",
            "price": 0.00,
            "readCount": "35.0k",
            "picUrl": "https://yanxuan.nosdn.127.net/14919007135160213.jpg"
          },
          {
            "id": 268,
            "title": "米饭好吃的秘诀：会呼吸的锅",
            "subtitle": "今年1月份，我们联系到了日本伊贺地区的长谷园，那里有着180年伊贺烧历史的窑厂。...",
            "price": 0.00,
            "readCount": "33.3k",
            "picUrl": "https://yanxuan.nosdn.127.net/14920623353130483.jpg"
          },
          {
            "id": 271,
            "title": "选式新懒人",
            "subtitle": "懒出格调，懒出好生活。",
            "price": 15.00,
            "readCount": "57.7k",
            "picUrl": "https://yanxuan.nosdn.127.net/14924199099661697.jpg"
          }
        ],
        "floorGoodsList": [
          {
            "name": "居家",
            "goodsList": [
              {
                "id": 1110016,
                "name": "天然硅胶宠物除毛按摩刷",
                "brief": "顺滑平面，猫狗通用，去除死毛",
                "picUrl": "http://yanxuan.nosdn.127.net/3bd73b7279a83d1cbb50c0e45778e6d6.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 59.00,
                "retailPrice": 39.00
              },
              {
                "id": 1110017,
                "name": "耐用材料猫咪护理清洁套装",
                "brief": "精致钢材，美容清洁",
                "picUrl": "http://yanxuan.nosdn.127.net/534231583f82572398ec84bad425cdaf.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 99.00,
                "retailPrice": 79.00
              },
              {
                "id": 1110018,
                "name": "耐用狗狗清洁美容护理套装",
                "brief": "精致钢材，耐咬美容",
                "picUrl": "http://yanxuan.nosdn.127.net/d93aa5d6e7a296101cf4cb72613aeda6.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 99.00,
                "retailPrice": 79.00
              },
              {
                "id": 1110019,
                "name": "宠物合金钢安全除菌指甲护理组合",
                "brief": "猫狗皆可用，保护家具",
                "picUrl": "http://yanxuan.nosdn.127.net/1e7e392b6fc9da99dc112197b7444eec.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 89.00,
                "retailPrice": 69.00
              }
            ],
            "id": 1005000
          },
          {
            "name": "餐厨",
            "goodsList": [
              {
                "id": 1023003,
                "name": "100年传世珐琅锅 全家系列",
                "brief": "特质铸铁，大容量全家共享",
                "picUrl": "http://yanxuan.nosdn.127.net/c39d54c06a71b4b61b6092a0d31f2335.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 418.00,
                "retailPrice": 398.00
              },
              {
                "id": 1073008,
                "name": "铸铁珐琅牛排煎锅",
                "brief": "沥油隔水，煎出外焦里嫩",
                "picUrl": "http://yanxuan.nosdn.127.net/619e46411ccd62e5c0f16692ee1a85a0.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 169.00,
                "retailPrice": 149.00
              },
              {
                "id": 1051000,
                "name": "Carat钻石炒锅30cm",
                "brief": "安全涂层，轻便无烟",
                "picUrl": "http://yanxuan.nosdn.127.net/e564410546a11ddceb5a82bfce8da43d.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 200.00,
                "retailPrice": 180.00
              },
              {
                "id": 1051001,
                "name": "Carat钻石煎锅28cm",
                "brief": "耐磨涂层，导热迅速",
                "picUrl": "http://yanxuan.nosdn.127.net/f53ed57d9e23fda7e24dfd0e0a50c5d1.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 179.00,
                "retailPrice": 159.00
              }
            ],
            "id": 1005001
          },
          {
            "name": "饮食",
            "goodsList": [
              {
                "id": 1045000,
                "name": "绿茶蛋黄酥 200克/4枚入",
                "brief": "香甜茶食，果腹优选",
                "picUrl": "http://yanxuan.nosdn.127.net/b2adc3fd9b84a289a1be03e8ee400e61.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 48.00,
                "retailPrice": 28.00
              },
              {
                "id": 1116011,
                "name": "蔓越莓曲奇 200克",
                "brief": "酥脆奶香，甜酸回味",
                "picUrl": "http://yanxuan.nosdn.127.net/767b370d07f3973500db54900bcbd2a7.png",
                "isNew": true,
                "isHot": true,
                "counterPrice": 56.00,
                "retailPrice": 36.00
              },
              {
                "id": 1070000,
                "name": "星云酥 180克/3颗",
                "brief": "酥饼界的小仙女",
                "picUrl": "http://yanxuan.nosdn.127.net/8392725765cdd57fdae3f173877f4bda.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 46.00,
                "retailPrice": 26.00
              },
              {
                "id": 1155015,
                "name": "绿豆糕 80克（4枚入）",
                "brief": "细腻松软，入口绵柔",
                "picUrl": "http://yanxuan.nosdn.127.net/66b9f1638c0517d179262f14ed1345f9.png",
                "isNew": true,
                "isHot": false,
                "counterPrice": 32.90,
                "retailPrice": 12.90
              }
            ],
            "id": 1005002
          },
          {
            "name": "配件",
            "goodsList": [
              {
                "id": 1085019,
                "name": "20寸 纯PC“铝框”（非全铝）登机箱",
                "brief": "铝质包角，牢固抗摔",
                "picUrl": "http://yanxuan.nosdn.127.net/65c955a7a98e84d44ca30bb88a591eac.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 369.00,
                "retailPrice": 349.00
              },
              {
                "id": 1086052,
                "name": "20寸 铝镁合金登机箱",
                "brief": "时尚金属箱，奢品质感",
                "picUrl": "http://yanxuan.nosdn.127.net/93171a281c4ed272c007a050816e6f6c.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 879.00,
                "retailPrice": 859.00
              },
              {
                "id": 1152101,
                "name": "魔兽世界 部落 奥格瑞玛 拉杆箱 可登机",
                "brief": "18寸，可携带登机",
                "picUrl": "http://yanxuan.nosdn.127.net/c1c62211a17b71a634fa0c705d11fb42.png",
                "isNew": false,
                "isHot": true,
                "counterPrice": 908.00,
                "retailPrice": 888.00
              },
              {
                "id": 1114011,
                "name": "104升 纯PC拉链斜纹拉杆箱",
                "brief": "104升的体积，90升的价格",
                "picUrl": "http://yanxuan.nosdn.127.net/196b5ce11930b4eadaec563cb0406634.png",
                "isNew": false,
                "isHot": false,
                "counterPrice": 319.00,
                "retailPrice": 299.00
              }
            ],
            "id": 1008000
          }
        ]
      },
      "errmsg": "成功"
    }

错误码

    无
        
### 2.3 类目服务

### 2.4 商品服务

#### 2.4.1 商品列表

应用场景

    商品列表
    
接口链接

    GET /wx/goods/list

请求参数
    
    isNew: 是否新品，true或者false
    isHot: 是否热卖商品，true或者false
    keyword: 关键字，如果设置则查询是否匹配关键字
    brandId: 品牌商ID，如果设置则查询品牌商所属商品
    categoryId: 商品分类ID，如果设置则查询分类所属商品
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
        
响应内容

    {
      "errno": 0,
      "data": {
        "total": 11,
        "pages": 6,
        "limit": 2,
        "page": 1,
        "list": [
          {
            "id": 1181000,
            "name": "母亲节礼物-舒适安睡组合",
            "brief": "安心舒适是最好的礼物",
            "picUrl": "http://yanxuan.nosdn.127.net/1f67b1970ee20fd572b7202da0ff705d.png",
            "isNew": true,
            "isHot": false,
            "counterPrice": 2618.00,
            "retailPrice": 2598.00
          },
          {
            "id": 1116011,
            "name": "蔓越莓曲奇 200克",
            "brief": "酥脆奶香，甜酸回味",
            "picUrl": "http://yanxuan.nosdn.127.net/767b370d07f3973500db54900bcbd2a7.png",
            "isNew": true,
            "isHot": true,
            "counterPrice": 56.00,
            "retailPrice": 36.00
          }
        ],
        "filterCategoryList": [
          {
            "id": 1005007,
            "name": "锅具",
            "keywords": "",
            "desc": "一口好锅，炖煮生活一日三餐",
            "pid": 1005001,
            "iconUrl": "http://yanxuan.nosdn.127.net/4aab4598017b5749e3b63309d25e9f6b.png",
            "picUrl": "http://yanxuan.nosdn.127.net/d2db0d1d0622c621a8aa5a7c06b0fc6d.png",
            "level": "L2",
            "sortOrder": 1,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 1008002,
            "name": "布艺软装",
            "keywords": "",
            "desc": "各种风格软装装点你的家",
            "pid": 1005000,
            "iconUrl": "http://yanxuan.nosdn.127.net/8bbcd7de60a678846664af998f57e71c.png",
            "picUrl": "http://yanxuan.nosdn.127.net/2e2fb4f2856a021bbcd1b4c8400f2b06.png",
            "level": "L2",
            "sortOrder": 6,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 1008008,
            "name": "被枕",
            "keywords": "",
            "desc": "守护你的睡眠时光",
            "pid": 1005000,
            "iconUrl": "http://yanxuan.nosdn.127.net/927bc33f7ae2895dd6c11cf91f5e3228.png",
            "picUrl": "http://yanxuan.nosdn.127.net/b43ef7cececebe6292d2f7f590522e05.png",
            "level": "L2",
            "sortOrder": 2,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 1008009,
            "name": "床品件套",
            "keywords": "",
            "desc": "MUJI等品牌制造商出品",
            "pid": 1005000,
            "iconUrl": "http://yanxuan.nosdn.127.net/243e5bf327a87217ad1f54592f0176ec.png",
            "picUrl": "http://yanxuan.nosdn.127.net/81f671bd36bce05d5f57827e5c88dd1b.png",
            "level": "L2",
            "sortOrder": 4,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 1008015,
            "name": "糕点",
            "keywords": "",
            "desc": "四季糕点，用心烘焙",
            "pid": 1005002,
            "iconUrl": "http://yanxuan.nosdn.127.net/93168242df456b5f7bf3c89653b3db76.png",
            "picUrl": "http://yanxuan.nosdn.127.net/66ea1d6ad602a8e441af7cada93bdc7a.png",
            "level": "L2",
            "sortOrder": 1,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 1012003,
            "name": "文具",
            "keywords": "",
            "desc": "找回书写的力量",
            "pid": 1012000,
            "iconUrl": "http://yanxuan.nosdn.127.net/e1743239e41ca9af76875aedc73be7f0.png",
            "picUrl": "http://yanxuan.nosdn.127.net/e074795f61a83292d0f20eb7d124e2ac.png",
            "level": "L2",
            "sortOrder": 1,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 1020003,
            "name": "服饰",
            "keywords": "",
            "desc": "萌宝穿搭，柔软舒适触感",
            "pid": 1011000,
            "iconUrl": "http://yanxuan.nosdn.127.net/4e50f3c4e4d0a64cd0ad14cfc0b6bd17.png",
            "picUrl": "http://yanxuan.nosdn.127.net/004f5f96df4aeb0645abbd70c0637239.png",
            "level": "L2",
            "sortOrder": 1,
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          }
        ]
      },
      "errmsg": "成功"
    }

错误码
    
    略   
    

#### 2.4.2 商品详情

应用场景

    商品详情
    
接口链接

    GET /wx/goods/detail

请求参数
    
    id: 商品ID，例如id=1152008

响应内容

    {
      "errno": 0,
      "data": {
        "specificationList": [
          {
            "name": "规格",
            "valueList": [
              {
                "id": 231,
                "goodsId": 1152008,
                "specification": "规格",
                "value": "标准",
                "picUrl": "",
                "addTime": "2018-02-01 00:00:00",
                "updateTime": "2018-02-01 00:00:00",
                "deleted": false
              }
            ]
          }
        ],
        "groupon": [],
        "issue": [
          {
            "id": 1,
            "question": "购买运费如何收取？",
            "answer": "单笔订单金额（不含运费）满88元免邮费；不满88元，每单收取10元运费。\n(港澳台地区需满",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 2,
            "question": "使用什么快递发货？",
            "answer": "严选默认使用顺丰快递发货（个别商品使用其他快递），配送范围覆盖全国大部分地区（港澳台地区除",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 3,
            "question": "如何申请退货？",
            "answer": "1.自收到商品之日起30日内，顾客可申请无忧退货，退款将原路返还，不同的银行处理时间不同，",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 4,
            "question": "如何开具发票？",
            "answer": "1.如需开具普通发票，请在下单时选择“我要开发票”并填写相关信息（APP仅限2.4.0及以",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          }
        ],
        "userHasCollect": 0,
        "shareImage": "",
        "comment": {
          "data": [],
          "count": 0
        },
        "attribute": [],
        "brand": {},
        "productList": [
          {
            "id": 232,
            "goodsId": 1152008,
            "specifications": [
              "标准"
            ],
            "price": 29.00,
            "number": 100,
            "url": "http://yanxuan.nosdn.127.net/203cb83d93606865e3ddde57b69b9e9a.png",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          }
        ],
        "info": {
          "id": 1152008,
          "goodsSn": "1152008",
          "name": "魔兽世界 部落 护腕 一只",
          "categoryId": 1032000,
          "brandId": 0,
          "gallery": [
            "http://yanxuan.nosdn.127.net/46bcddbc57e70bf5f36bdff9c9195c65.png",
            "http://yanxuan.nosdn.127.net/46bcddbc57e70bf5f36bdff9c9195c65.png",
            "http://yanxuan.nosdn.127.net/46bcddbc57e70bf5f36bdff9c9195c65.png",
            "http://yanxuan.nosdn.127.net/46bcddbc57e70bf5f36bdff9c9195c65.png",
            "http://yanxuan.nosdn.127.net/46bcddbc57e70bf5f36bdff9c9195c65.png"
          ],
          "keywords": "",
          "brief": "吸汗、舒适、弹性、防护、耐用",
          "isOnSale": true,
          "sortOrder": 7,
          "picUrl": "http://yanxuan.nosdn.127.net/203cb83d93606865e3ddde57b69b9e9a.png",
          "shareUrl": "",
          "isNew": false,
          "isHot": true,
          "unit": "件",
          "counterPrice": 49.00,
          "retailPrice": 29.00,
          "addTime": "2018-02-01 00:00:00",
          "updateTime": "2018-02-01 00:00:00",
          "deleted": false,
          "detail": ""
        }
      },
      "errmsg": "成功"
    }

错误码
    
    略   


#### 2.4.3 商品推荐

应用场景

    针对某个商品推荐其他商品
    
接口链接

    GET /wx/goods/related

请求参数
    
    id: 商品ID，例如id=1152008
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
        
响应内容

    {
      "errno": 0,
      "data": {
        "total": 8,
        "pages": 2,
        "limit": 6,
        "page": 1,
        "list": [
          {
            "id": 1152004,
            "name": "魔兽世界 蛋盾包 双肩包",
            "brief": "伊利丹掉落，挤地铁神器",
            "picUrl": "http://yanxuan.nosdn.127.net/8c93cef435d888bd79833777df1cd0c2.png",
            "isNew": false,
            "isHot": false,
            "counterPrice": 419.00,
            "retailPrice": 399.00
          },
          {
            "id": 1152008,
            "name": "魔兽世界 部落 护腕 一只",
            "brief": "吸汗、舒适、弹性、防护、耐用",
            "picUrl": "http://yanxuan.nosdn.127.net/203cb83d93606865e3ddde57b69b9e9a.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 49.00,
            "retailPrice": 29.00
          },
          {
            "id": 1152009,
            "name": "魔兽世界 联盟 护腕 一只",
            "brief": "吸汗、舒适、弹性、防护、耐用",
            "picUrl": "http://yanxuan.nosdn.127.net/ae6d41117717387b82dcaf1dfce0cd97.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 49.00,
            "retailPrice": 29.00
          },
          {
            "id": 1152031,
            "name": "魔兽世界-伊利丹颈枕眼罩套装",
            "brief": "差旅好伴侣",
            "picUrl": "http://yanxuan.nosdn.127.net/fd6e78a397bd9e9804116a36f0270b0a.png",
            "isNew": false,
            "isHot": true,
            "counterPrice": 119.00,
            "retailPrice": 99.00
          },
          {
            "id": 1152095,
            "name": "魔兽世界 联盟·暴风城 堡垒收纳盒",
            "brief": "桌面整理神器",
            "picUrl": "http://yanxuan.nosdn.127.net/c86b49f635fa141decebabbd0966a6ef.png",
            "isNew": false,
            "isHot": false,
            "counterPrice": 519.00,
            "retailPrice": 499.00
          },
          {
            "id": 1152097,
            "name": "魔兽世界 雷霆之怒逐风者的祝福之剑 雨伞",
            "brief": "炫酷装备，可以背的雨伞",
            "picUrl": "http://yanxuan.nosdn.127.net/532836444ae5eaec40b5810ca4f9b1e6.png",
            "isNew": false,
            "isHot": false,
            "counterPrice": 419.00,
            "retailPrice": 399.00
          }
        ]
      },
      "errmsg": "成功"
    }

错误码
    
    略   
            
#### 2.4.4 商品分类

应用场景

    针对某个商品推荐其他商品
    
接口链接

    GET /wx/goods/related

请求参数
    
        
响应内容

错误码

#### 2.4.5 在售商品总数

应用场景

    在售商品总数
    
接口链接

    GET /wx/goods/count

请求参数
    
    无
        
响应内容

    {
      "errno": 0,
      "data": 238,
      "errmsg": "成功"
    }
    
错误码
       
    无
         
### 2.5 购物车服务

#### 2.5.1 用户购物车

应用场景

    用户购物车
    
接口链接


请求参数
    
    无
        
响应内容

    
错误码
       
    略
    
### 2.6 订单服务
    
#### 2.6.1 订单列表

应用场景

    订单列表

接口链接

    GET /wx/order/list
    
请求参数

    showType: 订单类型，0则全部，1则待付款，2则待发货，3则待收货，4则代评价
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
    
响应结果

    {
      "errno": 0,
      "data": {
        "total": 1,
        "pages": 1,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "orderStatusText": "未付款",
            "isGroupin": false,
            "orderSn": "20190509607545",
            "actualPrice": 3989.00,
            "goodsList": [
              {
                "number": 1,
                "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
                "id": 3,
                "goodsName": "云端沙发组合",
                "specifications": [
                  "标准"
                ]
              }
            ],
            "id": 3,
            "handleOption": {
              "cancel": true,
              "delete": false,
              "pay": true,
              "comment": false,
              "confirm": false,
              "refund": false,
              "rebuy": false
            }
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码

    略
    
#### 2.6.2 订单详情

应用场景

    订单详情

接口链接

    GET /wx/order/detail
    
请求参数
    
    orderId： 订单ID
    
响应结果

    {
      "errno": 0,
      "data": {
        "orderInfo": {
          "consignee": "d",
          "address": "北京市市辖区东城区 ddd",
          "addTime": "2019-05-09 15:30:29",
          "orderSn": "20190509607545",
          "actualPrice": 3989.00,
          "mobile": "13811111111",
          "orderStatusText": "未付款",
          "goodsPrice": 3999.00,
          "couponPrice": 10.00,
          "id": 3,
          "freightPrice": 0.00,
          "handleOption": {
            "cancel": true,
            "delete": false,
            "pay": true,
            "comment": false,
            "confirm": false,
            "refund": false,
            "rebuy": false
          }
        },
        "orderGoods": [
          {
            "id": 3,
            "orderId": 3,
            "goodsId": 1109008,
            "goodsName": "云端沙发组合",
            "goodsSn": "1109008",
            "productId": 140,
            "number": 1,
            "price": 3999.00,
            "specifications": [
              "标准"
            ],
            "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
            "comment": 0,
            "addTime": "2019-05-09 15:30:29",
            "updateTime": "2019-05-09 15:30:29",
            "deleted": false
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码

    略
    
#### 2.6.3 创建订单

应用场景

    创建新订单

接口链接

    POST /wx/order/submit

请求参数

    {
      "cartId": 0,
      "addressId": 3,
      "couponId": -1,
      "message": "",
      "grouponRulesId": 0,
      "grouponLinkId": 0
    }
    
响应结果

    {
      "errno": 0,
      "data": {
        "orderId": 4
      },
      "errmsg": "成功"
    }
    
错误码

    略
    
#### 2.6.4 取消订单

应用场景

    取消订单

接口链接

    POST /wx/order/cancel
    
请求参数

    orderId: 订单ID
    
响应结果

    {
      "errno": 0,
      "errmsg": "成功"
    }
    
错误码

    略

#### 2.6.5 微信预支付交易单

应用场景

    订单的微信预支付交易单

接口链接

    POST /wx/order/prepay

说明

    具体微信支付交互流程和预支付使用方式，见官方文档: https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_3&index=1

请求参数

    orderId: 订单ID

响应结果

    {
        errno: 0,
        errmsg: "成功",
        data: {
            appId: 'xxx',
            timeStamp: 'xxx',
            nonceStr: 'xxx',
            packageValue: 'xxx',
            signType: 'xxx',
            paySign: 'xxx'
        }
    }

错误码

    略
     
#### 2.6.6 确认收货

应用场景

    订单确认收货

接口链接

    POST /wx/order/confirm
    
请求参数

    orderId: 订单ID
    
响应结果

    {
      "errno": 0,
      "errmsg": "成功"
    }
    
错误码

    略
    
#### 2.6.7 订单删除

应用场景

    删除订单记录

接口链接

    POST /wx/order/delete
    
请求参数

    orderId: 订单ID
    
响应结果

    {
      "errno": 0,
      "errmsg": "成功"
    }
    
错误码

    略
    
#### 2.6.8 订单退款

应用场景

    订单已经支付但是商家未发货，用户可以点击退款按钮申请退款取消订单。

说明

    退款请求发送以后，不会自动退款，仅仅是后端设置退款请求记录。
    管理员在管理后台看到退款请求以后会手动退款或者拒绝退款。
    
接口链接

    POST /wx/order/refund
    
请求参数

    orderId: 订单ID
    
响应结果

    {
      "errno": 0,
      "errmsg": "成功"
    }
    
错误码

    略
           
  
#### 2.6.9 待评价商品

应用场景

    用户确认收货以后，可以待评价的订单商品。    

接口链接

    GET /wx/order/goods
    
请求参数

    orderId: 订单ID
    goodsId: 商品ID
    
响应结果

    {
      "errno": 0,
      "data": {
        "id": 4,
        "orderId": 4,
        "goodsId": 1109008,
        "goodsName": "云端沙发组合",
        "goodsSn": "1109008",
        "productId": 140,
        "number": 1,
        "price": 3999.00,
        "specifications": [
          "标准"
        ],
        "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
        "comment": 0,
        "addTime": "2019-05-09 17:06:54",
        "updateTime": "2019-05-09 17:06:54",
        "deleted": false
      },
      "errmsg": "成功"
    }
    
错误码

    略
         
  
#### 2.6.10 订单评价

应用场景

    订单评价

接口链接

    POST /wx/order/comment
    
请求参数

    orderGoodsId: 订单商品ID
    content: 评价内容
    star: 评分，1分至5分
    hasPicture: 是否有评价图片
    picUrls: 评价图片列表
    
例如

    {
      "orderGoodsId": 4,
      "content": "不错",
      "star": 5,
      "hasPicture": true,
      "picUrls": []
    }
        
响应结果

    {
      "errno": 0,
      "errmsg": "成功"
    }
    
错误码

    略
                                                  
### 2.7 会员服务

### 2.8 收货地址服务

#### 2.8.1 收货地址列表

应用场景

    用户收货地址列表

接口链接

    GET /wx/address/list

请求参数

    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
        
响应结果

    {
      "errno": 0,
      "data": {
        "total": 1,
        "pages": 1,
        "limit": 1,
        "page": 1,
        "list": [
          {
            "id": 3,
            "name": "d",
            "userId": 2,
            "province": "北京市",
            "city": "市辖区",
            "county": "东城区",
            "addressDetail": "ddd",
            "areaCode": "110101",
            "tel": "13811111111",
            "isDefault": true,
            "addTime": "2019-05-06 14:17:32",
            "updateTime": "2019-05-06 14:17:32",
            "deleted": false
          }
        ]
      },
      "errmsg": "成功"
    }

错误码

    略
    
#### 2.8.2 收货地址详情

应用场景

    请求用户的收货地址详情

接口链接

    GET /wx/address/detail

请求参数

    id: 收货地址ID
    
响应结果

    {
        errno: 0,
        errmsg: "成功",，
        data: {
            id: 收货地址ID，
            name: 收货人，
            tel: 手机号
            province: 省级行政区域,
            city: 市级行政区域,
            county: 区级行政区域,
            addressDetail: 具体地址,
            areaCode: 地址编码，
            postalCode: 邮政编码
            isDefault: 是否默认
        }
    }

错误码

    略

    
#### 2.8.3 保存收货地址

应用场景

    添加或者更新用户收货地址

接口链接

    POST /wx/address/save

请求参数

    id: 收货地址ID，如果是0则是添加，否则是更新
    name: 收货人，
    tel: 手机号
    province: 省级行政区域,
    city: 市级行政区域,
    county: 区级行政区域,
    addressDetail: 具体地址,
    areaCode: 地址编码，
    postalCode: 邮政编码
    isDefault: 是否默认    

例如
    
    {
      "id": 0,
      "name": "xxx",
      "tel": "13811111111",
      "province": "北京市",
      "city": "市辖区",
      "county": "东城区",
      "areaCode": "110101",
      "addressDetail": "dddd",
      "isDefault": true
    }
    
响应结果

    {
        errno: 0,
        errmsg: "成功",，
        data: 3
    }

错误码

    略
    
    
#### 2.8.4 删除收货地址

应用场景

    删除用户的某个收货地址

接口链接

    POST /wx/address/delete

请求参数

    id: 收货地址ID
    
响应结果

    {
        errno: 0,
        errmsg: "成功"
    }

错误码

    略
            	
### 2.9 品牌商服务

#### 2.9.1 品牌商列表

应用场景

    访问品牌商列表信息
    
接口链接

    GET /wx/brand/list

请求参数
    
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 49,
        "pages": 5,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "id": 1024000,
            "name": "WMF制造商",
            "desc": "严选找寻德国百年高端厨具WMF的制造商，\n选择拥有14年经验的不锈钢生产工厂，\n为你甄选事半功倍的优质厨具。",
            "picUrl": "http://yanxuan.nosdn.127.net/2018e9ac91ec37d9aaf437a1fd5d7070.png",
            "floorPrice": 9.90
          },
          {
            "id": 1024001,
            "name": "OBH制造商",
            "desc": "严选寻找OBH品牌的制造商，打造精致厨具，\n韩国独资工厂制造，严格质检，品质雕琢\n力求为消费者带来全新的烹饪体验。",
            "picUrl": "http://yanxuan.nosdn.127.net/bf3499ac17a11ffb9bb7caa47ebef2dd.png",
            "floorPrice": 39.00
          },
          {
            "id": 1024003,
            "name": "Stoneline制造商",
            "desc": "严选找寻德国经典品牌Stoneline的制造商，\n追踪工艺，考量细节，亲自试用，\n为你甄选出最合心意的锅具和陶瓷刀，下厨如神。",
            "picUrl": "http://yanxuan.nosdn.127.net/3a44ae7db86f3f9b6e542720c54cc349.png",
            "floorPrice": 9.90
          },
          {
            "id": 1024006,
            "name": "KitchenAid制造商",
            "desc": "严选寻访KitchenAid品牌的制造商，\n采用德国LFGB认证食品级专用不锈钢，\n欧式简约设计，可靠安心，尽享下厨乐趣。",
            "picUrl": "http://yanxuan.nosdn.127.net/e11385bf29d1b3949435b80fcd000948.png",
            "floorPrice": 98.00
          },
          {
            "id": 1034001,
            "name": "Alexander McQueen制造商",
            "desc": "为制造精致实用的高品质包包，\n严选团队选择Alexander McQueen制造商，\n严格筛选，带来轻奢优雅体验。",
            "picUrl": "http://yanxuan.nosdn.127.net/db7ee9667d84cbce573688297586699c.jpg",
            "floorPrice": 69.00
          },
          {
            "id": 1023000,
            "name": "PetitBateau小帆船制造商",
            "desc": "为打造适合宝宝的婴童服装，\n严选团队寻找PetitBateau小帆船的品牌制造商，\n无荧光剂，国家A类标准，让宝宝穿的放心。",
            "picUrl": "http://yanxuan.nosdn.127.net/1a11438598f1bb52b1741e123b523cb5.jpg",
            "floorPrice": 36.00
          },
          {
            "id": 1001000,
            "name": "MUJI制造商",
            "desc": "严选精选了MUJI制造商和生产原料，\n用几乎零利润的价格，剔除品牌溢价，\n让用户享受原品牌的品质生活。",
            "picUrl": "http://yanxuan.nosdn.127.net/1541445967645114dd75f6b0edc4762d.png",
            "floorPrice": 12.90
          },
          {
            "id": 1001002,
            "name": "内野制造商",
            "desc": "严选从世界各地挑选毛巾，最终选择了为日本内野代工的工厂，追求毛巾的柔软度与功能性。品质比肩商场几百元的毛巾。",
            "picUrl": "http://yanxuan.nosdn.127.net/8ca3ce091504f8aa1fba3fdbb7a6e351.png",
            "floorPrice": 29.00
          },
          {
            "id": 1001003,
            "name": "Adidas制造商",
            "desc": "严选找到为Adidas等品牌制造商，\n选取优质原材料，与厂方一起设计，\n为你提供好的理想的运动装备。",
            "picUrl": "http://yanxuan.nosdn.127.net/335334d0deaff6dc3376334822ab3a2f.png",
            "floorPrice": 49.00
          },
          {
            "id": 1033003,
            "name": "Armani制造商",
            "desc": "严选团队携手国际标准化专业生产厂家，\n厂家长期为Armani、Alexander wang等知名品牌代工，\n专业进口设备，精密质量把控，精于品质居家体验。",
            "picUrl": "http://yanxuan.nosdn.127.net/981e06f0f46f5f1f041d7de3dd3202e6.jpg",
            "floorPrice": 199.00
          }
        ]
      },
      "errmsg": "成功"
    }

错误码
    
    略
    
#### 2.9.2 品牌商详情

应用场景

    访问单个品牌商详情信息
    
接口链接

    GET /wx/brand/detail

请求参数
    
    id: 品牌商ID，例如1001020
    
响应内容

    {
      "errno": 0,
      "data": {
        "id": 1001020,
        "name": "Ralph Lauren制造商",
        "desc": "我们与Ralph Lauren Home的制造商成功接洽，掌握先进的生产设备，传承品牌工艺和工序。追求生活品质的你，值得拥有。",
        "picUrl": "http://yanxuan.nosdn.127.net/9df78eb751eae2546bd3ee7e61c9b854.png",
        "sortOrder": 20,
        "floorPrice": 29.00,
        "addTime": "2018-02-01 00:00:00",
        "updateTime": "2018-02-01 00:00:00",
        "deleted": false
      },
      "errmsg": "成功"
    }    

错误码
    
    略
    
### 2.10 收藏服务

#### 2.10.1 收藏列表

应用场景

    收藏列表
    
接口链接

    GET /wx/collect/list
    
请求参数
    
    type： 收藏类型，如果是0则是商品收藏，如果是1则是专题收藏
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序   
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 2,
        "pages": 1,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "brief": "酥脆奶香，甜酸回味",
            "picUrl": "http://yanxuan.nosdn.127.net/767b370d07f3973500db54900bcbd2a7.png",
            "valueId": 1116011,
            "name": "蔓越莓曲奇 200克",
            "id": 3,
            "type": 0,
            "retailPrice": 36.00
          },
          {
            "brief": "MUJI供应商携手打造",
            "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
            "valueId": 1109008,
            "name": "云端沙发组合",
            "id": 2,
            "type": 0,
            "retailPrice": 3999.00
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码
    
    略
    

#### 2.10.2 收藏添加或删除

应用场景

    用户收藏添加或删除

说明

    如果用户已经收藏，则请求API会删除已收藏商品或专题；
    如果用户未收藏，则请求API会添加新的商品或专题收藏记录。
        
接口链接

    POST /wx/collect/addordelete
    
请求参数
    
    type: 收藏类型，如果是0则是商品收藏，如果是1则是专题收藏
    valueId: 收藏对象ID，如果type=0则设置商品ID，如果type=1则设置专题ID
    
例如
    
    {
      "type": 0,
      "valueId": 1116011
    }

    
响应内容

    
错误码
    
    略
        
### 2.11 评论服务

#### 2.11.1 评论数量

应用场景

    某个商品或者专题的评论数量，包括总的评论数量和包含图片的评论数量
    
接口链接

    GET /wx/comment/count

请求参数
    
    type: 评论类型，如果是0则是商品评论，如果是1则是专题评论
    valueId: 评论对象ID，如果type=0,则设置商品ID，如果type=0,则设置专题ID
    
响应内容

    {
      "errno": 0,
      "data": {
        "hasPicCount": 34,
        "allCount": 96
      },
      "errmsg": "成功"
    }

错误码
    
    无
    

#### 2.11.2 评论列表

应用场景

    某个商品或者专题的评论列表
    
接口链接

    GET /wx/comment/list
    
请求参数
    
    valueId=1181000&type=0&limit=20&page=1&showType=0
    type: 评论类型，如果是0则是商品评论，如果是1则是专题评论
    valueId: 评论对象ID，如果type=0,则设置商品ID，如果type=0,则设置专题ID
    showType: 评论显示类型，如果是0则是所有评论，如果是1则是包含图片的评论
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
        
响应内容

    {
      "errno": 0,
      "data": {
        "total": 96,
        "pages": 20,
        "limit": 5,
        "page": 1,
        "list": [
          {
            "userInfo": {
              "nickName": "user123",
              "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            },
            "addTime": "2018-02-01 00:00:00",
            "picList": [
              "https://yanxuan.nosdn.127.net/218783173f303ec6d8766810951d0790.jpg"
            ],
            "content": "布料很厚实，触感不错，洗过之后不缩水不掉色"
          },
          {
            "userInfo": {
              "nickName": "user123",
              "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            },
            "addTime": "2018-02-01 00:00:00",
            "picList": [
              "https://yanxuan.nosdn.127.net/33978a0d6f56d94c45e4fc594b4b8606.jpg"
            ],
            "content": "料子很舒服，凉凉的，配合蚕丝被，夏天很凉快~"
          },
          {
            "userInfo": {
              "nickName": "user123",
              "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            },
            "addTime": "2018-02-01 00:00:00",
            "picList": [
              "https://yanxuan.nosdn.127.net/d3975d1b6d88e9f9d762cd9a879d1a14.jpg"
            ],
            "content": "一直喜欢粗布的床上用品。冬暖夏凉。这套看起来非常漂亮。实际感觉有点粗布的感觉。很好！"
          },
          {
            "userInfo": {
              "nickName": "user123",
              "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            },
            "addTime": "2018-02-01 00:00:00",
            "picList": [
              "https://yanxuan.nosdn.127.net/5fe1121396458cfe0dc1b25ec86f7ff9.jpg",
              "https://yanxuan.nosdn.127.net/d5a55abd6ced5c811d775b04929aaabc.jpg",
              "https://yanxuan.nosdn.127.net/f1764d820ba6ddaf51d297e3cf3826cd.jpg"
            ],
            "content": "太好了，舒服的不得了，腰，腿，脊柱，头，颈椎！\n无一处不舒服，真没想到这么优惠！\n搬了新家还要买！"
          },
          {
            "userInfo": {
              "nickName": "user123",
              "avatarUrl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
            },
            "addTime": "2018-02-01 00:00:00",
            "picList": [
              "https://yanxuan.nosdn.127.net/f753f91430dfb56f574c737d4b2fde46.jpg"
            ],
            "content": "抱着试试的态度 先买了小的 果然感觉很舒服 深陷其中 把自己全身心都给了它 第二个床垫已经在路上"
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码
    
    无
        

#### 2.11.3 发表评论

应用场景

    针对某个商品或者专题的发表评论
    
接口链接


请求参数
    

响应内容


错误码
    
    略
            
### 2.12 优惠券服务


#### 2.12.1 优惠券列表

应用场景

    优惠券列表
    
接口链接

    GET  /wx/coupon/list
    
请求参数
    
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序       

响应内容

    {
      "errno": 0,
      "data": {
        "total": 2,
        "pages": 1,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "id": 1,
            "name": "限时满减券",
            "desc": "全场通用",
            "tag": "无限制",
            "discount": 5.00,
            "min": 99.00,
            "days": 10
          },
          {
            "id": 2,
            "name": "限时满减券",
            "desc": "全场通用",
            "tag": "无限制",
            "discount": 10.00,
            "min": 99.00,
            "days": 10
          }
        ]
      },
      "errmsg": "成功"
    }

错误码
    
    略
    

#### 2.12.2 用户优惠券列表

应用场景

    用户优惠券列表
    
接口链接

    GET /wx/coupon/mylist
    
请求参数
    
    status: 优惠券状态，如果0则未使用，如果1则已使用，如果2则已过期
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序    
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 2,
        "pages": 1,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "id": 1,
            "name": "限时满减券",
            "desc": "全场通用",
            "tag": "无限制",
            "min": "99.00",
            "discount": "5.00",
            "startTime": "2019-05-06 16:21:38",
            "endTime": "2019-05-16 16:21:38"
          },
          {
            "id": 3,
            "name": "新用户优惠券",
            "desc": "全场通用",
            "tag": "无限制",
            "min": "99.00",
            "discount": "10.00",
            "startTime": "2019-05-06 12:30:06",
            "endTime": "2019-05-16 12:30:06"
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码
    
    略
    

#### 2.12.3 下单可用优惠券

应用场景

    当前购物车下单商品订单可用优惠券
    
接口链接

    GET /wx/coupon/selectlist

请求参数
    
    cartId: 购物车ID，如果0则是购物车商品，如果非0则是立即单一商品
    grouponRulesId: 团购规则ID，如果是团购商品则需要设置具体团购规则ID
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 1,
        "pages": 1,
        "limit": 1,
        "page": 1,
        "list": [
          {
            "id": 2,
            "name": "限时满减券",
            "desc": "全场通用",
            "tag": "无限制",
            "min": "99.00",
            "discount": "10.00",
            "startTime": "2019-05-09 15:27:29",
            "endTime": "2019-05-19 15:27:29"
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码
    
    略
            
#### 2.12.4 优惠券领取

应用场景

    领取优惠券

接口链接
    
    POST /wx/coupon/receive

请求参数
    
    couponId： 可领取优惠券ID
    
例如

    {
        "couponId": 2
    }
    
响应内容

    {
      "errno": 0,
      "errmsg": "成功"
    }

错误码
    
    略

#### 2.12.5 优惠券兑换

应用场景

    通过兑换码兑换优惠券
    
接口链接

    POST /wx/coupon/exchange

请求参数
    
    code: 优惠券兑换码
    
响应内容

    {
      "errno": 0,
      "errmsg": "成功"
    }

错误码
    
    略
                                    
### 2.13 反馈服务

### 2.14 足迹服务

#### 2.14.1 用户足迹列表

应用场景

    用户足迹列表
    
接口链接

    GET /wx/footprint/list
    
请求参数
    
    page: 请求页码
    limit: 每一页数量 
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 22,
        "pages": 6,
        "limit": 4,
        "page": 1,
        "list": [
          {
            "brief": "酥脆奶香，甜酸回味",
            "picUrl": "http://yanxuan.nosdn.127.net/767b370d07f3973500db54900bcbd2a7.png",
            "addTime": "2019-05-09 10:10:01",
            "goodsId": 1116011,
            "name": "蔓越莓曲奇 200克",
            "id": 22,
            "retailPrice": 36.00
          },
          {
            "brief": "MUJI供应商携手打造",
            "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
            "addTime": "2019-05-09 10:09:49",
            "goodsId": 1109008,
            "name": "云端沙发组合",
            "id": 21,
            "retailPrice": 3999.00
          },
          {
            "brief": "酥脆奶香，甜酸回味",
            "picUrl": "http://yanxuan.nosdn.127.net/767b370d07f3973500db54900bcbd2a7.png",
            "addTime": "2019-05-08 22:40:55",
            "goodsId": 1116011,
            "name": "蔓越莓曲奇 200克",
            "id": 20,
            "retailPrice": 36.00
          },
          {
            "brief": "MUJI供应商携手打造",
            "picUrl": "http://yanxuan.nosdn.127.net/c5be2604c0e4186a4e7079feeb742cee.png",
            "addTime": "2019-05-07 14:35:41",
            "goodsId": 1109008,
            "name": "云端沙发组合",
            "id": 19,
            "retailPrice": 3999.00
          }
        ]
      },
      "errmsg": "成功"
    }
        
错误码
    
    略
    
#### 2.14.2 用户足迹删除

应用场景

    用户足迹删除
    
接口链接

    POST /wx/footprint/delete
    
请求参数
    
    id: 用户足迹ID
    
响应内容

    {
      "errno": 0,
      "errmsg": "成功"
    }
        
错误码
    
    略
        
### 2.15 团购服务

注意
> 团购业务还不完善


#### 2.15.1 团购商品列表

应用场景

    参加团购的商品列表信息
    
接口链接


请求参数
    

响应内容


错误码
    
    略


#### 2.15.2 团购活动详情

应用场景

    团购活动详情
    
接口链接


请求参数
    

响应内容


错误码
    
    略               

#### 2.15.3 参加团购

应用场景

    参加团购的商品列表信息
    
接口链接


请求参数
    

响应内容


错误码
    
    略


#### 2.15.4 用户参团列表

应用场景

    用户参团列表
    
接口链接


请求参数
    

响应内容


错误码
    
    略
                                                                  
### 2.16 帮助服务

#### 2.16.1 帮助列表

应用场景

    帮助列表
    
接口链接

    GET /wx/issue/list
    
请求参数
    
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序    

响应内容

    {
      "errno": 0,
      "data": {
        "total": 4,
        "pages": 1,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "id": 1,
            "question": "购买运费如何收取？",
            "answer": "单笔订单金额（不含运费）满88元免邮费；不满88元，每单收取10元运费。\n(港澳台地区需满",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 2,
            "question": "使用什么快递发货？",
            "answer": "严选默认使用顺丰快递发货（个别商品使用其他快递），配送范围覆盖全国大部分地区（港澳台地区除",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 3,
            "question": "如何申请退货？",
            "answer": "1.自收到商品之日起30日内，顾客可申请无忧退货，退款将原路返还，不同的银行处理时间不同，",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          },
          {
            "id": 4,
            "question": "如何开具发票？",
            "answer": "1.如需开具普通发票，请在下单时选择“我要开发票”并填写相关信息（APP仅限2.4.0及以",
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false
          }
        ]
      },
      "errmsg": "成功"
    }
    
错误码
    
    无
              
### 2.17 搜索服务

### 2.18 专题服务

#### 2.18.1 专题列表

应用场景

    访问专题列表信息
    
接口链接

    GET /wx/topic/list

请求参数
    
    page: 请求页码
    limit: 每一页数量
    sort: 排序字段
    order: 升序降序
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 20,
        "pages": 2,
        "limit": 10,
        "page": 1,
        "list": [
          {
            "id": 264,
            "title": "设计师们推荐的应季好物",
            "subtitle": "原创设计春款系列上新",
            "price": 29.90,
            "readCount": "77.7k",
            "picUrl": "https://yanxuan.nosdn.127.net/14918201901050274.jpg"
          },
          {
            "id": 266,
            "title": "一条丝巾就能提升时髦度",
            "subtitle": "不知道大家对去年G20时，严选与国礼制造商一起推出的《凤凰于飞》等几款丝巾是否还...",
            "price": 0.00,
            "readCount": "35.0k",
            "picUrl": "https://yanxuan.nosdn.127.net/14919007135160213.jpg"
          },
          {
            "id": 268,
            "title": "米饭好吃的秘诀：会呼吸的锅",
            "subtitle": "今年1月份，我们联系到了日本伊贺地区的长谷园，那里有着180年伊贺烧历史的窑厂。...",
            "price": 0.00,
            "readCount": "33.3k",
            "picUrl": "https://yanxuan.nosdn.127.net/14920623353130483.jpg"
          },
          {
            "id": 271,
            "title": "选式新懒人",
            "subtitle": "懒出格调，懒出好生活。",
            "price": 15.00,
            "readCount": "57.7k",
            "picUrl": "https://yanxuan.nosdn.127.net/14924199099661697.jpg"
          },
          {
            "id": 272,
            "title": "料理也要精细简单",
            "subtitle": "享受天然的味道，日子每天都好新鲜",
            "price": 69.00,
            "readCount": "125.6k",
            "picUrl": "https://yanxuan.nosdn.127.net/14925200530030186.jpg"
          },
          {
            "id": 274,
            "title": "没有软木拖，怎么过夏天",
            "subtitle": "刚入四月，杭州的气温就已升高至30度。店庆时买了软木拖的用户，陆续发回评价说，很...",
            "price": 0.00,
            "readCount": "46.4k",
            "picUrl": "https://yanxuan.nosdn.127.net/14925822213780237.jpg"
          },
          {
            "id": 277,
            "title": "治愈生活的满怀柔软",
            "subtitle": "太鼓抱枕的上架历程，是从失踪开始的。由于表面的绒感，最初它被安排在秋冬季上架。某...",
            "price": 0.00,
            "readCount": "19.6k",
            "picUrl": "https://yanxuan.nosdn.127.net/14926737925770587.jpg"
          },
          {
            "id": 281,
            "title": "条纹新风尚",
            "subtitle": "经典百搭，时尚线条",
            "price": 29.00,
            "readCount": "76.5k",
            "picUrl": "https://yanxuan.nosdn.127.net/14926859849200826.jpg"
          },
          {
            "id": 282,
            "title": "成就一室笋香",
            "subtitle": "三石哥办公室常备小食推荐",
            "price": 12.00,
            "readCount": "40.9k",
            "picUrl": "https://yanxuan.nosdn.127.net/14927695046601069.jpg"
          },
          {
            "id": 283,
            "title": "孩子成长中少不了的一双鞋",
            "subtitle": "说起毛毛虫鞋，好处实在太多了，作为一个2岁孩子的宝妈选品员，按捺不住想告诉大家，...",
            "price": 0.00,
            "readCount": "42.5k",
            "picUrl": "https://yanxuan.nosdn.127.net/14927748974441080.jpg"
          }
        ]
      },
      "errmsg": "成功"
    }

错误码
    
    略
    
#### 2.18.2 专题详情

应用场景

    单个专题详情信息
    
接口链接

    GET /wx/topic/detail

请求参数
    
    id: 专题ID，例如 id=264
    
响应内容

    {
      "errno": 0,
      "data": {
        "topic": {
          "id": 264,
          "title": "设计师们推荐的应季好物",
          "subtitle": "原创设计春款系列上新",
          "price": 29.90,
          "readCount": "77.7k",
          "picUrl": "https://yanxuan.nosdn.127.net/14918201901050274.jpg",
          "sortOrder": 0,
          "goods": [],
          "addTime": "2018-02-01 00:00:00",
          "updateTime": "2018-02-01 00:00:00",
          "deleted": false,
          "content": ""
        },
        "goods": []
      },
      "errmsg": "成功"
    }  

错误码
    
    略

#### 2.18.3 专题推荐

应用场景

    基于某个专题推荐其他专题
    
接口链接

    GET /wx/topic/related

请求参数
    
    id: 专题ID，例如 id=264
    
响应内容

    {
      "errno": 0,
      "data": {
        "total": 19,
        "pages": 5,
        "limit": 4,
        "page": 1,
        "list": [
          {
            "id": 266,
            "title": "一条丝巾就能提升时髦度",
            "subtitle": "不知道大家对去年G20时，严选与国礼制造商一起推出的《凤凰于飞》等几款丝巾是否还...",
            "price": 0.00,
            "readCount": "35.0k",
            "picUrl": "https://yanxuan.nosdn.127.net/14919007135160213.jpg",
            "sortOrder": 0,
            "goods": [],
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false,
            "content": "\u003cimg src\u003d\"//yanxuan.nosdn.127.net/75c55a13fde5eb2bc2dd6813b4c565cc.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/e27e1de2b271a28a21c10213b9df7e95.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/9d413d1d28f753cb19096b533d53418d.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/64b0f2f350969e9818a3b6c43c217325.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/a668e6ae7f1fa45565c1eac221787570.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/0d4004e19728f2707f08f4be79bbc774.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/79ee021bbe97de7ecda691de6787241f.jpg\"\u003e"
          },
          {
            "id": 268,
            "title": "米饭好吃的秘诀：会呼吸的锅",
            "subtitle": "今年1月份，我们联系到了日本伊贺地区的长谷园，那里有着180年伊贺烧历史的窑厂。...",
            "price": 0.00,
            "readCount": "33.3k",
            "picUrl": "https://yanxuan.nosdn.127.net/14920623353130483.jpg",
            "sortOrder": 0,
            "goods": [],
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false,
            "content": "\u003cimg src\u003d\"//yanxuan.nosdn.127.net/75c55a13fde5eb2bc2dd6813b4c565cc.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/e27e1de2b271a28a21c10213b9df7e95.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/9d413d1d28f753cb19096b533d53418d.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/64b0f2f350969e9818a3b6c43c217325.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/a668e6ae7f1fa45565c1eac221787570.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/0d4004e19728f2707f08f4be79bbc774.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/79ee021bbe97de7ecda691de6787241f.jpg\"\u003e"
          },
          {
            "id": 271,
            "title": "选式新懒人",
            "subtitle": "懒出格调，懒出好生活。",
            "price": 15.00,
            "readCount": "57.7k",
            "picUrl": "https://yanxuan.nosdn.127.net/14924199099661697.jpg",
            "sortOrder": 0,
            "goods": [],
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false,
            "content": "\u003cimg src\u003d\"//yanxuan.nosdn.127.net/75c55a13fde5eb2bc2dd6813b4c565cc.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/e27e1de2b271a28a21c10213b9df7e95.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/9d413d1d28f753cb19096b533d53418d.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/64b0f2f350969e9818a3b6c43c217325.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/a668e6ae7f1fa45565c1eac221787570.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/0d4004e19728f2707f08f4be79bbc774.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/79ee021bbe97de7ecda691de6787241f.jpg\"\u003e"
          },
          {
            "id": 272,
            "title": "料理也要精细简单",
            "subtitle": "享受天然的味道，日子每天都好新鲜",
            "price": 69.00,
            "readCount": "125.6k",
            "picUrl": "https://yanxuan.nosdn.127.net/14925200530030186.jpg",
            "sortOrder": 0,
            "goods": [],
            "addTime": "2018-02-01 00:00:00",
            "updateTime": "2018-02-01 00:00:00",
            "deleted": false,
            "content": "\u003cimg src\u003d\"//yanxuan.nosdn.127.net/75c55a13fde5eb2bc2dd6813b4c565cc.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/e27e1de2b271a28a21c10213b9df7e95.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/9d413d1d28f753cb19096b533d53418d.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/64b0f2f350969e9818a3b6c43c217325.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/a668e6ae7f1fa45565c1eac221787570.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/0d4004e19728f2707f08f4be79bbc774.jpg\"\u003e\n    \u003cimg src\u003d\"//yanxuan.nosdn.127.net/79ee021bbe97de7ecda691de6787241f.jpg\"\u003e"
          }
        ]
      },
      "errmsg": "成功"
    }
    
### 2.19 对象存储服务

### 2.20 其他服务


## 3 管理后台API服务

略

## 4 更新日志

略
