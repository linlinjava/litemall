import express from 'express';

const app = express();
app.use(express.json());

const state = {
  order: null,
  callbackMutations: 0,
  productStock: 5,
  couponStatus: '可用'
};

const products = [
  {
    id: 100,
    name: '青瓷杯',
    categoryId: 20,
    brandId: 5,
    isNew: true,
    isHot: true,
    retailPrice: 88.00,
    picUrl: '/static/goods-100.png',
    detail: '<p>天青色手作茶杯</p>',
    stock: 12,
    specification: '天青色'
  },
  {
    id: 101,
    name: '手冲壶',
    categoryId: 20,
    brandId: 5,
    isNew: false,
    isHot: true,
    retailPrice: 128.00,
    picUrl: '/static/goods-101.png',
    detail: '<p>细口控流手冲壶</p>',
    stock: 8,
    specification: '银色'
  },
  {
    id: 102,
    name: '茶盘',
    categoryId: 20,
    brandId: 6,
    isNew: false,
    isHot: false,
    retailPrice: 168.00,
    picUrl: '/static/goods-102.png',
    detail: '<p>竹制茶盘</p>',
    stock: 5,
    specification: '原竹色'
  }
];

const browseState = {
  lastListQuery: ''
};

function resetDemoState() {
  state.order = null;
  state.callbackMutations = 0;
  state.productStock = 5;
  state.couponStatus = '可用';
}

app.get('/', (_req, res) => {
  resetDemoState();
  res.type('html').send(`<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <title>litemall payment flow test harness</title>
  <style>
    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; margin: 48px; }
    main { max-width: 720px; }
    button { margin: 8px 8px 8px 0; padding: 10px 16px; }
    [data-testid] { margin: 10px 0; }
  </style>
</head>
<body>
  <main>
    <h1>litemall 支付回调可靠性 E2E</h1>
    <div data-testid="actual-price">加载中</div>
    <div data-testid="order-sn">未下单</div>
    <div data-testid="order-status">未开始</div>
    <div data-testid="callback-count">0</div>
    <div data-testid="stock-count">5</div>
    <div data-testid="coupon-status">可用</div>
    <button id="submit">提交订单</button>
    <button id="notify">模拟支付回调</button>
    <button id="notify-again">重复回调</button>
    <button id="cancel">取消订单</button>
  </main>
  <script>
    let orderId = null;

    async function checkout() {
      const response = await fetch('/wx/cart/checkout?cartId=9&addressId=7&couponId=-1&userCouponId=-1&grouponRulesId=0');
      const json = await response.json();
      document.querySelector('[data-testid="actual-price"]').textContent = '¥' + json.data.actualPrice.toFixed(2);
      document.querySelector('[data-testid="stock-count"]').textContent = String(json.data.productStock);
      document.querySelector('[data-testid="coupon-status"]').textContent = json.data.couponStatus;
    }

    async function submitOrder() {
      const response = await fetch('/wx/order/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          addressId: 7,
          cartId: 9,
          couponId: -1,
          userCouponId: -1,
          grouponLinkId: 0,
          grouponRulesId: 0,
          message: 'e2e'
        })
      });
      const json = await response.json();
      orderId = json.data.orderId;
      document.querySelector('[data-testid="order-sn"]').textContent = json.data.orderSn;
      await refreshStatus();
    }

    async function notifyPay() {
      await fetch('/wx/order/pay-notify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          orderSn: '20260711000001',
          transactionId: 'wxpay-e2e-001',
          totalFee: 10000
        })
      });
      await refreshStatus();
    }

    async function cancelOrder() {
      await fetch('/wx/order/cancel', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ orderId })
      });
      await refreshStatus();
    }

    async function refreshStatus() {
      if (!orderId) return;
      const response = await fetch('/wx/order/detail?orderId=' + orderId);
      const json = await response.json();
      document.querySelector('[data-testid="order-status"]').textContent = json.data.orderInfo.orderStatusText;
      document.querySelector('[data-testid="callback-count"]').textContent = String(json.data.callbackMutations);
      document.querySelector('[data-testid="stock-count"]').textContent = String(json.data.productStock);
      document.querySelector('[data-testid="coupon-status"]').textContent = json.data.couponStatus;
    }

    document.getElementById('submit').addEventListener('click', submitOrder);
    document.getElementById('notify').addEventListener('click', notifyPay);
    document.getElementById('notify-again').addEventListener('click', notifyPay);
    document.getElementById('cancel').addEventListener('click', cancelOrder);
    checkout();
  </script>
</body>
</html>`);
});

