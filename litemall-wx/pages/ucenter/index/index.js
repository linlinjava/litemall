var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var user = require('../../../utils/user.js');
var app = getApp();

Page({
  data: {
    userInfo: {
      nickName: '点击登录',
      avatarUrl: 'http://yanxuan.nosdn.127.net/8945ae63d940cc42406c3f67019c5cb6.png'
    }
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function() {

  },
  onShow: function() {

    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = wx.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
      });
    }

  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭
  },
  goLogin() {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }
  },
  goOrder() {
    if (app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/ucenter/order/order"
      });
    } else {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }
  },
  goCoupon() {
    if (app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/ucenter/coupon/coupon"
      });
    } else {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    };
 },
 goGroupon() {
  if (app.globalData.hasLogin) {
   wx.navigateTo({
    url: "/pages/groupon/myGroupon/myGroupon"
   });
  } else {
   wx.navigateTo({
    url: "/pages/auth/login/login"
   });
  };
 },
 goCollect() {
  if (app.globalData.hasLogin) {
   wx.navigateTo({
    url: "/pages/ucenter/collect/collect"
   });
  } else {
   wx.navigateTo({
    url: "/pages/auth/login/login"
   });
  };
 },
 goFootprint() {
  if (app.globalData.hasLogin) {
   wx.navigateTo({
    url: "/pages/ucenter/footprint/footprint"
   });
  } else {
   wx.navigateTo({
    url: "/pages/auth/login/login"
   });
  };
 },
 goAddress() {
  if (app.globalData.hasLogin) {
   wx.navigateTo({
    url: "/pages/ucenter/address/address"
   });
  } else {
   wx.navigateTo({
    url: "/pages/auth/login/login"
   });
  };
 },
bindPhoneNumber: function (e) {
  if (e.detail.errMsg !== "getPhoneNumber:ok"){
    // 拒绝授权
    return;
  }

  if (!app.globalData.hasLogin) {
    wx.showToast({
      title: '绑定失败：请先登录',
      icon: 'none',
      duration: 2000
    });
    return;
  }

  util.request(api.AuthBindPhone, { iv: e.detail.iv, encryptedData: e.detail.encryptedData }, 'POST').then(function (res) {
    if (res.errno === 0) {
      wx.showToast({
        title: '绑定手机号码成功',
        icon: 'success',
        duration: 2000
      });
    }
  });
}, 
aboutUs: function () {
  wx.navigateTo({
    url: '/pages/about/about'
  });
}, 
 exitLogin: function() {
  wx.showModal({
   title: '',
   confirmColor: '#b4282d',
   content: '退出登录？',
   success: function(res) {
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