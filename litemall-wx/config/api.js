// 以下是业务服务器API地址
// 本机开发时使用
//  var WxApiRoot = 'http://localhost:8080/wx/';
// 局域网测试使用
// var WxApiRoot = 'http://192.168.0.101:8080/wx/';
// 云平台部署时使用
var WxApiRoot = 'http://122.152.206.172:8080/wx/';
// 云平台上线时使用
// var WxApiRoot = 'https://www.menethil.com.cn/wx/';

module.exports = {
 IndexUrl: WxApiRoot + 'home/index', //首页数据接口
 CatalogList: WxApiRoot + 'catalog/index', //分类目录全部分类数据接口
 CatalogCurrent: WxApiRoot + 'catalog/current', //分类目录当前分类数据接口

 AuthLoginByWeixin: WxApiRoot + 'auth/login_by_weixin', //微信登录
 AuthLoginByAccount: WxApiRoot + 'auth/login', //账号登录
 AuthRegister: WxApiRoot + 'auth/register', //账号注册
 AuthReset: WxApiRoot + 'auth/reset', //账号密码重置
 AuthRegisterCaptcha: WxApiRoot + 'auth/regCaptcha', //验证码

 GoodsCount: WxApiRoot + 'goods/count', //统计商品总数
 GoodsList: WxApiRoot + 'goods/list', //获得商品列表
 GoodsCategory: WxApiRoot + 'goods/category', //获得分类数据
 GoodsDetail: WxApiRoot + 'goods/detail', //获得商品的详情
 GoodsNew: WxApiRoot + 'goods/new', //新品
 GoodsHot: WxApiRoot + 'goods/hot', //热门
 GoodsRelated: WxApiRoot + 'goods/related', //商品详情页的关联商品（大家都在看）

 BrandList: WxApiRoot + 'brand/list', //品牌列表
 BrandDetail: WxApiRoot + 'brand/detail', //品牌详情

 CartList: WxApiRoot + 'cart/index', //获取购物车的数据
 CartAdd: WxApiRoot + 'cart/add', // 添加商品到购物车
 CartFastAdd: WxApiRoot + 'cart/fastadd', // 立即购买商品
 CartUpdate: WxApiRoot + 'cart/update', // 更新购物车的商品
 CartDelete: WxApiRoot + 'cart/delete', // 删除购物车的商品
 CartChecked: WxApiRoot + 'cart/checked', // 选择或取消选择商品
 CartGoodsCount: WxApiRoot + 'cart/goodscount', // 获取购物车商品件数
 CartCheckout: WxApiRoot + 'cart/checkout', // 下单前信息确认

 CollectList: WxApiRoot + 'collect/list', //收藏列表
 CollectAddOrDelete: WxApiRoot + 'collect/addordelete', //添加或取消收藏

 CommentList: WxApiRoot + 'comment/list', //评论列表
 CommentCount: WxApiRoot + 'comment/count', //评论总数
 CommentPost: WxApiRoot + 'comment/post', //发表评论

 TopicList: WxApiRoot + 'topic/list', //专题列表
 TopicDetail: WxApiRoot + 'topic/detail', //专题详情
 TopicRelated: WxApiRoot + 'topic/related', //相关专题

 SearchIndex: WxApiRoot + 'search/index', //搜索关键字
 SearchResult: WxApiRoot + 'search/result', //搜索结果
 SearchHelper: WxApiRoot + 'search/helper', //搜索帮助
 SearchClearHistory: WxApiRoot + 'search/clearhistory', //搜索历史清楚

 AddressList: WxApiRoot + 'address/list', //收货地址列表
 AddressDetail: WxApiRoot + 'address/detail', //收货地址详情
 AddressSave: WxApiRoot + 'address/save', //保存收货地址
 AddressDelete: WxApiRoot + 'address/delete', //保存收货地址

 ExpressQuery: WxApiRoot + 'express/query', //物流查询

 RegionList: WxApiRoot + 'region/list', //获取区域列表

 OrderSubmit: WxApiRoot + 'order/submit', // 提交订单
 OrderPrepay: WxApiRoot + 'order/prepay', // 订单的预支付会话
 OrderList: WxApiRoot + 'order/list', //订单列表
 OrderDetail: WxApiRoot + 'order/detail', //订单详情
 OrderCancel: WxApiRoot + 'order/cancel', //取消订单
 OrderRefund: WxApiRoot + 'order/refund', //退款取消订单
 OrderDelete: WxApiRoot + 'order/delete', //删除订单
 OrderConfirm: WxApiRoot + 'order/confirm', //确认收货
 OrderComment: WxApiRoot + 'order/comment', // 代评价商品信息

 FootprintList: WxApiRoot + 'footprint/list', //足迹列表
 FootprintDelete: WxApiRoot + 'footprint/delete', //删除足迹

 UserFormIdCreate: WxApiRoot + 'formid/create', //用户FromId，用于发送模版消息

  StorageUpload: WxApiRoot + 'storage/upload' //图片上传
};