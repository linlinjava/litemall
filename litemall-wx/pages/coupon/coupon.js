var util = require('../../utils/util.js');
var api = require('../../config/api.js');

var app = getApp();

Page({
  data: {
    couponList: [],
    page: 1,
    size: 10,
    count: 0,
    scrollTop: 0,
    showPage: false
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getCouponList();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  getCouponList: function () {

    let that = this;
    that.setData({
      scrollTop: 0,
      showPage: false,
      couponList: []
    });
    // 页面渲染完成
    wx.showToast({
      title: '加载中...',
      icon: 'loading',
      duration: 2000
    });

    util.request(api.CouponList, {
      page: that.data.page,
      size: that.data.size
    }).then(function (res) {
      if (res.errno === 0) {

        that.setData({
          scrollTop: 0,
          couponList: res.data.data,
          showPage: true,
          count: res.data.count
        });
      }
      wx.hideToast();
    });

  },
  getCoupon(e) {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }

    let couponId = e.currentTarget.dataset.index
    util.request(api.CouponReceive, {
      couponId: couponId
    }, 'POST').then(res => {
      if (res.errno === 0) {
        wx.showToast({
          title: "领取成功"
        })
      }
      else {
        util.showErrorToast(res.errmsg);
      }
    })
  },
  nextPage: function (event) {
    var that = this;
    if (this.data.page > that.data.count / that.data.size) {
      return true;
    }

    that.setData({
      page: that.data.page + 1
    });

    this.getCouponList();

  },
  prevPage: function (event) {
    if (this.data.page <= 1) {
      return false;
    }

    var that = this;
    that.setData({
      page: that.data.page - 1
    });
    this.getCouponList();
  }
})