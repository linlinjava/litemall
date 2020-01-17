var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var app = getApp()
Page({
  data: {
    topicList: [],
    page: 1,
    limit: 10,
    count: 0,
    scrollTop: 0,
    showPage: false
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.getTopic();
  },
  onReady: function() {
    // 页面渲染完成
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
  nextPage: function(event) {
    var that = this;
    if (this.data.page > that.data.count / that.data.limit) {
      return true;
    }


    that.setData({
      page: that.data.page + 1
    });

    this.getTopic();

  },

  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getTopic();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  getTopic: function() {

    let that = this;
    that.setData({
      scrollTop: 0,
      showPage: false,
      topicList: []
    });
    // 页面渲染完成
    wx.showToast({
      title: '加载中...',
      icon: 'loading',
      duration: 2000
    });

    util.request(api.TopicList, {
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === 0) {

        that.setData({
          scrollTop: 0,
          topicList: res.data.list,
          showPage: true,
          count: res.data.total
        });
      }
      wx.hideToast();
    });

  },
  prevPage: function(event) {
    if (this.data.page <= 1) {
      return false;
    }

    var that = this;
    that.setData({
      page: that.data.page - 1
    });
    this.getTopic();
  }
})