var util = require('../../utils/util.js');
var api = require('../../config/api.js');
const pay = require('../../services/pay.js');

var app = getApp();
Page({
  data: {
    status: false,
    orderId: 0
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      orderId: options.orderId,
      status: options.status === '1' ? true : false
    })
  },
  onReady: function () {

  },
  onShow: function () {
    // 页面显示

  },
  onHide: function () {
    // 页面隐藏

  },
  onUnload: function () {
    // 页面关闭

  },
  payOrder() {
    let that = this;
    // 目前不能支持微信支付，这里仅仅是模拟支付成功，同理，后台也仅仅是返回一个成功的消息而已
    wx.showModal({
      title: '目前不能微信支付',
      content: '点击确定模拟支付成功，点击取消模拟未支付成功',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderPay, { orderId: that.data.orderId }, 'POST').then(res => {
            if (res.errno === 0) {
              that.setData({
                status: true
              });
            }
            else {
              util.showErrorToast('支付失败');
            }
          });
        }
        else if (res.cancel) {
          util.showErrorToast('支付失败');
        }

      }
    });

    // pay.payOrder(this.data.orderId).then(res => {
    //   this.setData({
    //     status: true
    //   });
    // }).catch(res => {
    //   util.showErrorToast('支付失败');
    // });
  }
})