# 前后端接口文档

本项目前后端接口规范和接口文档。

本项目没有采用Swagger技术，开发者可以自行集成。

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

* page, 和通常计算机概念中数组下标从0开始不同，这里的page参数应该从1开始，
1即代表第一页数据.
* limit
* sort, 例如'add_time'或者'id'.
* order, 只能是"desc"或者'asc'.

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
        errmsg: xxx,，
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
        errmsg: "成功",，
    }

#### 1.2.3 普通对象

    {
        errno: 0,
        errmsg: "成功",，
        data: {}
    }
    
#### 1.2.4 数组对象

    {
        errno: 0,
        errmsg: "成功",，
        data: {
            list: [],
            total: XX
        }
    }

list是对象数组，total是总的数量。

### 1.3 错误码

#### 1.3.1 系统通用错误码

#### 1.3.2 商场业务错误码

#### 1.3.3 管理后台业务错误码

### 1.4 Token

前后端采用token来验证访问权限。

#### 1.4.1 Header&Token

1. 前端访问商场登录API`/wx/auth/login`或者管理后台登录API`/admin/auth/login`

        POST /wx/auth/login
    
        {
            "username": "user123",
            "password': "user123'
        }

2. 成功以后，会接收后端想要的一个token，

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

#### 1.4.2 商场Header

访问受保护商场API采用自定义`X-Litemall-Token`头部

#### 1.4.3 管理后台Header

访问受保护管理后台API则是自定义`X-Litemall-Admin-Token`头部。

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

![](./pic/get.png)    

#### 1.8.2 GET & Token 示例

如果需要登录才能访问数据，则需要先向后端请求登录，得到token，然后请求时携带token。

例如测试2.8.1节收货地址列表API

如果没有登录，则返回未登录信息

![](./pic/get_no_token.png)

因此测试这些API，需要先登录

![](./pic/login.png)

然后，采用自定义`X-Litemall-Token`来携带token访问商场API

![](./pic/get_with_token.png)

注意：
> 访问受保护商场API是采用自定义`X-Litemall-Token`头部；
> 而访问受保护管理后台API则是自定义`X-Litemall-Admin-Token`头部。

#### 1.8.3 POST 示例

通常POST请求后端时，都需要先登录才能有权限上传数据，因此这里不举例说明。

#### 1.8.4 POST & Token 示例

如果需要登录才能提交数据，则需要先向后端请求登录，得到token，然后请求时携带token。

![](./pic/post_no_token.png)

因此测试这些API，需要先登录

![](./pic/login.png)

然后，采用自定义`X-Litemall-Token`来携带token访问商场API

![](./pic/get_with_token.png)

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

### 1.11 NO Swagger

暂不支持Swagger，基于以下考虑：

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

如果开发者需要Swagger，可以自行接入。

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

    微信登录
    
接口链接

    xxx
    
请求参数

    xxx
    
响应内容

    xxx
    
错误码

    xxx
    
#### 2.1.3 注册

#### 2.1.4 退出

#### 2.1.5 注册验证码

#### 2.1.6 验证码

#### 2.1.7 账号密码修改

#### 2.1.8 微信手机号码绑定

#### 2.1.9 手机号码修改

#### 2.1.10 账号信息修改

### 2.2 首页服务

#### 2.2.1 首页数据

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

### 2.6 订单服务
    
#### 2.6.1 订单列表

应用场景

    订单列表

接口链接


请求参数

    
响应结果

错误码

    略
    
#### 2.6.2 订单详情

应用场景

    订单详情

接口链接

请求参数
    
响应结果

错误码

    略
    
#### 2.6.3 创建新订单

应用场景

    创建新订单

接口链接


请求参数

    
响应结果


错误码

    略
    
#### 2.6.4 取消订单

应用场景

    取消订单

接口链接


请求参数

    
响应结果


错误码

    略

#### 2.6.4 订单的微信预支付交易单

应用场景

    订单的微信预支付交易单

接口链接


请求参数

    
响应结果

    {
        errno: 0,
        errmsg: "成功"
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

    userId: 用户ID
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

    userId: 用户ID
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

### 2.13 反馈服务

### 2.14 足迹服务

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


## 3 管理后台API服务

略