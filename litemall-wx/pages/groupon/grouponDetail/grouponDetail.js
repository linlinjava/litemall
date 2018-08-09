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

 shareGroupon: function() {
  let that = this;
  wx.showActionSheet({
   itemList: ['分享给朋友', '分享到朋友圈'],
   success: function(res) {
    if (res.tapIndex == 0) {
     wx.showModal({
      title: '提示',
      content: '点击右上角 "..." 转发给朋友',
      showCancel: false
     });
    } else if (res.tapIndex == 1) {
     that.saveShare();
    } else {
     console.log(res.tapIndex);
    }
   },
   fail: function(res) {
    console.log(res.errMsg);
   }
  })
 },

 // 保存分享图
 saveShare: function() {
  let that = this;
  wx.downloadFile({
   url: that.data.groupon.shareUrl,
   success: function(res) {
    console.log(res)
    wx.saveImageToPhotosAlbum({
     filePath: res.tempFilePath,
     success: function(res) {
      wx.showModal({
       title: '存图成功',
       content: '图片成功保存到相册了，可以分享到朋友圈了',
       showCancel: false,
       confirmText: '好的',
       confirmColor: '#a78845',
       success: function(res) {
        if (res.confirm) {
         console.log('用户点击确定');
        }
       }
      })
     },
     fail: function(res) {
      console.log('fail')
     }
    })
   },
   fail: function() {
    console.log('fail')
   }
  })
 },

 onPullDownRefresh() {
  wx.showNavigationBarLoading() //在标题栏中显示加载
  this.getOrderDetail();
  wx.hideNavigationBarLoading() //完成停止加载
  wx.stopPullDownRefresh() //停止下拉刷新
 },

 //获取物流信息
 getOrderExpress: function() {
  let that = this;
  util.request(api.ExpressQuery, {
   expCode: that.data.orderInfo.expCode,
   expNo: that.data.orderInfo.expNo
  }, 'POST').then(function(res) {
   if (res.errno === 0) {
    that.setData({
     expressInfo: res.data
    });
   }
  });
 },
 expandDetail: function() {
  let that = this;
  this.setData({
   flag: !that.data.flag
  })
 },
 getOrderDetail: function() {
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
     handleOption: res.data.orderInfo.handleOption
    });

    // 请求物流信息,仅当订单状态为发货时才请求
    if (res.data.orderInfo.handleOption.confirm) {
     that.getOrderExpress();
    }
   }
  });
 },
 // “去付款”按钮点击效果
 payOrder: function() {
  let that = this;
  util.request(api.OrderPrepay, {
   orderId: that.data.orderId
  }, 'POST').then(function(res) {
   if (res.errno === 0) {
    const payParam = res.data;
    console.log("支付过程开始");
    wx.requestPayment({
     'timeStamp': payParam.timeStamp,
     'nonceStr': payParam.nonceStr,
     'package': payParam.packageValue,
     'signType': payParam.signType,
     'paySign': payParam.paySign,
     'success': function(res) {
      console.log("支付过程成功");
      util.redirect('/pages/ucenter/order/order');
     },
     'fail': function(res) {
      console.log("支付过程失败");
      util.showErrorToast('支付失败');
     },
     'complete': function(res) {
      console.log("支付过程结束")
     }
    });
   }
  });

 },
 // “取消订单”点击效果
 cancelOrder: function() {
  let that = this;
  let orderInfo = that.data.orderInfo;

  wx.showModal({
   title: '',
   content: '确定要取消此订单？',
   success: function(res) {
    if (res.confirm) {
     util.request(api.OrderCancel, {
      orderId: orderInfo.id
     }, 'POST').then(function(res) {
      if (res.errno === 0) {
       wx.showToast({
        title: '取消订单成功'
       });
       util.redirect('/pages/ucenter/order/order');
      } else {
       util.showErrorToast(res.errmsg);
      }
     });
    }
   }
  });
 },
 // “取消订单并退款”点击效果
 refundOrder: function() {
  let that = this;
  let orderInfo = that.data.orderInfo;

  wx.showModal({
   title: '',
   content: '确定要取消此订单？',
   success: function(res) {
    if (res.confirm) {
     util.request(api.OrderRefund, {
      orderId: orderInfo.id
     }, 'POST').then(function(res) {
      if (res.errno === 0) {
       wx.showToast({
        title: '取消订单成功'
       });
       util.redirect('/pages/ucenter/order/order');
      } else {
       util.showErrorToast(res.errmsg);
      }
     });
    }
   }
  });
 },
 // “删除”点击效果
 deleteOrder: function() {
  let that = this;
  let orderInfo = that.data.orderInfo;

  wx.showModal({
   title: '',
   content: '确定要删除此订单？',
   success: function(res) {
    if (res.confirm) {
     util.request(api.OrderDelete, {
      orderId: orderInfo.id
     }, 'POST').then(function(res) {
      if (res.errno === 0) {
       wx.showToast({
        title: '删除订单成功'
       });
       util.redirect('/pages/ucenter/order/order');
      } else {
       util.showErrorToast(res.errmsg);
      }
     });
    }
   }
  });
 },
 // “确认收货”点击效果
 confirmOrder: function() {
  let that = this;
  let orderInfo = that.data.orderInfo;

  wx.showModal({
   title: '',
   content: '确认收货？',
   success: function(res) {
    if (res.confirm) {
     util.request(api.OrderConfirm, {
      orderId: orderInfo.id
     }, 'POST').then(function(res) {
      if (res.errno === 0) {
       wx.showToast({
        title: '确认收货成功！'
       });
       util.redirect('/pages/ucenter/order/order');
      } else {
       util.showErrorToast(res.errmsg);
      }
     });
    }
   }
  });
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
 }
});