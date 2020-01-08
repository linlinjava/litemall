import request from '@/utils/request'

const IndexUrl= 'wx/home/index'; //首页数据接口
export function getHome() {
  return request({
    url: IndexUrl,
    method: 'get'
  })
}

const CatalogList='wx/catalog/index'; //分类目录全部分类数据接口
export function catalogList() {
  return request({
    url: CatalogList,
    method: 'get'
  })
}

const CatalogCurrent='wx/catalog/current'; //分类目录当前分类数据接口
export function catalogCurrent(query) {
  return request({
    url: CatalogCurrent,
    method: 'get',
    params: query
  })
}

const AuthLoginByWeixin='wx/auth/login_by_weixin'; //微信登录


const AuthLoginByAccount='wx/auth/login'; //账号登录
export function authLoginByAccount(data) {
  return request({
    url: AuthLoginByAccount,
    method: 'post',
    data
  })
}
const AuthLogout='wx/auth/logout'; //账号登出
export function authLogout() {
  return request({
    url: AuthLogout,
    method: 'post'
  })
}
const AuthInfo='wx/auth/info'; //用户信息
export function authInfo() {
  return request({
    url: AuthInfo,
    method: 'get'
  })
}
const AuthProfile='wx/auth/profile'; //账号修改
export function authProfile(data) {
  return request({
    url: AuthProfile,
    method: 'post',
    data    
  })
}
const AuthRegister='wx/auth/register'; //账号注册
export function authRegister(data) {
  return request({
    url: AuthRegister,
    method: 'post',
    data
  });
}
const AuthReset='wx/auth/reset'; //账号密码重置
export function authReset(data) {
  return request({
    url: AuthReset,
    method: 'post',
    data    
  })
}
const AuthRegisterCaptcha='wx/auth/regCaptcha'; //注册验证码
export function authRegisterCaptcha(data) {
  return request({
    url: AuthRegisterCaptcha,
    method: 'post',
    data    
  })
}
const AuthCaptcha='wx/auth/captcha'; //验证码
export function authCaptcha(data) {
  return request({
    url: AuthCaptcha,
    method: 'post',
    data    
  })
}

const GoodsCount='wx/goods/count'; //统计商品总数
export function goodsCount() {
  return request({
    url: GoodsCount,
    method: 'get'
  })
}
export const GoodsList='wx/goods/list'; //获得商品列表
export function goodsList(query) {
  return request({
    url: GoodsList,
    method: 'get',
    params: query
  })
}
const GoodsCategory='wx/goods/category'; //获得分类数据
export function goodsCategory(query) {
  return request({
    url: GoodsCategory,
    method: 'get',
    params: query
  })
}
const GoodsDetail='wx/goods/detail'; //获得商品的详情
export function goodsDetail(query) {
  return request({
    url: GoodsDetail,
    method: 'get',
    params: query
  })
}
const GoodsRelated='wx/goods/related'; //商品详情页的关联商品（大家都在看）

const BrandList='wx/brand/list'; //品牌列表
export function brandList(query) {
  return request({
    url: BrandList,
    method: 'get',
    params: query
  })
}
const BrandDetail='wx/brand/detail'; //品牌详情
export function brandDetail(query) {
  return request({
    url: BrandDetail,
    method: 'get',
    params: query
  })
}

const CartList='wx/cart/index'; //获取购物车的数据
export function cartList(query) {
  return request({
    url: CartList,
    method: 'get',
    params: query
  })
}
const CartAdd='wx/cart/add'; // 添加商品到购物车
export function cartAdd(data) {
  return request({
    url: CartAdd,
    method: 'post',
    data
  })
}
const CartFastAdd='wx/cart/fastadd'; // 立即购买商品
export function cartFastAdd(data) {
  return request({
    url: CartFastAdd,
    method: 'post',
    data
  })
}
const CartUpdate='wx/cart/update'; // 更新购物车的商品
export function cartUpdate(data) {
  return request({
    url: CartUpdate,
    method: 'post',
    data
  })
}
const CartDelete='wx/cart/delete'; // 删除购物车的商品
export function cartDelete(data) {
  return request({
    url: CartDelete,
    method: 'post',
    data
  })
}
const CartChecked='wx/cart/checked'; // 选择或取消选择商品
export function cartChecked(data) {
  return request({
    url: CartChecked,
    method: 'post',
    data
  })
}
const CartGoodsCount='wx/cart/goodscount'; // 获取购物车商品件数
export function cartGoodsCount() {
  return request({
    url: CartGoodsCount,
    method: 'get'
  })
}
const CartCheckout='wx/cart/checkout'; // 下单前信息确认
export function cartCheckout(query) {
  return request({
    url: CartCheckout,
    method: 'get',
    params: query
  })
}

const CollectList='wx/collect/list'; //收藏列表
export function collectList(query) {
  return request({
    url: CollectList,
    method: 'get',
    params: query
  })
}
const CollectAddOrDelete='wx/collect/addordelete'; //添加或取消收藏
export function collectAddOrDelete(data) {
  return request({
    url: CollectAddOrDelete,
    method: 'post',
    data
  })
}
const CommentList='wx/comment/list'; //评论列表
const CommentCount='wx/comment/count'; //评论总数
const CommentPost='wx/comment/post'; //发表评论

