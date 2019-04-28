import Notify from '../../components/notify/index';
var app = getApp();
var WxParse = require('../../lib/wxParse/wxParse.js');
var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var user = require('../../utils/user.js');

Page({
  data: {
    id: 0,
    goods: {},
    groupon: [], //该商品支持的团购规格
    grouponLink: {}, //参与的团购
    attribute: [],
    issueList: [],
    comment: [],
    brand: {},
    specificationList: [],
    productList: [],
    relatedGoods: [],
    cartGoodsCount: 0,
    userHasCollect: 0,
    number: 1,
    checkedSpecText: '规格数量选择',
    tmpSpecText: '请选择规格数量',
    checkedSpecPrice: 0,
    openAttr: false,
    noCollectImage: '/static/images/icon_collect.png',
    hasCollectImage: '/static/images/icon_collect_checked.png',
    collectImage: '/static/images/icon_collect.png',
    shareImage: '',
    isGroupon: false, //标识是否是一个参团购买
    soldout: false,
    canWrite: false
  },

  // 页面分享
  onShareAppMessage: function() {
    let that = this;
    return {
      title: that.data.goods.name,
      desc: '唯爱与美食不可辜负',
      path: '/pages/index/index?goodId=' + this.data.id
    }
  },
  handleSetting: function(e) {
      var that = this;
      // console.log(e)
      if (!e.detail.authSetting['scope.writePhotosAlbum']) {
          wx.showModal({
              title: '警告',
              content: '不授权无法保存',
              showCancel: false
          })
          that.setData({
              canWrite: false
          })
      } else {
          wx.showToast({
              title: '保存成功'
          })
          that.setData({
              canWrite: true
          })
      }
  },
  showShare: function() {
    this.sharePop.togglePopup();
  },

  //从分享的团购进入
  getGrouponInfo: function(grouponId) {
    let that = this;
    util.request(api.GroupOnJoin, {
      grouponId: grouponId
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          grouponLink: res.data.groupon,
          id: res.data.goods.id
        });
        //获取商品详情
        that.getGoodsInfo();
      }
    });
  },

  // 获取商品信息
  getGoodsInfo: function() {
    wx.showLoading({
      title: '加载中',
    });

    setTimeout(function() {
      wx.hideLoading()
    }, 2000);

    let that = this;
    util.request(api.GoodsDetail, {
      id: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {

        let _specificationList = res.data.specificationList
        // 如果仅仅存在一种货品，那么商品页面初始化时默认checked
        if (_specificationList.length == 1) {
          if (_specificationList[0].valueList.length == 1) {
            _specificationList[0].valueList[0].checked = true

            // 如果仅仅存在一种货品，那么商品价格应该和货品价格一致
            // 这里检测一下
            let _productPrice = res.data.productList[0].price;
            let _goodsPrice = res.data.info.retailPrice;
            if (_productPrice != _goodsPrice) {
              console.error('商品数量价格和货品不一致');
            }

            that.setData({
              checkedSpecText: _specificationList[0].valueList[0].value,
              tmpSpecText: '已选择：' + _specificationList[0].valueList[0].value,
            });
          }
        }

        that.setData({
          goods: res.data.info,
          attribute: res.data.attribute,
          issueList: res.data.issue,
          comment: res.data.comment,
          brand: res.data.brand,
          specificationList: res.data.specificationList,
          productList: res.data.productList,
          userHasCollect: res.data.userHasCollect,
          shareImage: res.data.shareImage,
          checkedSpecPrice: res.data.info.retailPrice,
          groupon: res.data.groupon
        });

        //如果是通过分享的团购参加团购，则团购项目应该与分享的一致并且不可更改
        if (that.data.isGroupon) {
          let groupons = that.data.groupon;
          for (var i = 0; i < groupons.length; i++) {
            if (groupons[i].id != that.data.grouponLink.rulesId) {
              groupons.splice(i, 1);
            }
          }
          groupons[0].checked = true;
          //重设团购规格
          that.setData({
            groupon: groupons
          });

        }

        if (res.data.userHasCollect == 1) {
          that.setData({
            collectImage: that.data.hasCollectImage
          });
        } else {
          that.setData({
            collectImage: that.data.noCollectImage
          });
        }

        WxParse.wxParse('goodsDetail', 'html', res.data.info.detail, that);
        //获取推荐商品
        that.getGoodsRelated();
        wx.hideLoading();
      }
    });
  },

  // 获取推荐商品
  getGoodsRelated: function() {
    let that = this;
    util.request(api.GoodsRelated, {
      id: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          relatedGoods: res.data.goodsList,
        });
      }
    });
  },

  // 团购选择
  clickGroupon: function(event) {
    let that = this;

    //参与团购，不可更改选择
    if (that.data.isGroupon) {
      return;
    }

    let specName = event.currentTarget.dataset.name;
    let specValueId = event.currentTarget.dataset.valueId;

    let _grouponList = this.data.groupon;
    for (let i = 0; i < _grouponList.length; i++) {
      if (_grouponList[i].id == specValueId) {
        if (_grouponList[i].checked) {
          _grouponList[i].checked = false;
        } else {
          _grouponList[i].checked = true;
        }
      } else {
        _grouponList[i].checked = false;
      }
    }

    this.setData({
      groupon: _grouponList,
    });
  },

  // 规格选择
  clickSkuValue: function(event) {
    let that = this;
    let specName = event.currentTarget.dataset.name;
    let specValueId = event.currentTarget.dataset.valueId;

    //判断是否可以点击

    //TODO 性能优化，可在wx:for中添加index，可以直接获取点击的属性名和属性值，不用循环
    let _specificationList = this.data.specificationList;
    for (let i = 0; i < _specificationList.length; i++) {
      if (_specificationList[i].name === specName) {
        for (let j = 0; j < _specificationList[i].valueList.length; j++) {
          if (_specificationList[i].valueList[j].id == specValueId) {
            //如果已经选中，则反选
            if (_specificationList[i].valueList[j].checked) {
              _specificationList[i].valueList[j].checked = false;
            } else {
              _specificationList[i].valueList[j].checked = true;
            }
          } else {
            _specificationList[i].valueList[j].checked = false;
          }
        }
      }
    }
    this.setData({
      specificationList: _specificationList,
    });
    //重新计算spec改变后的信息
    this.changeSpecInfo();

    //重新计算哪些值不可以点击
  },

  //获取选中的团购信息
  getCheckedGrouponValue: function() {
    let checkedValues = {};
    let _grouponList = this.data.groupon;
    for (let i = 0; i < _grouponList.length; i++) {
      if (_grouponList[i].checked) {
        checkedValues = _grouponList[i];
      }
    }

    return checkedValues;
  },

  //获取选中的规格信息
  getCheckedSpecValue: function() {
    let checkedValues = [];
    let _specificationList = this.data.specificationList;
    for (let i = 0; i < _specificationList.length; i++) {
      let _checkedObj = {
        name: _specificationList[i].name,
        valueId: 0,
        valueText: ''
      };
      for (let j = 0; j < _specificationList[i].valueList.length; j++) {
        if (_specificationList[i].valueList[j].checked) {
          _checkedObj.valueId = _specificationList[i].valueList[j].id;
          _checkedObj.valueText = _specificationList[i].valueList[j].value;
        }
      }
      checkedValues.push(_checkedObj);
    }

    return checkedValues;
  },

  //判断规格是否选择完整
  isCheckedAllSpec: function() {
    return !this.getCheckedSpecValue().some(function(v) {
      if (v.valueId == 0) {
        return true;
      }
    });
  },

  soldoutNotify: function() {
    Notify("商品已售完");
  },

  getCheckedSpecKey: function() {
    let checkedValue = this.getCheckedSpecValue().map(function(v) {
      return v.valueText;
    });
    return checkedValue;
  },

  // 规格改变时，重新计算价格及显示信息
  changeSpecInfo: function() {
    let checkedNameValue = this.getCheckedSpecValue();

    //设置选择的信息
    let checkedValue = checkedNameValue.filter(function(v) {
      if (v.valueId != 0) {
        return true;
      } else {
        return false;
      }
    }).map(function(v) {
      return v.valueText;
    });
    if (checkedValue.length > 0) {
      this.setData({
        tmpSpecText: checkedValue.join('　')
      });
    } else {
      this.setData({
        tmpSpecText: '请选择规格数量'
      });
    }

    if (this.isCheckedAllSpec()) {
      this.setData({
        checkedSpecText: this.data.tmpSpecText
      });

      // 规格所对应的货品选择以后
      let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
      if (!checkedProductArray || checkedProductArray.length <= 0) {
        this.setData({
          soldout: true
        });
        this.soldoutNotify();
        console.error('规格所对应货品不存在');
        return;
      }

      let checkedProduct = checkedProductArray[0];
      if (checkedProduct.number > 0) {
        this.setData({
          checkedSpecPrice: checkedProduct.price,
          soldout: false
        });
        this.notify.hide();
      } else {
        this.setData({
          checkedSpecPrice: this.data.goods.retailPrice,
          soldout: true
        });
        this.soldoutNotify();
      }

    } else {
      this.setData({
        checkedSpecText: '规格数量选择',
        checkedSpecPrice: this.data.goods.retailPrice,
        soldout: false
      });
      this.notify.hide();
    }

  },

  // 获取选中的产品（根据规格）
  getCheckedProductItem: function(key) {
    return this.data.productList.filter(function(v) {
      if (v.specifications.toString() == key.toString()) {
        return true;
      } else {
        return false;
      }
    });
  },

  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    if (options.id) {
      this.setData({
        id: parseInt(options.id)
      });
      this.getGoodsInfo();
    }

    if (options.grouponId) {
      this.setData({
        isGroupon: true,
      });
      this.getGrouponInfo(options.grouponId);
    }
    let that = this;
    wx.getSetting({
        success: function (res) {
            console.log(res)
            //不存在相册授权
            if (!res.authSetting['scope.writePhotosAlbum']) {
                wx.authorize({
                    scope: 'scope.writePhotosAlbum',
                    success: function () {
                        that.setData({
                            canWrite: true
                        })
                    },
                    fail: function (err) {
                        that.setData({
                            canWrite: false
                        })
                    }
                })
            } else {
                that.setData({
                    canWrite: true
                });
            }
        }
    })

  },

  onShow: function() {
    // 页面显示
    var that = this;
    util.request(api.CartGoodsCount).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          cartGoodsCount: res.data
        });
      }
    });
  },

  //添加或是取消收藏
  addCollectOrNot: function() {
    let that = this;
    util.request(api.CollectAddOrDelete, {
        type: 0,
        valueId: this.data.id
      }, "POST")
      .then(function(res) {
        let _res = res;
        if (_res.errno == 0) {
          if (_res.data.type == 'add') {
            that.setData({
              collectImage: that.data.hasCollectImage
            });
          } else {
            that.setData({
              collectImage: that.data.noCollectImage
            });
          }

        } else {
          wx.showToast({
            image: '/static/images/icon_error.png',
            title: _res.errmsg,
            mask: true
          });
        }

      });

  },

  //立即购买（先自动加入购物车）
  addFast: function() {
    var that = this;
    if (this.data.openAttr == false) {
      //打开规格选择窗口
      this.setData({
        openAttr: !this.data.openAttr
      });
    } else {

      //提示选择完整规格
      if (!this.isCheckedAllSpec()) {
        wx.showToast({
          image: '/static/images/icon_error.png',
          title: '请选择完整规格'
        });
        return false;
      }

      //根据选中的规格，判断是否有对应的sku信息
      let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
      if (!checkedProductArray || checkedProductArray.length <= 0) {
        //找不到对应的product信息，提示没有库存
        wx.showToast({
          image: '/static/images/icon_error.png',
          title: '没有库存'
        });
        return false;
      }

      let checkedProduct = checkedProductArray[0];
      //验证库存
      if (checkedProduct.number <= 0) {
        wx.showToast({
          image: '/static/images/icon_error.png',
          title: '没有库存'
        });
        return false;
      }

      //验证团购是否有效
      let checkedGroupon = this.getCheckedGrouponValue();

      //立即购买
      util.request(api.CartFastAdd, {
          goodsId: this.data.goods.id,
          number: this.data.number,
          productId: checkedProduct.id
        }, "POST")
        .then(function(res) {
          if (res.errno == 0) {
            // 如果storage中设置了cartId，则是立即购买，否则是购物车购买
            try {
              wx.setStorageSync('cartId', res.data);
              wx.setStorageSync('grouponRulesId', checkedGroupon.id);
              wx.setStorageSync('grouponLinkId', that.data.grouponLink.id);
              wx.navigateTo({
                url: '/pages/shopping/checkout/checkout'
              })
            } catch (e) {}

          } else {
            wx.showToast({
              image: '/static/images/icon_error.png',
              title: res.errmsg,
              mask: true
            });
          }
        });
    }


  },

  //添加到购物车
  addToCart: function() {
    var that = this;
    if (this.data.openAttr == false) {
      //打开规格选择窗口
      this.setData({
        openAttr: !this.data.openAttr
      });
    } else {

      //提示选择完整规格
      if (!this.isCheckedAllSpec()) {
        wx.showToast({
          image: '/static/images/icon_error.png',
          title: '请选择完整规格'
        });
        return false;
      }

      //根据选中的规格，判断是否有对应的sku信息
      let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
      if (!checkedProductArray || checkedProductArray.length <= 0) {
        //找不到对应的product信息，提示没有库存
        wx.showToast({
          image: '/static/images/icon_error.png',
          title: '没有库存'
        });
        return false;
      }

      let checkedProduct = checkedProductArray[0];
      //验证库存
      if (checkedProduct.number <= 0) {
        wx.showToast({
          image: '/static/images/icon_error.png',
          title: '没有库存'
        });
        return false;
      }

      //添加到购物车
      util.request(api.CartAdd, {
          goodsId: this.data.goods.id,
          number: this.data.number,
          productId: checkedProduct.id
        }, "POST")
        .then(function(res) {
          let _res = res;
          if (_res.errno == 0) {
            wx.showToast({
              title: '添加成功'
            });
            that.setData({
              openAttr: !that.data.openAttr,
              cartGoodsCount: _res.data
            });
            if (that.data.userHasCollect == 1) {
              that.setData({
                collectImage: that.data.hasCollectImage
              });
            } else {
              that.setData({
                collectImage: that.data.noCollectImage
              });
            }
          } else {
            wx.showToast({
              image: '/static/images/icon_error.png',
              title: _res.errmsg,
              mask: true
            });
          }

        });
    }

  },

  cutNumber: function() {
    this.setData({
      number: (this.data.number - 1 > 1) ? this.data.number - 1 : 1
    });
  },
  addNumber: function() {
    this.setData({
      number: this.data.number + 1
    });
  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭

  },
  switchAttrPop: function() {
    if (this.data.openAttr == false) {
      this.setData({
        openAttr: !this.data.openAttr
      });
    }
  },

  closeAttr: function() {
    this.setData({
      openAttr: false,
    });
  },
  openCartPage: function() {
    wx.switchTab({
      url: '/pages/cart/cart'
    });
  },
  onReady: function() {
    // 页面渲染完成
    this.sharePop = this.selectComponent("#sharePop");
    this.notify = this.selectComponent("#van-notify");
  }
})