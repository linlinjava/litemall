var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data: {
    id: 0,
    orderId: 0,
    groupon: {},
    linkGrouponId: 0,
    joiners: [],
    orderInfo: {},
    orderGoods: [],
    expressInfo: {},
    flag: false,
    handleOption: {}
  },

  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      id: options.id
    });
    this.getOrderDetail();
  },

  // 页面分享
  onShareAppMessage: function() {
    let that = this;
    return {
      title: '邀请团购',
      desc: '唯爱与美食不可辜负',
      path: '/pages/index/index?grouponId=' + this.data.linkGrouponId
    }
  },

  showShare: function() {
    this.sharePop.togglePopup();
  },

  onPullDownRefresh() {
    this.getOrderDetail();
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  expandDetail: function() {
    let that = this;
    this.setData({
      flag: !that.data.flag
    })
  },
  getOrderDetail: function() {
    wx.showLoading({
      title: '加载中',
    });

    setTimeout(function() {
      wx.hideLoading()
    }, 2000);

    let that = this;
    util.request(api.GroupOnDetail, {
      grouponId: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          joiners: res.data.joiners,
          groupon: res.data.groupon,
          linkGrouponId: res.data.linkGrouponId,
          orderId: res.data.orderInfo.id,
          orderInfo: res.data.orderInfo,
          orderGoods: res.data.orderGoods,
          handleOption: res.data.orderInfo.handleOption,
          expressInfo: res.data.expressInfo
        });

        wx.hideLoading();
      }
    });
  },

  onReady: function() {
    // 页面渲染完成
    this.sharePop = this.selectComponent("#sharePop");
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
});