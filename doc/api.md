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

#### 1.4.1 Header&Token

#### 1.4.2 商场Header

#### 1.4.3 管理后台Header

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

## 2 商城API服务

### 2.1 安全服务

#### 2.1.1 注册

#### 2.1.2 登录

#### 2.1.3 账号信息

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

### 2.5 购物车服务

### 2.6 订单服务

### 2.7 会员服务

### 2.8 收货地址服务

#### 2.8.1 收货地址列表

应用场景

    请求用户的收货地址列表

接口链接

    GET /wx/address/list

请求参数

    userId: 用户ID
    
响应结果

    {
        errno: 0,
        errmsg: "成功",，
        list: [AddressVo]
        page:  xx
        limit: xx
        total: xx
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
    
#### 2.9.2 品牌商信息

应用场景

    访问单个品牌商信息
    
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

### 2.12 优惠券服务

### 2.13 反馈服务

### 2.14 足迹服务

### 2.15 团购服务

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