var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data: {
    orderId: 0,
    orderInfo: {},
    orderGoods: [],
    aftersale: {
      pictures: []
    },
    columns: ['未收货退款', '不退货退款', '退货退款'],
    contentLength: 0,
    fileList: []
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      orderId: options.id
    });
    this.getOrderDetail();
  },
  getOrderDetail: function () {
    wx.showLoading({
      title: '加载中',
    });

    setTimeout(function () {
      wx.hideLoading()
    }, 2000);

    let that = this;
    util.request(api.OrderDetail, {
      orderId: that.data.orderId
    }).then(function (res) {
      if (res.errno === 0) {
        console.log(res.data);
        that.setData({
          orderInfo: res.data.orderInfo,
          orderGoods: res.data.orderGoods,
          'aftersale.orderId': that.data.orderId,
          'aftersale.amount': res.data.orderInfo.actualPrice - res.data.orderInfo.freightPrice
        });
      }

      wx.hideLoading();
    });
  },
  deleteImage (event) {
    const { fileList = [] } = this.data;
    fileList.splice(event.detail.index, 1)
    this.setData({
      fileList: fileList
    })
  },
  afterRead(event) {
    const { file } = event.detail
    let that = this
    const uploadTask = wx.uploadFile({
      url: api.StorageUpload,
      filePath: file.path,
      name: 'file',
      success: function (res) {
        var _res = JSON.parse(res.data);
        if (_res.errno === 0) {
          var url = _res.data.url
          that.data.aftersale.pictures.push(url)
          const { fileList = [] } = that.data;
          fileList.push({ ...file, url: url });
          that.setData({
            fileList: fileList
          })
        }
      },
      fail: function (e) {
        wx.showModal({
          title: '错误',
          content: '上传失败',
          showCancel: false
        })
      },
    })
  },
  previewImage: function (e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: this.data.files // 需要预览的图片http链接列表
    })
  },
  contentInput: function (e) {
    this.setData({
      contentLength: e.detail.cursor,
      'aftersale.comment': e.detail.value,
    });
  },
  onReasonChange: function (e) {
    this.setData({
      'aftersale.reason': e.detail,
    });
  },
  showTypePicker: function () {
    this.setData({
      showPicker: true,
    });
  },

  onCancel: function () {
    this.setData({
      showPicker: false,
    });
  },
  onConfirm: function (event) {
    this.setData({
      'aftersale.type': event.detail.index,
      'aftersale.typeDesc': event.detail.value,
      showPicker: false,
    });
  },
  submit: function () {
    let that = this;
    if (that.data.aftersale.type == undefined) {
      util.showErrorToast('请选择退款类型');
      return false;
    }

    if (that.data.reason == '') {
      util.showErrorToast('请输入退款原因');
      return false;
    }

    wx.showLoading({
      title: '提交中...',
      mask: true,
      success: function () {

      }
    });

    util.request(api.AftersaleSubmit, that.data.aftersale, 'POST').then(function (res) {
      wx.hideLoading();

      if (res.errno === 0) {
        wx.showToast({
          title: '申请售后成功',
          icon: 'success',
          duration: 2000,
          complete: function () {
            wx.switchTab({
              url: '/pages/ucenter/index/index'
            });
          }
        });
      } else {
        util.showErrorToast(res.errmsg);
      }

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