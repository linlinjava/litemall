var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

var app = getApp();

Page({
  data: {
    typeId: 0,
    collectList: []
  },
  getCollectList() {
    let that = this;
    util.request(api.CollectList, { typeId: that.data.typeId}).then(function (res) {
      if (res.errno === 0) {
        console.log(res.data);
        that.setData({
          collectList: res.data.data
        });
      }
    });
  },
  onLoad: function (options) {
    this.getCollectList();
  },
  onReady: function () {

  },
  onShow: function () {

  },
  onHide: function () {
    // 页面隐藏

  },
  onUnload: function () {
    // 页面关闭
  },
  openGoods(event) {
    
    let that = this;
    let goodsId = this.data.collectList[event.currentTarget.dataset.index].valueId;

    //触摸时间距离页面打开的毫秒数  
    var touchTime = that.data.touchEnd - that.data.touchStart;
    console.log(touchTime);
    //如果按下时间大于350为长按  
    if (touchTime > 350) {
      wx.showModal({
        title: '',
        content: '确定删除吗？',
        success: function (res) {
          if (res.confirm) {
            
            util.request(api.CollectAddOrDelete, { typeId: that.data.typeId, valueId: goodsId}, 'POST').then(function (res) {
              if (res.errno === 0) {
                console.log(res.data);
                wx.showToast({
                  title: '删除成功',
                  icon: 'success',
                  duration: 2000
                });
                that.getCollectList();
              }
            });
          }
        }
      })
    } else {
      
      wx.navigateTo({
        url: '/pages/goods/goods?id=' + goodsId,
      });
    }  
  },
  //按下事件开始  
  touchStart: function (e) {
    let that = this;
    that.setData({
      touchStart: e.timeStamp
    })
    console.log(e.timeStamp + '- touch-start')
  },
  //按下事件结束  
  touchEnd: function (e) {
    let that = this;
    that.setData({
      touchEnd: e.timeStamp
    })
    console.log(e.timeStamp + '- touch-end')
  }, 
})