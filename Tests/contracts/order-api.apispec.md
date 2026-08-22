# litemall 订单与支付接口 API Spec

这份文档是 `Tests/contracts/order-api.openapi.yaml` 的人工阅读版，用于培训、评审和 AI Native workflow。当前项目仍保留 Springfox Swagger 配置，但测试体系需要一份随代码版本化的静态 spec，保证 Story、AC、Test Case ID、契约测试和脚本生成使用同一份接口事实。

## 范围

- 后端控制器：`litemall-wx-api/src/main/java/org/linlinjava/litemall/wx/web/WxOrderController.java`
- 前端客户端：`litemall-vue/src/api/api.js`
- 前端开发环境 baseURL：`/wx`
- API 前缀：`/wx/order`
- 机器可读 spec：`Tests/contracts/order-api.openapi.yaml`
- 测试索引：`Tests/test-index.yml`

## 通用约定

| 项 | 约定 |
| --- | --- |
| 登录态 | 前端请求拦截器通过 `X-Litemall-Token` 传递本地 token |
| JSON 响应 | `{ errno, errmsg, data? }` |
| 成功码 | `errno = 0` |
| 未登录 | `errno = 501` |
| 参数错误 | `errno = 401 / 402` |
| 乐观锁过期 | `errno = 504` |
| 支付回调 | 微信服务端 XML callback，响应也是 XML |

## 接口清单

| Test Case ID | Method | Path | 前端路径 | 后端 mapping | 关键验证 |
| --- | --- | --- | --- | --- | --- |
| - | GET | `/wx/order/list` | `/order/list` | `list` | 订单分页列表 |
| `MALL-PAY-AC08-FE-IT-001` | GET | `/wx/order/detail` | `/order/detail` | `detail` | `orderId` 查询参数 |
| `MALL-PAY-AC07-FE-IT-001` | POST | `/wx/order/submit` | `/order/submit` | `submit` | 下单 payload 字段完整 |
| `MALL-PAY-AC09-FE-IT-001` | POST | `/wx/order/cancel` | `/order/cancel` | `cancel` | 取消未支付订单，库存和优惠券补偿 |
| `MALL-PAY-AC01-FE-IT-001` | POST | `/wx/order/prepay` | `/order/prepay` | `prepay` | JSAPI 预支付 |
| `MALL-PAY-AC01-FE-IT-002` | POST | `/wx/order/h5pay` | `/order/h5pay` | `h5pay` | H5 支付 |
| `MALL-PAY-AC04-CONTRACT-001` | POST | `/wx/order/pay-notify` | - | `pay-notify` | 微信 XML 回调字段、金额一致性、幂等 |
| `MALL-PAY-AC12-FE-IT-001` | POST | `/wx/order/refund` | `/order/refund` | `refund` | 退款申请 |
| `MALL-PAY-AC13-FE-IT-001` | POST | `/wx/order/confirm` | `/order/confirm` | `confirm` | 确认收货 |
| `MALL-PAY-AC14-FE-IT-001` | POST | `/wx/order/delete` | `/order/delete` | `delete` | 删除可删除订单 |
| - | GET | `/wx/order/goods` | - | `goods` | 待评价订单商品 |
| - | POST | `/wx/order/comment` | - | `comment` | 订单商品评价 |

## 关键请求体

### 提交订单

```json
{
  "addressId": 1,
  "cartId": 0,
  "couponId": 0,
  "userCouponId": 0,
  "grouponLinkId": null,
  "grouponRulesId": null,
  "message": "请尽快发货"
}
```

### 订单 ID 请求

这些接口共用同一请求体：`prepay`、`h5pay`、`cancel`、`refund`、`confirm`、`delete`。

```json
{
  "orderId": 1001
}
```

### 微信支付回调 XML

```xml
<xml>
  <return_code><![CDATA[SUCCESS]]></return_code>
  <result_code><![CDATA[SUCCESS]]></result_code>
  <out_trade_no><![CDATA[20260711000001]]></out_trade_no>
  <transaction_id><![CDATA[wxpay-transaction-001]]></transaction_id>
  <total_fee>10000</total_fee>
</xml>
```

完整 fixture 见 `Tests/contracts/wechat-pay-notify-success.xml`。

## 测试闭环

- `FrontendBackendOrderContractTest` 校验前端路径、后端 mapping、HTTP method 和 OpenAPI spec 对齐。
- `PaymentNotifyXmlContractTest` 校验微信 XML fixture 的必填字段，并确认 fixture 可装载为微信 SDK notify result 模型。
- `test-index.yml` 把接口 spec、contract、前端 IT、后端 UT、后端 IT、E2E 串成可追踪测试资产。