app.get('/browse', (_req, res) => {
  browseState.lastListQuery = '';
  res.type('html').send(`<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <title>litemall product browsing test harness</title>
  <style>
    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; margin: 48px; }
    main { max-width: 760px; }
    button { margin: 8px 8px 8px 0; padding: 10px 16px; }
    [data-testid] { margin: 10px 0; }
    .goods-card { display: block; width: 260px; text-align: left; }
  </style>
</head>
<body>
  <main>
    <h1>litemall 商品浏览 E2E</h1>
    <div data-testid="new-goods-count">0</div>
    <div data-testid="hot-goods-count">0</div>
    <div data-testid="goods-list"></div>
    <button id="filter-new">只看新品</button>
    <div data-testid="query-log">未筛选</div>
    <section aria-label="商品详情">
      <h2 data-testid="product-name">未选择</h2>
      <div data-testid="product-price">未选择</div>
      <div data-testid="product-stock">未选择</div>
      <div data-testid="product-spec">未选择</div>
      <div data-testid="related-goods">未加载</div>
    </section>
  </main>
  <script>
    async function loadHome() {
      const response = await fetch('/wx/home/index');
      const json = await response.json();
      document.querySelector('[data-testid="new-goods-count"]').textContent = String(json.data.newGoodsList.length);
      document.querySelector('[data-testid="hot-goods-count"]').textContent = String(json.data.hotGoodsList.length);
      renderGoods(json.data.newGoodsList.concat(json.data.hotGoodsList));
    }

    function renderGoods(goods) {
      const container = document.querySelector('[data-testid="goods-list"]');
      container.innerHTML = '';
      goods.forEach((item) => {
        const button = document.createElement('button');
        button.className = 'goods-card';
        button.textContent = item.name + ' ¥' + item.retailPrice.toFixed(2);
        button.addEventListener('click', () => loadDetail(item.id));
        container.appendChild(button);
      });
    }

    async function filterNewGoods() {
      const response = await fetch('/wx/goods/list?isNew=true&page=1&limit=10&sort=retail_price&order=asc');
      const json = await response.json();
      renderGoods(json.data.list);
      document.querySelector('[data-testid="query-log"]').textContent = json.data.queryLog;
    }

    async function loadDetail(id) {
      const detailResponse = await fetch('/wx/goods/detail?id=' + id);
      const detailJson = await detailResponse.json();
      const relatedResponse = await fetch('/wx/goods/related?id=' + id);
      const relatedJson = await relatedResponse.json();
      const product = detailJson.data.productList[0];
      document.querySelector('[data-testid="product-name"]').textContent = detailJson.data.info.name;
      document.querySelector('[data-testid="product-price"]').textContent = '¥' + detailJson.data.info.retailPrice.toFixed(2);
      document.querySelector('[data-testid="product-stock"]').textContent = String(product.number);
      document.querySelector('[data-testid="product-spec"]').textContent = detailJson.data.specificationList[0].valueList[0].value;
      document.querySelector('[data-testid="related-goods"]').textContent = relatedJson.data.list.map((item) => item.name).join(',');
    }

    document.getElementById('filter-new').addEventListener('click', filterNewGoods);
    loadHome();
  </script>
</body>
</html>`);
});

app.get('/wx/cart/checkout', (_req, res) => {
  res.json({
    errno: 0,
    data: {
      cartId: 9,
      addressId: 7,
      couponId: -1,
      userCouponId: -1,
      actualPrice: 100.00,
      goodsTotalPrice: 100.00,
      freightPrice: 0,
      couponPrice: 0,
      productStock: state.productStock,
      couponStatus: state.couponStatus
    }
  });
});

app.post('/wx/order/submit', (_req, res) => {
  state.productStock -= 2;
  state.couponStatus = '已使用';
  state.order = {
    id: 1001,
    orderSn: '20260711000001',
    orderStatus: 101,
    orderStatusText: '未付款',
    actualPrice: 100.00,
    payId: null
  };
  state.callbackMutations = 0;
  res.json({
    errno: 0,
    data: {
      orderId: state.order.id,
      orderSn: state.order.orderSn,
      payed: false
    }
  });
});

