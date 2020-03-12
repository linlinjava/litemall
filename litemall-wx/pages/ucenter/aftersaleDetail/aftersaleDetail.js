var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data: {
    orderId: 0,
    order: {},
    orderGoods: [],
    aftersale: {},
    statusColumns: ['未申请', '已申请，待审核', '审核通过，待退款', '退款成功', '审核不通过，已拒绝'],
    typeColumns: ['未收货退款', '不退货退款', '退货退款'],
    fileList: []
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      orderId: options.id
    });
    this.getAftersaleDetail();
  },
  getAftersaleDetail: function () {
    wx.showLoading({
      title: '加载中',
    });

    setTimeout(function () {
      wx.hideLoading()
    }, 2000);

    let that = this;
    util.request(api.AftersaleDetail, {
      orderId: that.data.orderId
    }).then(function (res) {
      if (res.errno === 0) {
        let _fileList = []
        res.data.aftersale.pictures.forEach(function (v) {
          _fileList.push({
            url: v
          })
        });

        that.setData({
          order: res.data.order,
          orderGoods: res.data.orderGoods,
          aftersale: res.data.aftersale,
          fileList: _fileList
        });
      }

      wx.hideLoading();
    });
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  }
})