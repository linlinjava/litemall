var util = require('../../utils/util.js');
var api = require('../../config/api.js');

Page({
  data: {
    categoryList: [],
    currentCategory: {},
    currentSubCategoryList: {},
    allList: {},
    scrollLeft: 0,
    scrollTop: 0,
    goodsCount: 0,
    scrollHeight: 0
  },
  onLoad: function(options) {
    this.getCatalog();
  },

  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getCatalog();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  getCatalog: function() {
    //CatalogList
    let that = this;
    wx.showLoading({
      title: '加载中...',
    });
    util.request(api.CatalogAll).then(function(res) {
      that.setData({
        allList: res.data.allList,
        categoryList: res.data.categoryList,
        currentCategory: res.data.currentCategory,
        currentSubCategoryList: res.data.currentSubCategory
      });

    });

    wx.hideLoading();
  },
  getCurrentCategory: function(item) {
    let that = this;

    for (var key in that.data.allList) {
      if (key == item.id) {
        that.setData({
          currentCategory: item,
          currentSubCategoryList: that.data.allList[key]
        });
      }
    }
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

  switchCate: function(event) {
    if (this.data.currentCategory.id == event.currentTarget.dataset.id) {
      return false;
    }

    this.getCurrentCategory(event.currentTarget.dataset.id);
  },
  levelClick: function(e) {
    console.log(e.currentTarget.dataset.id)
    wx.navigateTo({
      url: "/pages/category/category?id=" + e.currentTarget.dataset.id
    })
  }
})