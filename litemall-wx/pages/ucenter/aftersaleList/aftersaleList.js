var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data: {
    aftersaleList: [],
    showType: 1,
    page: 1,
    limit: 10,
    totalPages: 1
  },
  onLoad: function (options) {
  },
  getAftersaleList() {
    let that = this;
    util.request(api.AftersaleList, {
      status: that.data.showType,
      page: that.data.page,
      limit: that.data.limit
    }).then(function (res) {
      if (res.errno === 0) {
        console.log(res.data);
        that.setData({
          aftersaleList: that.data.aftersaleList.concat(res.data.list),
          totalPages: res.data.pages
        });
      }
    });
  },
  onReachBottom() {
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getAftersaleList();
    } else {
      wx.showToast({
        title: '没有更多售后了',
        icon: 'none',
        duration: 2000
      });
      return false;
    }
  },
  switchTab: function (event) {
    let showType = event.currentTarget.dataset.index;
    this.setData({
      aftersaleList: [],
      showType: showType,
      page: 1,
      limit: 10,
      totalPages: 1
    });
    this.getAftersaleList();
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
    this.getAftersaleList();
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  }
})