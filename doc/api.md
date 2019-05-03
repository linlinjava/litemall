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
    
#### 1.2.1 操作成功

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
    
#### 1.2.3 对象数组

    {
        errno: 0,
        errmsg: "成功",，
        data: {
            list: [],
            page: xxx,
            limit: xxx,
            total: xxx
         }
    }

list是数组，page、limit和total是分页信息。

### 1.3 错误码

#### 1.3.1 系统通用错误码

#### 1.3.2 商场业务错误码

#### 1.3.3 管理后台业务错误码

### 1.4 Token

#### 1.4.1 Header&Token

#### 1.4.2 商场Header

#### 1.4.3 管理后台Header

### 1.5 API文档格式

接下来会分别从用户层面和管理员层面构建商场API服务和管理后台API服务。

这里定义一个API的格式：

* 应用场景
* 接口链接
* 请求参数
* 响应内容
* 错误码

## 2 商城API服务

### 2.1 安全服务

### 2.2 首页服务

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

### 2.10 收藏服务

### 2.11 评论服务

### 2.12 优惠券服务

### 2.13 反馈服务

### 2.14 足迹服务

### 2.15 团购服务

### 2.16 帮助服务

### 2.17 搜索服务

### 2.18专题服务

### 2.18 对象存储服务


## 3 管理后台API服务

略