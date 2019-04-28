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

const AuthRegister='wx/auth/register'; //账号注册
const AuthReset='wx/auth/reset'; //账号密码重置
const AuthRegisterCaptcha='wx/auth/regCaptcha'; //验证码
const AuthBindPhone='wx/auth/bindPhone'; //绑定微信手机号

const GoodsCount='wx/goods/count'; //统计商品总数
export function goodsCount() {
  return request({
    url: GoodsCount,
    method: 'get'
  })
}
const GoodsList='wx/goods/list'; //获得商品列表
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
const BrandDetail='wx/brand/detail'; //品牌详情

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
    url: cartUpdate,
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

const CollectList='wx/collect/list'; //收藏列表
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
const TopicDetail='wx/topic/detail'; //专题详情
const TopicRelated='wx/topic/related'; //相关专题

const SearchIndex='wx/search/index'; //搜索关键字
const SearchResult='wx/search/result'; //搜索结果
const SearchHelper='wx/search/helper'; //搜索帮助
const SearchClearHistory='wx/search/clearhistory'; //搜索历史清楚

const AddressList='wx/address/list'; //收货地址列表
const AddressDetail='wx/address/detail'; //收货地址详情
const AddressSave='wx/address/save'; //保存收货地址
const AddressDelete='wx/address/delete'; //保存收货地址

const ExpressQuery='wx/express/query'; //物流查询

const RegionList='wx/region/list'; //获取区域列表

const OrderSubmit='wx/order/submit'; // 提交订单
const OrderPrepay='wx/order/prepay'; // 订单的预支付会话
const OrderList='wx/order/list'; //订单列表
const OrderDetail='wx/order/detail'; //订单详情
const OrderCancel='wx/order/cancel'; //取消订单
const OrderRefund='wx/order/refund'; //退款取消订单
const OrderDelete='wx/order/delete'; //删除订单
const OrderConfirm='wx/order/confirm'; //确认收货
const OrderGoods='wx/order/goods'; // 代评价商品信息
const OrderComment='wx/order/comment'; // 评价订单商品信息

const FeedbackAdd='wx/feedback/submit'; //添加反馈
const FootprintList='wx/footprint/list'; //足迹列表
const FootprintDelete='wx/footprint/delete'; //删除足迹

const UserFormIdCreate='wx/formid/create'; //用户FromId，用于发送模版消息

const GroupOnList='wx/groupon/list'; //团购列表
const GroupOn='wx/groupon/query'; //团购API-查询
const GroupOnMy='wx/groupon/my'; //团购API-我的团购
const GroupOnDetail='wx/groupon/detail'; //团购API-详情
const GroupOnJoin='wx/groupon/join'; //团购API-详情

const CouponList='wx/coupon/list'; //优惠券列表
const CouponMyList='wx/coupon/mylist'; //我的优惠券列表
const CouponSelectList='coupon/selectlist'; //当前订单可用优惠券列表
const CouponReceive='wx/coupon/receive'; //优惠券领取
export function couponReceive(data) {
  return request({
    url: CouponReceive,
    method: 'post',
    data
  })
}
const CouponExchange='wx/coupon/exchange'; //优惠券兑换

const StorageUpload='wx/torage/upload'; //图片上传,

const UserIndex='wx/user/index'; //个人页面用户相关信息
export function userIndex() {
  return request({
    url: UserIndex,
    method: 'get'
  })
}
const IssueList='wx/issue/list'; //帮助信息