app.post('/wx/order/cancel', (_req, res) => {
  if (!state.order) {
    res.status(404).json({ errno: 404, errmsg: '订单不存在' });
    return;
  }
  if (state.order.orderStatus !== 101) {
    res.status(409).json({ errno: 720, errmsg: '订单不能取消' });
    return;
  }
  state.order.orderStatus = 102;
  state.order.orderStatusText = '已取消';
  state.productStock += 2;
  state.couponStatus = '可用';
  res.json({ errno: 0, errmsg: '成功' });
});

app.post('/wx/order/pay-notify', (req, res) => {
  if (!state.order) {
    res.status(404).json({ errno: 404, errmsg: '订单不存在' });
    return;
  }
  if (req.body.totalFee !== 10000) {
    res.status(409).json({ errno: 409, errmsg: '支付金额不符合' });
    return;
  }
  if (state.order.orderStatus !== 201) {
    state.order.orderStatus = 201;
    state.order.orderStatusText = '已付款';
    state.order.payId = req.body.transactionId;
    state.callbackMutations += 1;
  }
  res.json({ errno: 0, errmsg: '处理成功' });
});

app.get('/wx/order/detail', (_req, res) => {
  res.json({
    errno: 0,
    data: {
      orderInfo: state.order,
      callbackMutations: state.callbackMutations,
      productStock: state.productStock,
      couponStatus: state.couponStatus
    }
  });
});

app.get('/wx/home/index', (_req, res) => {
  res.json({
    errno: 0,
    errmsg: '成功',
    data: {
      banner: [],
      channel: [{ id: 20, name: '茶器' }],
      couponList: [],
      newGoodsList: products.filter((item) => item.isNew),
      hotGoodsList: products.filter((item) => item.isHot).slice(0, 1),
      brandList: [{ id: 5, name: '器物集' }],
      topicList: [],
      grouponList: [],
      floorGoodsList: [
        {
          id: 20,
          name: '茶器',
          goodsList: products.filter((item) => item.categoryId === 20)
        }
      ]
    }
  });
});

app.get('/wx/goods/list', (req, res) => {
  browseState.lastListQuery = new URLSearchParams(req.query).toString();
  let list = products.slice();
  if (req.query.isNew === 'true') {
    list = list.filter((item) => item.isNew);
  }
  if (req.query.isHot === 'true') {
    list = list.filter((item) => item.isHot);
  }
  if (req.query.keyword) {
    list = list.filter((item) => item.name.includes(String(req.query.keyword)));
  }
  if (req.query.sort === 'retail_price') {
    list.sort((left, right) => left.retailPrice - right.retailPrice);
  }
  if (req.query.order === 'desc') {
    list.reverse();
  }
  res.json({
    errno: 0,
    errmsg: '成功',
    data: {
      list,
      total: list.length,
      page: Number(req.query.page || 1),
      limit: Number(req.query.limit || list.length),
      pages: 1,
      filterCategoryList: [{ id: 20, name: '茶器' }],
      queryLog: browseState.lastListQuery
    }
  });
});

app.get('/wx/goods/detail', (req, res) => {
  const goods = products.find((item) => item.id === Number(req.query.id));
  if (!goods) {
    res.status(404).json({ errno: 402, errmsg: '参数值不对' });
    return;
  }
  res.json({
    errno: 0,
    errmsg: '成功',
    data: {
      info: goods,
      userHasCollect: 0,
      issue: [],
      comment: { count: 0, data: [] },
      specificationList: [
        {
          name: '颜色',
          valueList: [{ value: goods.specification }]
        }
      ],
      productList: [
        {
          id: 9000 + goods.id,
          goodsId: goods.id,
          price: goods.retailPrice,
          number: goods.stock,
          specifications: [goods.specification],
          url: goods.picUrl
        }
      ],
      attribute: [{ attribute: '材质', value: '瓷' }],
      brand: { id: goods.brandId, name: '器物集' },
      groupon: [],
      share: false,
      shareImage: '/static/share-' + goods.id + '.png'
    }
  });
});

app.get('/wx/goods/related', (req, res) => {
  const goods = products.find((item) => item.id === Number(req.query.id));
  if (!goods) {
    res.status(404).json({ errno: 402, errmsg: '参数值不对' });
    return;
  }
  const list = products.filter((item) => item.categoryId === goods.categoryId && item.id !== goods.id);
  res.json({
    errno: 0,
    errmsg: '成功',
    data: {
      list,
      total: list.length,
      page: 1,
      limit: list.length,
      pages: 1
    }
  });
});

app.listen(4179, '127.0.0.1', () => {
  console.log('payment flow harness listening on http://127.0.0.1:4179');
});
