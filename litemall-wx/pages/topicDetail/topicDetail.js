var app = getApp();
var WxParse = require('../../lib/wxParse/wxParse.js');
var util = require('../../utils/util.js');
var api = require('../../config/api.js');

Page({
  data: {
    id: 0,
    topic: {},
    topicList: [],
    commentCount: 0,
    commentList: [],
    topicGoods: [],
    collect: false,
    userHasCollect: 0
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
          topic: res.data.topic,
          topicGoods: res.data.goods,
          userHasCollect: res.data.userHasCollect,
          collect: res.data.userHasCollect == 1
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
  getCommentList() {
    let that = this;
    util.request(api.CommentList, {
      valueId: that.data.id,
      type: 1,
      showType: 0,
      page: 1,
      limit: 5
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          commentList: res.data.list,
          commentCount: res.data.total
        });
      }
    });
  },

  //添加或是取消收藏
  addCollectOrNot: function() {
    let that = this;
    util.request(api.CollectAddOrDelete, {
        type: 1,
        valueId: this.data.id
      }, "POST")
      .then(function(res) {
        if (that.data.userHasCollect == 1) {
          that.setData({
            collect: false,
            userHasCollect: 0
          });
        } else {
          that.setData({
            collect: true,
            userHasCollect: 1
          });
        }

      });

  },

  postComment() {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    } else {
      wx.navigateTo({
        url: '/pages/topicCommentPost/topicCommentPost?valueId=' + this.data.id + '&type=1',
      })
    }
  },
  onReady: function() {

  },
  onShow: function() {
    // 页面显示
    this.getCommentList();
  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭

  }
})