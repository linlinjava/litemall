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

### 2.19 对象存储服务


## 3 管理后台API服务

略