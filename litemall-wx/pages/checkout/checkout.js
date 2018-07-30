var util = require('../../utils/util.js');
var api = require('../../config/api.js');

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
      url: '/pages/ucenter/address/address',
    })
  },
  addAddress() {
    wx.navigateTo({
      url: '/pages/ucenter/addressAdd/addressAdd',
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
    util.request(api.OrderSubmit, { cartId: this.data.cartId, addressId: this.data.addressId, couponId: this.data.couponId }, 'POST').then(res => {
      if (res.errno === 0) {
        const orderId = res.data.orderId;

        // 模拟支付成功，同理，后台也仅仅是返回一个成功的消息而已
        // wx.showModal({
        //   title: '目前不能微信支付',
        //   content: '点击确定模拟支付成功，点击取消模拟未支付成功',
        //   success: function(res) {
        //     if (res.confirm) {
        //       util.request(api.OrderPrepay, { orderId: orderId }, 'POST').then(res => {
        //         if (res.errno === 0) {
        //           wx.redirectTo({
        //             url: '/pages/payResult/payResult?status=1&orderId=' + orderId
        //           });
        //         }
        //         else{
        //           wx.redirectTo({
        //             url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        //           });
        //         }
        //       });
        //     }
        //     else if (res.cancel) {
        //       wx.redirectTo({
        //         url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        //       });
        //     }

        //   }
        // });

        util.request(api.OrderPrepay, {
          orderId: orderId
        }, 'POST').then(function (res) {
          if (res.errno === 0) {
            const payParam = res.data;
            console.log("支付过程开始")
            wx.requestPayment({
              'timeStamp': payParam.timeStamp,
              'nonceStr': payParam.nonceStr,
              'package': payParam.packageValue,
              'signType': payParam.signType,
              'paySign': payParam.paySign,
              'success': function (res) {
                console.log("支付过程成功")
                wx.redirectTo({
                  url: '/pages/payResult/payResult?status=1&orderId=' + orderId
                });
              },
              'fail': function (res) {
                console.log("支付过程失败")
                wx.redirectTo({
                  url: '/pages/payResult/payResult?status=0&orderId=' + orderId
                });
              },
              'complete': function (res) {
                console.log("支付过程结束")
              }
            });
          }
          else{
            wx.redirectTo({
              url: '/pages/payResult/payResult?status=0&orderId=' + orderId
            });
          }
        });

      } else {
        wx.redirectTo({
          url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        });
      }
    });
  }
})