/**
 * 微信小程序JavaScriptSDK
 * 
 * @version 1.0
 * @date 2017-01-10
 * @author jaysonzhou@tencent.com
 */

var ERROR_CONF = {
  KEY_ERR: 311,
  KEY_ERR_MSG: 'key格式错误',
  PARAM_ERR: 310,
  PARAM_ERR_MSG: '请求参数信息有误',
  SYSTEM_ERR: 600,
  SYSTEM_ERR_MSG: '系统错误',
  WX_ERR_CODE: 1000,
  WX_OK_CODE: 200
};
var BASE_URL = 'https://apis.map.qq.com/ws/';
var URL_SEARCH = BASE_URL + 'place/v1/search';
var URL_SUGGESTION = BASE_URL + 'place/v1/suggestion';
var URL_GET_GEOCODER = BASE_URL + 'geocoder/v1/';
var URL_CITY_LIST = BASE_URL + 'district/v1/list';
var URL_AREA_LIST = BASE_URL + 'district/v1/getchildren';
var URL_DISTANCE = BASE_URL + 'distance/v1/';
var Utils = {
  /**
   * 得到终点query字符串
   * @param {Array|String} 检索数据
   */
  location2query(data) {
    if (typeof data == 'string') {
      return data;
    }
    var query = '';
    for (var i = 0; i < data.length; i++) {
      var d = data[i];
      if (!!query) {
        query += ';';
      }
      if (d.location) {
        query = query + d.location.lat + ',' + d.location.lng;
      }
      if (d.latitude && d.longitude) {
        query = query + d.latitude + ',' + d.longitude;
      }
    }
    return query;
  },

  /**
   * 使用微信接口进行定位
   */
  getWXLocation(success, fail, complete) {
    wx.getLocation({
      type: 'gcj02',
      success: success,
      fail: fail,
      complete: complete
    });
  },

  /**
   * 获取location参数
   */
  getLocationParam(location) {
    if (typeof location == 'string') {
      var locationArr = location.split(',');
      if (locationArr.length === 2) {
        location = {
          latitude: location.split(',')[0],
          longitude: location.split(',')[1]
        };
      } else {
        location = {};
      }
    }
    return location;
  },

  /**
   * 回调函数默认处理
   */
  polyfillParam(param) {
    param.success = param.success || function () { };
    param.fail = param.fail || function () { };
    param.complete = param.complete || function () { };
  },

  /**
   * 验证param对应的key值是否为空
   * 
   * @param {Object} param 接口参数
   * @param {String} key 对应参数的key
   */
  checkParamKeyEmpty(param, key) {
    if (!param[key]) {
      var errconf = this.buildErrorConfig(ERROR_CONF.PARAM_ERR, ERROR_CONF.PARAM_ERR_MSG + key + '参数格式有误');
      param.fail(errconf);
      param.complete(errconf);
      return true;
    }
    return false;
  },

  /**
   * 验证参数中是否存在检索词keyword
   * 
   * @param {Object} param 接口参数
   */
  checkKeyword(param) {
    return !this.checkParamKeyEmpty(param, 'keyword');
  },

  /**
   * 验证location值
   * 
   * @param {Object} param 接口参数
   */
  checkLocation(param) {
    var location = this.getLocationParam(param.location);
    if (!location || !location.latitude || !location.longitude) {
      var errconf = this.buildErrorConfig(ERROR_CONF.PARAM_ERR, ERROR_CONF.PARAM_ERR_MSG + ' location参数格式有误')
      param.fail(errconf);
      param.complete(errconf);
      return false;
    }
    return true;
  },

  /**
   * 构造错误数据结构
   * @param {Number} errCode 错误码
   * @param {Number} errMsg 错误描述
   */
  buildErrorConfig(errCode, errMsg) {
    return {
      status: errCode,
      message: errMsg
    };
  },

  /**
   * 构造微信请求参数，公共属性处理
   * 
   * @param {Object} param 接口参数
   * @param {Object} param 配置项
   */
  buildWxRequestConfig(param, options) {
    var that = this;
    options.header = { "content-type": "application/json" };
    options.method = 'GET';
    options.success = function (res) {
      var data = res.data;
      if (data.status === 0) {
        param.success(data);
      } else {
        param.fail(data);
      }
    };
    options.fail = function (res) {
      res.statusCode = ERROR_CONF.WX_ERR_CODE;
      param.fail(that.buildErrorConfig(ERROR_CONF.WX_ERR_CODE, result.errMsg));
    };
    options.complete = function (res) {
      var statusCode = +res.statusCode;
      switch (statusCode) {
        case ERROR_CONF.WX_ERR_CODE: {
          param.complete(that.buildErrorConfig(ERROR_CONF.WX_ERR_CODE, res.errMsg));
          break;
        }
        case ERROR_CONF.WX_OK_CODE: {
          var data = res.data;
          if (data.status === 0) {
            param.complete(data);
          } else {
            param.complete(that.buildErrorConfig(data.status, data.message));
          }
          break;
        }
        default: {
          param.complete(that.buildErrorConfig(ERROR_CONF.SYSTEM_ERR, ERROR_CONF.SYSTEM_ERR_MSG));
        }

      }
    }
    return options;
  },

  /**
   * 处理用户参数是否传入坐标进行不同的处理
   */
  locationProcess(param, locationsuccess, locationfail, locationcomplete) {
    var that = this;
    locationfail = locationfail || function (res) {
      res.statusCode = ERROR_CONF.WX_ERR_CODE;
      param.fail(that.buildErrorConfig(ERROR_CONF.WX_ERR_CODE, res.errMsg));
    };
    locationcomplete = locationcomplete || function (res) {
      if (res.statusCode == ERROR_CONF.WX_ERR_CODE) {
        param.complete(that.buildErrorConfig(ERROR_CONF.WX_ERR_CODE, res.errMsg));
      }
    };
    if (!param.location) {
      that.getWXLocation(locationsuccess, locationfail, locationcomplete);
    } else if (that.checkLocation(param)) {
      var location = Utils.getLocationParam(param.location);
      locationsuccess(location);
    }
  }
}


