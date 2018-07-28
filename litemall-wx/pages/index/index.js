const util = require('../../utils/util.js');
const api = require('../../config/api.js');
const user = require('../../utils/user.js');

//获取应用实例
const app = getApp()
Page({
 data: {
  newGoods: [],
  hotGoods: [],
  topics: [],
  brands: [],
  floorGoods: [],
  banner: [],
  channel: []
 },

 onShareAppMessage: function() {
  return {
   title: 'litemall小程序商场',
   desc: '开源微信小程序商城',
   path: '/pages/index/index'
  }
 },

 onPullDownRefresh() {
  wx.showNavigationBarLoading() //在标题栏中显示加载
  this.getIndexData();
  wx.hideNavigationBarLoading() //完成停止加载
  wx.stopPullDownRefresh() //停止下拉刷新
 },

 getIndexData: function() {
  let that = this;
  util.request(api.IndexUrl).then(function(res) {
   if (res.errno === 0) {
    that.setData({
     newGoods: res.data.newGoodsList,
     hotGoods: res.data.hotGoodsList,
     topics: res.data.topicList,
     brands: res.data.brandList,
     floorGoods: res.data.floorGoodsList,
     banner: res.data.banner,
     channel: res.data.channel
    });
   }
  });
 },
 onLoad: function(options) {

  // 页面初始化 options为页面跳转所带来的参数
  if (options.scene) {
   //这个scene的值存在则证明首页的开启来源于朋友圈分享的图,同时可以通过获取到的goodId的值跳转导航到对应的详情页
   var scene = decodeURIComponent(options.scene);
   console.log("scene:" + scene);
   wx.navigateTo({
    url: '../goods/goods?id=' + scene
   });
  }

  // 页面初始化 options为页面跳转所带来的参数
  if (options.goodId) {
   //这个goodId的值存在则证明首页的开启来源于分享,同时可以通过获取到的goodId的值跳转导航到对应的详情页
   wx.navigateTo({
    url: '../goods/goods?id=' + options.goodId
   });
  }

  // 页面初始化 options为页面跳转所带来的参数
  if (options.orderId) {
   //这个orderId的值存在则证明首页的开启来源于订单模版通知,同时可以通过获取到的pageId的值跳转导航到对应的详情页
   wx.navigateTo({
    url: '../ucenter/orderDetail/orderDetail?id=' + options.orderId
   });
  }

  this.getIndexData();
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
})