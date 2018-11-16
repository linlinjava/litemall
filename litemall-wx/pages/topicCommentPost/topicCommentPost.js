// 上传组件 基于https://github.com/Tencent/weui-wxss/tree/master/src/example/uploader
var app = getApp();
var util = require('../../utils/util.js');
var api = require('../../config/api.js');
Page({
  data: {
    valueId: 0,
    topic: {},
    content: '',
    stars: [0, 1, 2, 3, 4],
    star: 5,
    starText: '十分满意',
    hasPicture: false,
    picUrls: [],
    files: []
  },
  chooseImage: function(e) {
    if (this.data.files.length >= 5) {
      util.showErrorToast('只能上传五张图片')
      return false;
    }

    var that = this;
    wx.chooseImage({
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        that.setData({
          files: that.data.files.concat(res.tempFilePaths)
        });
        that.upload(res);
      }
    })
  },
  upload: function(res) {
    var that = this;
    const uploadTask = wx.uploadFile({
      url: api.StorageUpload,
      filePath: res.tempFilePaths[0],
      name: 'file',
      success: function(res) {
        var _res = JSON.parse(res.data);
        if (_res.errno === 0) {
          var url = _res.data.url
          that.data.picUrls.push(url)
          that.setData({
            hasPicture: true,
            picUrls: that.data.picUrls
          })
        }
      },
      fail: function(e) {
        wx.showModal({
          title: '错误',
          content: '上传失败',
          showCancel: false
        })
      },
    })

    uploadTask.onProgressUpdate((res) => {
      console.log('上传进度', res.progress)
      console.log('已经上传的数据长度', res.totalBytesSent)
      console.log('预期需要上传的数据总长度', res.totalBytesExpectedToSend)
    })

  },
  previewImage: function(e) {
    wx.previewImage({
      current: e.currentTarget.id, // 当前显示图片的http链接
      urls: this.data.files // 需要预览的图片http链接列表
    })
  },
  selectRater: function(e) {
    var star = e.currentTarget.dataset.star + 1;
    var starText;
    if (star == 1) {
      starText = '很差';
    } else if (star == 2) {
      starText = '不太满意';
    } else if (star == 3) {
      starText = '满意';
    } else if (star == 4) {
      starText = '比较满意';
    } else {
      starText = '十分满意'
    }
    this.setData({
      star: star,
      starText: starText
    })

  },
  onLoad: function(options) {
    if (parseInt(options.type) !== 1) {
      return;
    }

    var that = this;
    that.setData({
      valueId: options.valueId
    });
    this.getTopic();
  },
  getTopic: function() {
    let that = this;
    util.request(api.TopicDetail, {
      id: that.data.valueId
    }).then(function(res) {
      if (res.errno === 0) {

        that.setData({
          topic: res.data.topic
        });

      }
    });
  },
  onClose: function() {
    wx.navigateBack();
  },
  onPost: function() {
    let that = this;

    if (!this.data.content) {
      util.showErrorToast('请填写评论')
      return false;
    }

    util.request(api.CommentPost, {
      type: 1,
      valueId: that.data.valueId,
      content: that.data.content,
      star: that.data.star,
      hasPicture: that.data.hasPicture,
      picUrls: that.data.picUrls
    }, 'POST').then(function(res) {
      if (res.errno === 0) {
        wx.showToast({
          title: '评论成功',
          complete: function() {
            wx.navigateBack();
          }
        })
      }
    });
  },
  bindInputValue(event) {

    let value = event.detail.value;

    //判断是否超过140个字符
    if (value && value.length > 140) {
      return false;
    }

    this.setData({
      content: event.detail.value,
    })
  },
  onReady: function() {

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
})