class QQMapWX {

  /**
   * 构造函数
   * 
   * @param {Object} options 接口参数,key 为必选参数
   */
  constructor(options) {
    if (!options.key) {
      throw Error('key值不能为空');
    }
    this.key = options.key;
  }

  /**
   * POI周边检索
   *
   * @param {Object} options 接口参数对象
   * 
   * 参数对象结构可以参考
   * @see http://lbs.qq.com/webservice_v1/guide-search.html
   */
  search(options) {
    var that = this;
    options = options || {};

    Utils.polyfillParam(options);

    if (!Utils.checkKeyword(options)) {
      return;
    }

    var requestParam = {
      keyword: options.keyword,
      orderby: options.orderby || '_distance',
      page_size: options.page_size || 10,
      page_index: options.page_index || 1,
      output: 'json',
      key: that.key
    };

    if (options.address_format) {
      requestParam.address_format = options.address_format;
    }

    if (options.filter) {
      requestParam.filter = options.filter;
    }

    var distance = options.distance || "1000";
    var auto_extend = options.auto_extend || 1;

    var locationsuccess = function (result) {
      requestParam.boundary = "nearby(" + result.latitude + "," + result.longitude + "," + distance + "," + auto_extend + ")";
      wx.request(Utils.buildWxRequestConfig(options, {
        url: URL_SEARCH,
        data: requestParam
      }));
    }
    Utils.locationProcess(options, locationsuccess);
  }

  /**
   * sug模糊检索
   *
   * @param {Object} options 接口参数对象
   * 
   * 参数对象结构可以参考
   * http://lbs.qq.com/webservice_v1/guide-suggestion.html
   */
  getSuggestion(options) {
    var that = this;
    options = options || {};
    Utils.polyfillParam(options);

    if (!Utils.checkKeyword(options)) {
      return;
    }

    var requestParam = {
      keyword: options.keyword,
      region: options.region || '全国',
      region_fix: options.region_fix || 0,
      policy: options.policy || 0,
      output: 'json',
      key: that.key
    };
    wx.request(Utils.buildWxRequestConfig(options, {
      url: URL_SUGGESTION,
      data: requestParam
    }));
  }

