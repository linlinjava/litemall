var util = require('../../utils/util.js');
var api = require('../../config/api.js');

Page({
  data: {
    navList: [],
    goodsList: [],
    id: 0,
    currentCategory: {},
    scrollLeft: 0,
    scrollTop: 0,
    scrollHeight: 0,
    page: 1,
    limit: 10,
    pages:1, //总页数
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    var that = this;
    if (options.id) {
      that.setData({
        id: parseInt(options.id)
      });
    }

    wx.getSystemInfo({
      success: function(res) {
        that.setData({
          scrollHeight: res.windowHeight
        });
      }
    });


    this.getCategoryInfo();

  },
  getCategoryInfo: function() {
    let that = this;
    util.request(api.GoodsCategory, {
        id: this.data.id
      })
      .then(function(res) {

        if (res.errno == 0) {
          that.setData({
            navList: res.data.brotherCategory,
            currentCategory: res.data.currentCategory
          });

          wx.setNavigationBarTitle({
            title: res.data.parentCategory.name
          })

          // 当id是L1分类id时，这里需要重新设置成L1分类的一个子分类的id
          if (res.data.parentCategory.id == that.data.id) {
            that.setData({
              id: res.data.currentCategory.id
            });
          }

          //nav位置
          let currentIndex = 0;
          let navListCount = that.data.navList.length;
          for (let i = 0; i < navListCount; i++) {
            currentIndex += 1;
            if (that.data.navList[i].id == that.data.id) {
              break;
            }
          }
          if (currentIndex > navListCount / 2 && navListCount > 5) {
            that.setData({
              scrollLeft: currentIndex * 60
            });
          }
          that.getGoodsList();

        } else {
          //显示错误信息
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
  //触底开始下一页
  onReachBottom: function () {
    var that=this;

    var pagenum = that.data.page + 1; //获取当前页数并+1
    if(pagenum <=that.data.pages){
      that.setData({
        page: pagenum, //更新当前页数
      })
      that.getGoodsList();//重新调用请求获取下一页数据
    }else{
      util.showErrorToast("已经是最后一页了");
    }
  },

  getGoodsList: function() {
    var that = this;

    util.request(api.GoodsList, {
        categoryId: that.data.id,
        page: that.data.page,
        limit: that.data.limit
      })
      .then(function(res) {
        var arr1 = that.data.goodsList; //从data获取当前datalist数组
        var arr2 = res.data.list; //从此次请求返回的数据中获取新数组
        arr1 = arr1.concat(arr2); //合并数组
        that.setData({
          goodsList: arr1,
          pages: res.data.pages //得到总页数
        });
      });
  },
  onUnload: function() {
    // 页面关闭
  },
  switchCate: function(event) {
    if (this.data.id == event.currentTarget.dataset.id) {
      return false;
    }
    var that = this;
    var clientX = event.detail.x;
    var currentTarget = event.currentTarget;
    if (clientX < 60) {
      that.setData({
        scrollLeft: currentTarget.offsetLeft - 60
      });
    } else if (clientX > 330) {
      that.setData({
        scrollLeft: currentTarget.offsetLeft
      });
    }
    this.setData({
      id: event.currentTarget.dataset.id,
      page:1, //从第一页开始查
      goodsList:[]
    });

    this.getCategoryInfo();
  }
})
