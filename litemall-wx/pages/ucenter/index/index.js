var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var user = require('../../../services/user.js');
var app = getApp();

Page({
  data: {
    userInfo: {
      nickName: '点击登录',
      avatarUrl: 'http://yanxuan.nosdn.127.net/8945ae63d940cc42406c3f67019c5cb6.png'
    }
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {

  },
  onShow: function () {

    //获取用户的登录信息
    user.checkLogin().then(res => {
      let userInfo = wx.getStorageSync('userInfo');

      this.setData({
        userInfo: userInfo,
      });

    }).catch(() => {

    });

  },
  onHide: function () {
    // 页面隐藏

  },
  onUnload: function () {
    // 页面关闭
  },
  goLogin(){
    user.checkLogin().catch(() => {

      user.loginByWeixin().then(res => {
        this.setData({
          userInfo: res.data.userInfo,
        });
      }).catch((err) => {
        util.showErrorToast('登陆失败');
      });

    });
  },
  goOrder() {
    user.checkLogin().then(() => {
      wx.navigateTo({ url: "/pages/ucenter/order/order" });
    })
    .catch(() => {
      wx.navigateTo({ url: "/pages/auth/login/login" });
    });
  },
  goCoupon() {
    user.checkLogin().then(() => {
      wx.navigateTo({ url: "/pages/ucenter/coupon/coupon" });
    })
    .catch(() => {
      wx.navigateTo({ url: "/pages/auth/login/login" });
    });

  },
  goCollect() {
    user.checkLogin().then(() => {
      wx.navigateTo({ url: "/pages/ucenter/collect/collect" });
    })
    .catch(() => {
      wx.navigateTo({ url: "/pages/auth/login/login" });
    });
  },
  goFootprint() {
    user.checkLogin().then(() => {
      wx.navigateTo({ url: "/pages/ucenter/footprint/footprint" });
    })
    .catch(() => {
      wx.navigateTo({ url: "/pages/auth/login/login" });
    });
  },
  goAddress() {
    user.checkLogin().then(() => {
      wx.navigateTo({ url: "/pages/ucenter/address/address" });
    })
    .catch(() => {
      wx.navigateTo({ url: "/pages/auth/login/login" });
    });
  },
  exitLogin: function () {
    wx.showModal({
      title: '',
      confirmColor: '#b4282d',
      content: '退出登录？',
      success: function (res) {
        if (res.confirm) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      }
    })

  }
})