  /**
   * 逆地址解析
   *
   * @param {Object} options 接口参数对象
   * 
   * 请求参数结构可以参考
   * http://lbs.qq.com/webservice_v1/guide-gcoder.html
   */
  reverseGeocoder(options) {
    var that = this;
    options = options || {};
    Utils.polyfillParam(options);
    var requestParam = {
      coord_type: options.coord_type || 5,
      get_poi: options.get_poi || 0,
      output: 'json',
      key: that.key
    };
    if (options.poi_options) {
      requestParam.poi_options = options.poi_options
    }

    var locationsuccess = function (result) {
      requestParam.location = result.latitude + ',' + result.longitude;
      wx.request(Utils.buildWxRequestConfig(options, {
        url: URL_GET_GEOCODER,
        data: requestParam
      }));
    };
    Utils.locationProcess(options, locationsuccess);
  }

  /**
   * 地址解析
   *
   * @param {Object} options 接口参数对象
   * 
   * 请求参数结构可以参考
   * http://lbs.qq.com/webservice_v1/guide-geocoder.html
   */
  geocoder(options) {
    var that = this;
    options = options || {};
    Utils.polyfillParam(options);

    if (Utils.checkParamKeyEmpty(options, 'address')) {
      return;
    }

    var requestParam = {
      address: options.address,
      output: 'json',
      key: that.key
    };

    wx.request(Utils.buildWxRequestConfig(options, {
      url: URL_GET_GEOCODER,
      data: requestParam
    }));
  }


  /**
   * 获取城市列表
   *
   * @param {Object} options 接口参数对象
   * 
   * 请求参数结构可以参考
   * http://lbs.qq.com/webservice_v1/guide-region.html
   */
  getCityList(options) {
    var that = this;
    options = options || {};
    Utils.polyfillParam(options);
    var requestParam = {
      output: 'json',
      key: that.key
    };

    wx.request(Utils.buildWxRequestConfig(options, {
      url: URL_CITY_LIST,
      data: requestParam
    }));
  }

  /**
   * 获取对应城市ID的区县列表
   *
   * @param {Object} options 接口参数对象
   * 
   * 请求参数结构可以参考
   * http://lbs.qq.com/webservice_v1/guide-region.html
   */
  getDistrictByCityId(options) {
    var that = this;
    options = options || {};
    Utils.polyfillParam(options);

    if (Utils.checkParamKeyEmpty(options, 'id')) {
      return;
    }

    var requestParam = {
      id: options.id || '',
      output: 'json',
      key: that.key
    };

    wx.request(Utils.buildWxRequestConfig(options, {
      url: URL_AREA_LIST,
      data: requestParam
    }));
  }

  /**
   * 用于单起点到多终点的路线距离(非直线距离)计算：
   * 支持两种距离计算方式：步行和驾车。
   * 起点到终点最大限制直线距离10公里。
   *
   * @param {Object} options 接口参数对象
   * 
   * 请求参数结构可以参考
   * http://lbs.qq.com/webservice_v1/guide-distance.html
   */
  calculateDistance(options) {
    var that = this;
    options = options || {};
    Utils.polyfillParam(options);

    if (Utils.checkParamKeyEmpty(options, 'to')) {
      return;
    }

    var requestParam = {
      mode: options.mode || 'walking',
      to: Utils.location2query(options.to),
      output: 'json',
      key: that.key
    };

    var locationsuccess = function (result) {
      requestParam.from = result.latitude + ',' + result.longitude;
      wx.request(Utils.buildWxRequestConfig(options, {
        url: URL_DISTANCE,
        data: requestParam
      }));
    }
    if (options.from) {
      options.location = options.from;
    }

    Utils.locationProcess(options, locationsuccess);
  }
}

module.exports = QQMapWX;