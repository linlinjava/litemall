// index.js
var app = getApp()
var QQMapWX = require('../../utils/qqmap-wx-jssdk.js');
var util = require("../../utils/util.js");
var qqmapsdk;

var api = require("../../config/api.js");
Page({

  /**
   * 页面的初始数据
   */
  data: {
    load_statue: true,
    shopInfo: {
      // pic: res.data.data.pic,
      name: 'xxx有限公司',
      address: 'https://github.com/linlinjava/litemall',
      latitude: 22.60,
      longitude: 116.87,
      linkPhone: '0755-1234-5678',
      qqNumber: '738696120'
    },
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    
    // qqmapsdk = new QQMapWX({

    //   key: ''//此处使用你自己申请的key

    // });
  },

  showLocation: function (e) {
    var that = this
    wx.openLocation({
      latitude: that.data.shopInfo.latitude,
      longitude: that.data.shopInfo.longitude,
      name: that.data.shopInfo.name,
      address: that.data.shopInfo.address,
    })
  },
  callPhone: function (e) {
    var that = this
    wx.makePhoneCall({
      phoneNumber: that.data.shopInfo.linkPhone,
    })
  },
  reLoad: function (e) {
    this.loadShopInfo();
  }
})