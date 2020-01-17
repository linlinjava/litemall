var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data: {
    id: 0,
    groupon: {},
    joiners: [],
    orderInfo: {},
    orderGoods: [],
    rules: {},
    active: 0,
    steps: [],
    activeIcon: '',
    activeColor: ''
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
      path: '/pages/index/index?grouponId=' + this.data.id
    }
  },
  getOrderDetail: function() {
    let that = this;
    util.request(api.GroupOnDetail, {
      grouponId: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {
        let _steps = [{
            text: '已开团'
          },
          {
            text: '开团中'
          },
          {
            text: '开团成功'
          }
        ]
        let _active = res.data.groupon.status
        let _activeIcon = 'success'
        let _activeColor = '#07c160'
        if (res.data.groupon.status === 3) {
          _steps = [{
              text: '已开团'
            },
            {
              text: '开团中'
            },
            {
              text: '开团失败'
            }
          ]
          _active = 2
          _activeIcon = 'fail'
          _activeColor = '#EE0A24'
        }
        that.setData({
          joiners: res.data.joiners,
          groupon: res.data.groupon,
          orderInfo: res.data.orderInfo,
          orderGoods: res.data.orderGoods,
          rules: res.data.rules,
          active: _active,
          steps: _steps,
          activeIcon: _activeIcon,
          activeColor: _activeColor
        });
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