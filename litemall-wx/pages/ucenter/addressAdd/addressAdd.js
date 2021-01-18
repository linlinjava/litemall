var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');
var check = require('../../../utils/check.js');
var area = require('../../../utils/area.js');

var app = getApp();
Page({
  data: {
    address: {
      id: 0,
      areaCode: 0,
      address: '',
      name: '',
      tel: '',
      isDefault: 0,
      province: '',
      city: '',
      county: ''
    },
    addressId: 0,
    openSelectRegion: false,
    selectRegionList: [{
        code: 0,
        name: '省份'
      },
      {
        code: 0,
        name: '城市'
      },
      {
        code: 0,
        name: '区县'
      }
    ],
    regionType: 1,
    regionList: [],
    selectRegionDone: false
  },
  bindinputMobile(event) {
    let address = this.data.address;
    address.tel = event.detail.value;
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
    address.addressDetail = event.detail.value;
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
  setRegionDoneStatus() {
    let that = this;
    let doneStatus = that.data.selectRegionList.every(item => {
      return item.code != 0;
    });

    that.setData({
      selectRegionDone: doneStatus
    })

  },
  chooseRegion() {
    let that = this;
    this.setData({
      openSelectRegion: !this.data.openSelectRegion
    });

    //设置区域选择数据
    let address = this.data.address;
    if (address.areaCode > 0) {
      let selectRegionList = this.data.selectRegionList;
      selectRegionList[0].code = address.areaCode.slice(0, 2) + '0000';
      selectRegionList[0].name = address.province;

      selectRegionList[1].code = address.areaCode.slice(0, 4) + '00';
      selectRegionList[1].name = address.city;

      selectRegionList[2].code = address.areaCode;
      selectRegionList[2].name = address.county;

      let regionList = area.getList('county', address.areaCode.slice(0, 4));
      regionList = regionList.map(item => {
        //标记已选择的
        if (address.areaCode === item.code) {
          item.selected = true;
        } else {
          item.selected = false;
        }
        return item;
      })

      this.setData({
        selectRegionList: selectRegionList,
        regionType: 3,
        regionList: regionList
      });

    } else {
      let selectRegionList = [{
            code: 0,
            name: '省份',
          },
          {
            code: 0,
            name: '城市',
          },
          {
            code: 0,
            name: '区县',
          }
        ];

      this.setData({
        selectRegionList: selectRegionList,
        regionType: 1,
        regionList: area.getList('province')
      });
    }

    this.setRegionDoneStatus();

  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    console.log(options)
    if (options.id && options.id != 0) {
      this.setData({
        addressId: options.id
      });
      this.getAddressDetail();
    }
  },
  onReady: function() {

  },
  selectRegionType(event) {
    let that = this;
    let regionTypeIndex = event.target.dataset.regionTypeIndex;
    let selectRegionList = that.data.selectRegionList;

    //判断是否可点击
    if (regionTypeIndex + 1 == this.data.regionType || (regionTypeIndex - 1 >= 0 && selectRegionList[regionTypeIndex - 1].code <= 0)) {
      return false;
    }

    let selectRegionItem = selectRegionList[regionTypeIndex];
    let code = selectRegionItem.code;
    let regionList;
    if (regionTypeIndex === 0) {
      // 点击省级，取省级
      regionList = area.getList('province');
    }
    else if (regionTypeIndex === 1) {
      // 点击市级，取市级
      regionList = area.getList('city', code.slice(0, 2)); 
    }
    else{
      // 点击县级，取县级
      regionList = area.getList('county', code.slice(0, 4)); 
    }

    regionList = regionList.map(item => {
      //标记已选择的
      if (that.data.selectRegionList[regionTypeIndex].code == item.code) {
        item.selected = true;
      } else {
        item.selected = false;
      }
      return item;
    })

    this.setData({
      regionList: regionList,
      regionType: regionTypeIndex + 1
    })

    this.setRegionDoneStatus();
  },
  selectRegion(event) {
    let that = this;
    let regionIndex = event.target.dataset.regionIndex;
    let regionItem = this.data.regionList[regionIndex];
    let regionType = this.data.regionType;
    let selectRegionList = this.data.selectRegionList;
    selectRegionList[regionType - 1] = regionItem;

    if (regionType == 3) {
      this.setData({
        selectRegionList: selectRegionList
      })

      let regionList = that.data.regionList.map(item => {
        //标记已选择的
        if (that.data.selectRegionList[that.data.regionType - 1].code == item.code) {
          item.selected = true;
        } else {
          item.selected = false;
        }
        return item;
      })

      this.setData({
        regionList: regionList
      })

      this.setRegionDoneStatus();
      return
    }

    //重置下级区域为空
    selectRegionList.map((item, index) => {
      if (index > regionType - 1) {
        item.code = 0;
        item.name = index == 1 ? '城市' : '区县';
      }
      return item;
    });

    this.setData({
      selectRegionList: selectRegionList,
      regionType: regionType + 1
    })
    
    let code = regionItem.code;
    let regionList = [];
    if (regionType === 1) {
      // 点击省级，取市级
      regionList= area.getList('city', code.slice(0, 2))
    }
    else {
      // 点击市级，取县级
      regionList= area.getList('county', code.slice(0, 4))
    }

    this.setData({
      regionList: regionList
    })

    this.setRegionDoneStatus();
  },
  doneSelectRegion() {
    if (this.data.selectRegionDone === false) {
      return false;
    }

    let address = this.data.address;
    let selectRegionList = this.data.selectRegionList;
    address.province = selectRegionList[0].name;
    address.city = selectRegionList[1].name;
    address.county = selectRegionList[2].name;
    address.areaCode = selectRegionList[2].code;

    this.setData({
      address: address,
      openSelectRegion: false
    });

  },
  cancelSelectRegion() {
    this.setData({
      openSelectRegion: false,
      regionType: this.data.regionDoneStatus ? 3 : 1
    });

  },
  cancelAddress() {
    wx.navigateBack();
  },
  saveAddress() {
    console.log(this.data.address)
    let address = this.data.address;

    if (address.name == '') {
      util.showErrorToast('请输入姓名');

      return false;
    }

    if (address.tel == '') {
      util.showErrorToast('请输入手机号码');
      return false;
    }


    if (address.areaCode == 0) {
      util.showErrorToast('请输入省市区');
      return false;
    }

    if (address.addressDetail == '') {
      util.showErrorToast('请输入详细地址');
      return false;
    }

    let that = this;
    util.request(api.AddressSave, {
      id: address.id,
      name: address.name,
      tel: address.tel,
      province: address.province,
      city: address.city,
      county: address.county,
      areaCode: address.areaCode,
      addressDetail: address.addressDetail,
      isDefault: address.isDefault
    }, 'POST').then(function(res) {
      if (res.errno === 0) {
        //返回之前，先取出上一页对象，并设置addressId
        var pages = getCurrentPages();
        var prevPage = pages[pages.length - 2];
        console.log(prevPage);
        if (prevPage.route == "pages/checkout/checkout") {
          prevPage.setData({
            addressId: res.data
          })

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
})