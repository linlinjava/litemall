var app = getApp();
var WxParse = require('../../lib/wxParse/wxParse.js');
var util = require('../../utils/util.js');
var api = require('../../config/api.js');

Page({
  data: {
    id: 0,
    topic: {},
    topicList: []
  },

  onShareAppMessage: function() {
    return {
      title: 'Renard 的甜品屋',
      desc: '唯爱与美食不可辜负',
      path: '/pages/topicDetail/topicDetail?id=' + this.data.id
    }
  },

  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    var that = this;
    that.setData({
      id: options.id
    });

    util.request(api.TopicDetail, {
      id: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {

        that.setData({
          topic: res.data.topic
        });

        WxParse.wxParse('topicDetail', 'html', res.data.topic.content, that);
      }
    });

    util.request(api.TopicRelated, {
      id: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          topicList: res.data.list
        });
      }
    });
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

  }
})