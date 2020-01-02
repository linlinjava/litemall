var app = getApp();
var util = require('../../utils/util.js');

var api = require('../../config/api.js');

Page({
  data: {
    comments: [],
    allCommentList: [],
    picCommentList: [],
    type: 0,
    valueId: 0,
    showType: 0,
    allCount: 0,
    hasPicCount: 0,
    allPage: 1,
    picPage: 1,
    limit: 20
  },
  getCommentCount: function() {
    let that = this;
    util.request(api.CommentCount, {
      valueId: that.data.valueId,
      type: that.data.type
    }).then(function(res) {
      if (res.errno === 0) {

        that.setData({
          allCount: res.data.allCount,
          hasPicCount: res.data.hasPicCount
        });
      }
    });
  },
  getCommentList: function() {
    let that = this;
    util.request(api.CommentList, {
      valueId: that.data.valueId,
      type: that.data.type,
      limit: that.data.limit,
      page: (that.data.showType == 0 ? that.data.allPage : that.data.picPage),
      showType: that.data.showType
    }).then(function(res) {
      if (res.errno === 0) {

        if (that.data.showType == 0) {
          that.setData({
            allCommentList: that.data.allCommentList.concat(res.data.list),
            allPage: res.data.page,
            comments: that.data.allCommentList.concat(res.data.list)
          });
        } else {
          that.setData({
            picCommentList: that.data.picCommentList.concat(res.data.list),
            picPage: res.data.page,
            comments: that.data.picCommentList.concat(res.data.list)
          });
        }
      }
    });
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      type: options.type,
      valueId: options.valueId
    });
    this.getCommentCount();
    this.getCommentList();
  },
  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getCommentCount();
    this.getCommentList();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
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
  switchTab: function () {
    let that = this;
    if (that.data.showType == 0) {
      that.setData({
        allCommentList: [],
        allPage: 1,
        comments: [],
        showType: 1
      });
    } else {
      that.setData({
        picCommentList: [],
        picPage: 1,
        comments: [],
        showType: 0
      });
    }
    this.getCommentList();
  },
  onReachBottom: function() {
    if (this.data.showType == 0) {
      if (this.data.allCount / this.data.limit < this.data.allPage) {
        return false;
      }
      this.setData({
        allPage: this.data.allPage + 1
      });
    } else {
      if (this.data.hasPicCount / this.data.limit < this.data.picPage) {
        return false;
      }
      this.setData({
        picPage: this.data.picPage + 1
      });
    }
    this.getCommentList();
  }
})