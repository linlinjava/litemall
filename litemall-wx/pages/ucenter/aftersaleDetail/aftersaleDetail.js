var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data: {
    aftersaleId: 0,
    order: {},
    orderGoods: [],
    aftersale: {},
    statusColumns: ['已申请', '处理中', '退款成功', '已拒绝'],
    typeColumns: ['未收货退款', '不退货退款', '退货退款'],
    fileList: []
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      aftersaleId: options.id
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
      id: that.data.aftersaleId
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