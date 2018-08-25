var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var check = require('../../../utils/check.js');

var app = getApp();
Page({
 data: {
  address: {
   id: 0,
   provinceId: 0,
   cityId: 0,
   areaId: 0,
   address: '',
   name: '',
   mobile: '',
   isDefault: 0,
   provinceName: '',
   cityName: '',
   areaName: ''
  },
  addressId: 0,
  openSelectRegion: false,
  regionType: 1,
  selectRegionDone: false
 },
 bindinputMobile(event) {
  let address = this.data.address;
  address.mobile = event.detail.value;
  this.setData({
   address: address
  });
 },
 bindinputName(event) {
  let address = this.data.address;
  address.name = event.detail.value;
  this.setData({
   address: address
  });
 },
 bindinputAddress(event) {
  let address = this.data.address;
  address.address = event.detail.value;
  this.setData({
   address: address
  });
 },
 bindIsDefault() {
  let address = this.data.address;
  address.isDefault = !address.isDefault;
  this.setData({
   address: address
  });
 },
 getAddressDetail() {
  let that = this;
  util.request(api.AddressDetail, {
   id: that.data.addressId
  }).then(function(res) {
   if (res.errno === 0) {
    if (res.data) {
     that.setData({
      address: res.data
     });
    }
   }
  });
 },

 wxChooseAddress() {
  let that = this;
  let address = this.data.address;
  // 用户已经同意小程序使用地址功能
  wx.chooseAddress({
   success: function(res) {
    address.provinceId = 99999;
    address.cityId = 88888;
    address.areaId = 77777;
    address.name = res.userName;
    address.mobile = res.telNumber;
    address.provinceName = res.provinceName;
    address.cityName = res.cityName;
    address.areaName = res.countyName;
    address.address = res.provinceName + res.cityName + res.countyName + res.detailInfo;

    that.setData({
     address: address,
    });
   }
  });
 },

 wxAddress() {
  let that = this;
  // 可以通过 wx.getSetting 先查询一下用户是否授权了 "scope.address" 这个 scope
  wx.getSetting({
   success(res) {
    if (!res.authSetting['scope.address']) {
     wx.authorize({
      scope: 'scope.address',
      success() {
       that.wxChooseAddress();
      }
     })
    } else {
     that.wxChooseAddress();
    }
   }
  })
 },

 onLoad: function(options) {
  let that = this;
  // 页面初始化 options为页面跳转所带来的参数
  console.log(options);
  if (options.id && options.id != 0) {
   this.setData({
    addressId: options.id
   });
   this.getAddressDetail();
  } else {
   that.wxAddress();
  }
 },


 onReady: function() {

 },

 cancelAddress() {
  wx.navigateBack();
 },
 saveAddress() {
  console.log(this.data.address);
  let address = this.data.address;

  if (address.name == '') {
   util.showErrorToast('请输入姓名');

   return false;
  }

  if (address.mobile == '') {
   util.showErrorToast('请输入手机号码');
   return false;
  }


  if (address.areaId == 0) {
   util.showErrorToast('请输入省市区');
   return false;
  }

  if (address.address == '') {
   util.showErrorToast('请输入详细地址');
   return false;
  }

  if (!check.isValidPhone(address.mobile)) {
   util.showErrorToast('手机号不正确');
   return false;
  }

  let that = this;
  util.request(api.AddressSave, {
   id: address.id,
   name: address.name,
   mobile: address.mobile,
   provinceId: address.provinceId,
   cityId: address.cityId,
   areaId: address.areaId,
   address: address.address,
   isDefault: address.isDefault,
   provinceName: address.provinceName,
   cityName: address.cityName,
   countyName: address.areaName
  }, 'POST').then(function(res) {
   if (res.errno === 0) {
    //返回之前，先取出上一页对象，并设置addressId
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2];
    console.log(prevPage);
    if (prevPage.route == "pages/shopping/checkout/checkout") {
     prevPage.setData({
      addressId: res.data
     });

     try {
      wx.setStorageSync('addressId', res.data);
     } catch (e) {

     }
     console.log("set address");
    }
    wx.navigateBack();
   }
  });

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