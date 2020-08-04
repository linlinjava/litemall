var api = require('../../../config/api.js');
var check = require('../../../utils/check.js');

var app = getApp();
Page({
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    mobile: '',
    code: ''
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    // 页面渲染完成

  },
  onReady: function() {

  },
  onShow: function() {
    // 页面显示

  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭

  },
  sendCode: function() {
    let that = this;

    if (this.data.mobile.length == 0) {
      wx.showModal({
        title: '错误信息',
        content: '手机号不能为空',
        showCancel: false
      });
      return false;
    }

    wx.request({
      url: api.AuthRegisterCaptcha,
      data: {
        mobile: that.data.mobile
      },
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res) {
        if (res.data.errno == 0) {
          wx.showModal({
            title: '发送成功',
            content: '验证码已发送',
            showCancel: false
          });
        } else {
          wx.showModal({
            title: '错误信息',
            content: res.data.errmsg,
            showCancel: false
          });
        }
      }
    });
  },
  requestRegister: function(wxCode) {
    let that = this;
    wx.request({
      url: api.AuthRegister,
      data: {
        username: that.data.username,
        password: that.data.password,
        mobile: that.data.mobile,
        code: that.data.code,
        wxCode: wxCode
      },
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res) {
        if (res.data.errno == 0) {
          app.globalData.hasLogin = true;
          wx.setStorageSync('userInfo', res.data.data.userInfo);
          wx.setStorage({
            key: "token",
            data: res.data.data.token,
            success: function() {
              wx.switchTab({
                url: '/pages/ucenter/index/index'
              });
            }
          });
        } else {
          wx.showModal({
            title: '错误信息',
            content: res.data.errmsg,
            showCancel: false
          });
        }
      }
    });
  },
  startRegister: function() {
    var that = this;

    if (this.data.password.length < 6 || this.data.username.length < 6) {
      wx.showModal({
        title: '错误信息',
        content: '用户名和密码不得少于6位',
        showCancel: false
      });
      return false;
    }

    if (this.data.password != this.data.confirmPassword) {
      wx.showModal({
        title: '错误信息',
        content: '确认密码不一致',
        showCancel: false
      });
      return false;
    }

    if (this.data.mobile.length == 0 || this.data.code.length == 0) {
      wx.showModal({
        title: '错误信息',
        content: '手机号和验证码不能为空',
        showCancel: false
      });
      return false;
    }

    wx.login({
      success: function(res) {
        if (!res.code) {
          wx.showModal({
            title: '错误信息',
            content: '注册失败',
            showCancel: false
          });
        }

        that.requestRegister(res.code);
      }
    });
  },
  bindUsernameInput: function(e) {

    this.setData({
      username: e.detail.value
    });
  },
  bindPasswordInput: function(e) {

    this.setData({
      password: e.detail.value
    });
  },
  bindConfirmPasswordInput: function(e) {

    this.setData({
      confirmPassword: e.detail.value
    });
  },
  bindMobileInput: function(e) {

    this.setData({
      mobile: e.detail.value
    });
  },
  bindCodeInput: function(e) {

    this.setData({
      code: e.detail.value
    });
  },
  clearInput: function(e) {
    switch (e.currentTarget.id) {
      case 'clear-username':
        this.setData({
          username: ''
        });
        break;
      case 'clear-password':
        this.setData({
          password: ''
        });
        break;
      case 'clear-confirm-password':
        this.setData({
          confirmPassword: ''
        });
        break;
      case 'clear-mobile':
        this.setData({
          mobile: ''
        });
        break;
      case 'clear-code':
        this.setData({
          code: ''
        });
        break;
    }
  }
})