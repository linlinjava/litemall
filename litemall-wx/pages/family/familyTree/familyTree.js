// pages/family/familyTree/familyTree.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    hotGoods: [{ "id": 1152008, "name": "1卷一彩图1.pdf", "brief": "晋阳堂，中华匡式通谱，卷四人物篇", "picUrl": "https://obs.cn-north-1.myhuaweicloud.com/litemalltest/abc.pdf", "addtime": "2019-07-20"}, { "id": 1152009, "name": "2四.pdf", "brief": "古代人物，近现代人物，族贤", "addtime": "2019-07-20", "picUrl": "http://yanxuan.nosdn.127.net/ae6d41117717387b82dcaf1dfce0cd97.png"}, { "id": 1152031, "name": "3卷四彩页", "brief": "族贤·出资人简介、全家福", "picUrl": "http://yanxuan.nosdn.127.net/fd6e78a397bd9e9804116a36f0270b0a.png"}, { "id": 1022000, "name": "4卷四", "brief": "一二三四五六七八九十一二三四五六七八九十", "picUrl": "http://yanxuan.nosdn.127.net/5350e35e6f22165f38928f3c2c52ac57.png"}, { "id": 1011004, "name": "卷二", "brief": "一二三四五六七一二三四五六七一二三四五六七", "picUrl": "http://yanxuan.nosdn.127.net/0984c9388a2c3fd2335779da904be393.png"}]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    console.log("下拉刷新中.......");
    this.setData({
      hotGoods: [{ "id": 1152008, "name": "1卷一彩图1.pdf", "brief": "晋阳堂，中华匡式通谱，卷四人物篇", "picUrl": "https://obs.cn-north-1.myhuaweicloud.com/litemalltest/abc.pdf", "addtime": "2019-07-20"}, { "id": 1152009, "name": "2四.pdf", "brief": "古代人物，近现代人物，族贤", "addtime": "2019-07-20", "picUrl": "http://yanxuan.nosdn.127.net/ae6d41117717387b82dcaf1dfce0cd97.png"}, { "id": 1152031, "name": "3卷四彩页", "brief": "族贤·出资人简介、全家福", "picUrl": "http://yanxuan.nosdn.127.net/fd6e78a397bd9e9804116a36f0270b0a.png"}]
    });
    this.sleep(1000);
    wx.stopPullDownRefresh();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  openpdf: function(event){
    wx.showLoading({
      title: '加载中'
    });
    var that = this;
    var id = event.currentTarget.dataset.path;
    wx.downloadFile({
      url: id,
      success: function (res) {
        var path = res.tempFilePath;
        wx.openDocument({
          filePath: path,
          success: function (res) {
            wx.hideLoading()
          }
        })
      }, fail: function (res) {
        wx.hideLoading()
      }
    })
  },
  sleep: function(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while(true) {
      now = new Date();
      if (now.getTime() > exitTime)
        return;
    }
  }
})