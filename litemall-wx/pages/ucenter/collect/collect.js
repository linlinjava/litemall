var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

var app = getApp();

Page({
  data: {
    type: 0,
    collectList: [],
    page: 1,
    limit: 10,
    totalPages: 1
  },
  getCollectList() {
    wx.showLoading({
      title: '加载中...',
    });
    let that = this;
    util.request(api.CollectList, {
      type: that.data.type,
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          collectList: that.data.collectList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
    }).finally(() => {
      wx.hideLoading();
    });
  },
  switchTab: function(event) {
    let type = event.currentTarget.dataset.index;
    this.setData({
      collectList: [],
      type,
      page: 1,
      limit: 10,
      totalPages: 1
    });
    this.getCollectList();
  },
  onLoad: function(options) {
    this.getCollectList();
  },
  onReachBottom() {
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getCollectList();
    } else {
      wx.showToast({
        title: '没有更多用户收藏了',
        icon: 'none',
        duration: 2000
      });
      return false;
    }
  },
  onReady: function() {

  },
  onShow: function() {

  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭
  },
  openCollect(event) {
    let that = this;
    let index = event.currentTarget.dataset.index;
    let valueId = this.data.collectList[index].valueId;

    //触摸时间距离页面打开的毫秒数  
    var touchTime = that.data.touchEnd - that.data.touchStart;
    //如果按下时间大于350为长按  
    if (touchTime > 350) {
      wx.showModal({
        title: '',
        content: '确定删除吗？',
        success: function(res) {
          if (res.confirm) {

            util.request(api.CollectAddOrDelete, {
              type: that.data.type,
              valueId: valueId
            }, 'POST').then(function(res) {
              if (res.errno === 0) {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success',
                  duration: 2000
                });
                that.data.collectList.splice(index, 1)
                that.setData({
                  collectList: that.data.collectList
                });
              }
            });
          }
        }
      })
    } else {
      var prefix = '/pages/goods/goods?id='
      if(this.data.type == 1){
        prefix = "/pages/topicDetail/topicDetail?id="
      }
      wx.navigateTo({
        url: prefix + valueId,
      });
    }
  },
  //按下事件开始  
  touchStart: function(e) {
    let that = this;
    that.setData({
      touchStart: e.timeStamp
    })
  },
  //按下事件结束  
  touchEnd: function(e) {
    let that = this;
    that.setData({
      touchEnd: e.timeStamp
    })
  },
})