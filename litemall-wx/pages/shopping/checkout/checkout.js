var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
const pay = require('../../../services/pay.js');

var app = getApp();

Page({
  data: {
    checkedGoodsList: [],
    checkedAddress: {},
    checkedCoupon: [],
    couponList: [],
    goodsTotalPrice: 0.00, //商品总价
    freightPrice: 0.00,    //快递费
    couponPrice: 0.00,     //优惠券的价格
    orderTotalPrice: 0.00,  //订单总价
    actualPrice: 0.00,     //实际需要支付的总价
    cartId: 0,
    addressId: 0,
    couponId: 0
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
  },
  getCheckoutInfo: function () {
    let that = this;
    util.request(api.CartCheckout, { cartId: that.data.cartId, addressId: that.data.addressId, couponId: that.data.couponId }).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          checkedGoodsList: res.data.checkedGoodsList,
          checkedAddress: res.data.checkedAddress,
          actualPrice: res.data.actualPrice,
          checkedCoupon: res.data.checkedCoupon,
          couponList: res.data.couponList,
          couponPrice: res.data.couponPrice,
          freightPrice: res.data.freightPrice,
          goodsTotalPrice: res.data.goodsTotalPrice,
          orderTotalPrice: res.data.orderTotalPrice,
          addressId: res.data.addressId,
          couponId: res.data.couponId
        });
      }
      wx.hideLoading();
    });
  },
  selectAddress() {
    wx.navigateTo({
      url: '/pages/shopping/address/address',
    })
  },
  addAddress() {
    wx.navigateTo({
      url: '/pages/shopping/addressAdd/addressAdd',
    })
  },
  onReady: function () {
    // 页面渲染完成

  },
  onShow: function () {
    // 页面显示
    wx.showLoading({
      title: '加载中...',
    })
    try {
      var cartId = wx.getStorageSync('cartId');
      if (cartId) {
        this.setData({
          'cartId': cartId
        });
      }

      var addressId = wx.getStorageSync('addressId');
      if (addressId) {
        this.setData({
          'addressId': addressId
        });
      }

      var couponId = wx.getStorageSync('couponId');
      if (couponId) {
        this.setData({
          'couponId': couponId
        });
      }
    } catch (e) {
      // Do something when catch error
      console.log(e);
    }
    this.getCheckoutInfo();
  },
  onHide: function () {
    // 页面隐藏

  },
  onUnload: function () {
    // 页面关闭

  },
  submitOrder: function () {
    if (this.data.addressId <= 0) {
      util.showErrorToast('请选择收货地址');
      return false;
    }
    util.request(api.OrderSubmit, { cartId: 0, addressId: this.data.addressId, couponId: this.data.couponId }, 'POST').then(res => {
      if (res.errno === 0) {
        const orderId = res.data.orderInfo.id;

      // 目前不能支持微信支付，这里仅仅是模拟支付成功，同理，后台也仅仅是返回一个成功的消息而已
          wx.redirectTo({
            url: '/pages/payResult/payResult?status=true&orderId=' + orderId
          });

      //   pay.payOrder(orderId).then(res => {
      //     wx.redirectTo({
      //       url: '/pages/payResult/payResult?status=true&orderId=' + orderId
      //     });
      //   }).catch(res => {
      //     wx.redirectTo({
      //       url: '/pages/payResult/payResult?status=false&orderId=' + orderId
      //     });
      //   });
      // } else {
      //   wx.redirectTo({
      //     url: '/pages/payResult/payResult?status=false&orderId=' + orderId
      //   });
      }
    });
  }
})