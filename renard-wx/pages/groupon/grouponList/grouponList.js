// pages/groupon/grouponList/grouponList.js
var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    grouponList: [],
    page: 1,
    size: 10,
    count: 0,
    scrollTop: 0,
    showPage: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    this.getGrouponList();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  },
  getGrouponList: function() {

    let that = this;
    that.setData({
      scrollTop: 0,
      showPage: false,
      grouponList: []
    });
    // 页面渲染完成
    wx.showToast({
      title: '加载中...',
      icon: 'loading',
      duration: 2000
    });

    util.request(api.GroupOnList, {
      page: that.data.page,
      size: that.data.size
    }).then(function(res) {
      if (res.errno === 0) {

        that.setData({
          scrollTop: 0,
          grouponList: res.data.data,
          showPage: true,
          count: res.data.count
        });
      }
      wx.hideToast();
    });

  },
  nextPage: function(event) {
    var that = this;
    if (this.data.page > that.data.count / that.data.size) {
      return true;
    }


    that.setData({
      page: that.data.page + 1
    });

    this.getGrouponList();

  },
  prevPage: function(event) {
    if (this.data.page <= 1) {
      return false;
    }

    var that = this;
    that.setData({
      page: that.data.page - 1
    });
    this.getGrouponList();
  }
})