const TopicList='wx/topic/list'; //专题列表
export function topicList(query) {
  return request({
    url: TopicList,
    method: 'get',
    params: query
  })
}
const TopicDetail='wx/topic/detail'; //专题详情
export function topicDetail(query) {
  return request({
    url: TopicDetail,
    method: 'get',
    params: query
  })
}
const TopicRelated='wx/topic/related'; //相关专题
export function topicRelated(query) {
  return request({
    url: TopicRelated,
    method: 'get',
    params: query
  })
}

const SearchIndex='wx/search/index'; //搜索关键字
const SearchResult='wx/search/result'; //搜索结果
const SearchHelper='wx/search/helper'; //搜索帮助
const SearchClearHistory='wx/search/clearhistory'; //搜索历史清楚

const AddressList='wx/address/list'; //收货地址列表
export function addressList(query) {
  return request({
    url: AddressList,
    method: 'get',
    params: query
  })
}

const AddressDetail='wx/address/detail'; //收货地址详情
export function addressDetail(query) {
  return request({
    url: AddressDetail,
    method: 'get',
    params: query
  })
}
const AddressSave='wx/address/save'; //保存收货地址
export function addressSave(data) {
  return request({
    url: AddressSave,
    method: 'post',
    data
  })
}
const AddressDelete='wx/address/delete'; //保存收货地址
export function addressDelete(data) {
  return request({
    url: AddressDelete,
    method: 'post',
    data
  })
}

const ExpressQuery='wx/express/query'; //物流查询

const OrderSubmit='wx/order/submit'; // 提交订单
export function orderSubmit(data) {
  return request({
    url: OrderSubmit,
    method: 'post',
    data
  })
}
const OrderPrepay='wx/order/prepay'; // 订单的预支付会话
export function orderPrepay(data) {
  return request({
    url: OrderPrepay,
    method: 'post',
    data
  })
}
const OrderH5pay = 'wx/order/h5pay'; // h5支付
export function orderH5pay(data) {
  return request({
    url: OrderH5pay,
    method: 'post',
    data
  });
}
export const OrderList='wx/order/list'; //订单列表
export function orderList(query) {
  return request({
    url: OrderList,
    method: 'get',
    params: query
  })
}
const OrderDetail='wx/order/detail'; //订单详情
export function orderDetail(query) {
  return request({
    url: OrderDetail,
    method: 'get',
    params: query
  })
}
const OrderCancel='wx/order/cancel'; //取消订单
export function orderCancel(data) {
  return request({
    url: OrderCancel,
    method: 'post',
    data
  })
}
const OrderRefund='wx/order/refund'; //退款取消订单
export function orderRefund(data) {
  return request({
    url: OrderRefund,
    method: 'post',
    data
  })
}
const OrderDelete='wx/order/delete'; //删除订单
export function orderDelete(data) {
  return request({
    url: OrderDelete,
    method: 'post',
    data
  })
}
const OrderConfirm='wx/order/confirm'; //确认收货
export function orderConfirm(data) {
  return request({
    url: OrderConfirm,
    method: 'post',
    data
  })
}
const OrderGoods='wx/order/goods'; // 代评价商品信息
const OrderComment='wx/order/comment'; // 评价订单商品信息

const FeedbackAdd='wx/feedback/submit'; //添加反馈
export function feedbackAdd(data) {
  return request({
    url: FeedbackAdd,
    method: 'post',
    data
  })
}

const FootprintList='wx/footprint/list'; //足迹列表
const FootprintDelete='wx/footprint/delete'; //删除足迹

const GrouponList='wx/groupon/list'; //团购列表
export function grouponList(query) {
  return request({
    url: GrouponList,
    method: 'get',
    params: query
  })
}
const GroupOn='wx/groupon/query'; //团购API-查询
const GroupOnMy='wx/groupon/my'; //团购API-我的团购
const GroupOnDetail='wx/groupon/detail'; //团购API-详情
const GroupOnJoin='wx/groupon/join'; //团购API-详情

const CouponList='wx/coupon/list'; //优惠券列表
export function couponList(query) {
  return request({
    url: CouponList,
    method: 'get',
    params: query
  })
}
export const CouponMyList='wx/coupon/mylist'; //我的优惠券列表
export function couponMyList(query) {
  return request({
    url: CouponMyList,
    method: 'get',
    params: query
  })
}
const CouponSelectList='wx/coupon/selectlist'; //当前订单可用优惠券列表
export function couponSelectList(query) {
  return request({
    url: CouponSelectList,
    method: 'get',
    params: query
  })
}
const CouponReceive='wx/coupon/receive'; //优惠券领取
export function couponReceive(data) {
  return request({
    url: CouponReceive,
    method: 'post',
    data
  })
}
const CouponExchange='wx/coupon/exchange'; //优惠券兑换

const StorageUpload='wx/storage/upload'; //图片上传,

const UserIndex='wx/user/index'; //个人页面用户相关信息
export function userIndex() {
  return request({
    url: UserIndex,
    method: 'get'
  })
}
const IssueList='wx/issue/list'; //帮助信息
export function issueList() {
  return request({
    url: IssueList,
    method: 'get'
  })
}

export function getList(api, query) {
  return request({
    url: api,
    method: 'get',
    params: query
  })
}

export const REFUND_LIST = '';
