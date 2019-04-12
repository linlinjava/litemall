
const protobuf = require("protobufjs");

/*尽量保持业务无关 业务相关放chatinfo.js中*/
if (!window.PSMU) {

  var PSMU = {

    validChars: /^[\],:{}\s]*$/,
    validEscape: /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,
    validTokens: /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
    validBraces: /(?:^|:|,)(?:\s*\[)+/g,
    htmlRegArr: [],


    SERV_LOG_INTERVAL: 30 * 1000,
    servLogId: false,
    servLogMap: {},
    tokenName: 'Authorization',
    tuid: 'false',
    tuidCounter: 0,
    isUptuid: false,
    uptuidDefaultVal: 9960,
    maxtuidval: 9999,
    // Base64 encoding table
    b64: new Array(64),

    // Base64 decoding table
    s64: new Array(123),

    /*目前全局变量及状态保存到 psm中 组件数据通信频繁的可以放vuex中
     * 尽量不依赖vue 增加额外复杂性 */
    REV_MSG: 'REV_MSG',
    /*接收消息*/
    REV_ROOM_MSG: 'REV_ROOM_MSG',
    /*接收房态消息*/
    REV_HIS_MSG: 'REV_HIS_MSG',
    /*接收历史消息*/
    LOAD_HIS_MSG_END: 'LOAD_HIS_MSG_END',
    /*加载历史消息结束*/
    SELF_MSG: 'SELF_MSG',
    /*发出消息*/
    RESEND_MSG: 'RESEND_MSG',
    /*重发消息*/
    CHAT_NET_ERR_MSG: 'CHAT_NET_ERR_MSG',
    /*网络异常消息发送失败*/
    OUT_CHAT_PAGE_MSG: 'OUT_CHAT_PAGE_MSG',
    /*离开聊天页面*/
    IN_CHAT_PAGE_MSG: 'IN_CHAT_PAGE_MSG',
    /*进入聊天页面*/
    LOGIN_SUCC: 'LOGIN_SUCC',
    /*登录成功*/
    LOGIN_FAILED: 'LOGIN_FAILED',
    /*登录失败*/

    CODE_ZU: 'ZU',
    CODE_ZP: 'ZP',

    codeType: false,

    setup: function () {

      PSMU.codeType = PSMU.CODE_ZP;

      // 65..90, 97..122, 48..57, 43, 47
      for (var i = 0; i < 64;)
        PSMU.s64[PSMU.b64[i] = i < 26 ? i + 65 : i < 52 ? i + 71 : i < 62 ? i - 4 : i - 59 | 43] = i++;

    },


    isEmt: function (value, allowBlank) {
      if (PSMU.isNotDef(value)) {
        return true;
      }
      return value === null || value === undefined || /*((PSMU.isArr(value) && !value.length)) ||*/ (!allowBlank ? value === '' : false) || value === "undefined";
    },
    isEmtVal: function (value) {
      if (PSMU.isNotDef(value)) {
        return true;
      }
      return value === null || value === 'null' || (typeof (value) === 'string' && (value === "undefined" || value === '' || PSMU.trim(value) === '')) || (typeof (value) === 'boolean' && value === false);
    },
    isNotDef: function (value) {
      return typeof (value) === "undefined";
    },
    isArr: function (obj) {
      return Object.prototype.toString.call(obj) === '[object Array]';
    },
    isString: function (obj) {
      return Object.prototype.toString.call(obj) === '[object String]';
    },
    isDate: function (obj) {
      return Object.prototype.toString.call(obj) === '[object Date]';
    },
    isNum: function (num) {
      return (typeof (num) === "number") && (num !== Infinity) && !isNaN(num);
    },
    isNotEmtArr: function (o) {
      return PSMU.isArr(o) && typeof (o.length) && o.length > 0;
    },
    trim: function (value) {
      return value.replace(/^\s*/, "").replace(/\s*$/, "");
    },

    setBuffer: function (ret, val) {
      if (PSMU.isEmt(ret[ret.len])) {
        ret[ret.len] = protobuf.util.newBuffer(100);
      }

      if (ret.offset + 1 >= ret.len * 100 + 100) {
        ret[++ret.len] = protobuf.util.newBuffer(100);
      }
      var len = parseInt(ret.offset / 100);
      var pos = ret.offset % 100;

      ret[len][pos] = val;
      return ++ret.offset;
    },
    base64Decode: function (string, /*buffer,*/ offset) {
      var ret = {
        len: 0,
        offset: offset
      };

      // var start = offset;
      var j = 0, // goto index
        t; // temporary
      for (var i = 0; i < string.length;) {
        var c = string.charCodeAt(i++);
        if (c === 61 && j > 1)
          break;
        if ((c = PSMU.s64[c]) === undefined)
          throw Error("invalid encoding");
        switch (j) {
          case 0:
            t = c;
            j = 1;
            break;
          case 1:
            PSMU.setBuffer(ret, t << 2 | (c & 48) >> 4);
            // buffer[offset++] = t << 2 | (c & 48) >> 4;
            t = c;
            j = 2;
            break;
          case 2:
            PSMU.setBuffer(ret, (t & 15) << 4 | (c & 60) >> 2);
            // buffer[offset++] = (t & 15) << 4 | (c & 60) >> 2;
            t = c;
            j = 3;
            break;
          case 3:
            PSMU.setBuffer(ret, (t & 3) << 6 | c);
            // buffer[offset++] = (t & 3) << 6 | c;
            j = 0;
            break;
        }
      }
      if (j === 1)
        throw Error("invalid encoding");

      if (ret.offset > 0) {
        var buf = protobuf.util.newBuffer(ret.offset);
        for (var i = 0; i < ret.offset; i++) {
          var len = parseInt(i / 100);
          var pos = i % 100;
          buf[i] = ret[len][pos];
        }
        return buf;
      }
      return null;
      // return offset - start;
    },
    base64Encode: function (buffer, start, end) {
      var parts = null,
        chunk = [];
      var i = 0, // output index
        j = 0, // goto index
        t; // temporary
      while (start < end) {
        var b = buffer[start++];
        switch (j) {
          case 0:
            chunk[i++] = PSMU.b64[b >> 2];
            t = (b & 3) << 4;
            j = 1;
            break;
          case 1:
            chunk[i++] = PSMU.b64[t | b >> 4];
            t = (b & 15) << 2;
            j = 2;
            break;
          case 2:
            chunk[i++] = PSMU.b64[t | b >> 6];
            chunk[i++] = PSMU.b64[b & 63];
            j = 0;
            break;
        }
        if (i > 8191) {
          (parts || (parts = [])).push(String.fromCharCode.apply(String, chunk));
          i = 0;
        }
      }
      if (j) {
        chunk[i++] = PSMU.b64[t];
        chunk[i++] = 61;
        if (j === 1)
          chunk[i++] = 61;
      }
      if (parts) {
        if (i)
          parts.push(String.fromCharCode.apply(String, chunk.slice(0, i)));
        return parts.join("");
      }
      return String.fromCharCode.apply(String, chunk.slice(0, i));
    },
    /*TODO*/
    DU: function (str) {

      if (!PSMU.IS_DU(str)) {
        return PSMU.DP(str)
      }

      str = str.replace(/(2_3)(\w{4}|\w{2})/gi, function ($0, $1, $2) {
        return String.fromCharCode(parseInt($2, 16));
      });
      return str;
    },
    EU: function (str) {
      return str.replace(/([\u0000-\uFFFF])/g, function ($0) {
        try {
          var char16tmp = parseInt($0.charCodeAt().toString(16), 16);
          if (char16tmp >= 0x2002 && char16tmp <= 0x200d) {
            return "";
          }
        } catch (e) {
          return "";
        }

        if ($0.charCodeAt() <= 16) {
          return '2_3000' + $0.charCodeAt().toString(16);
        } else if ($0.charCodeAt() < 256) {
          return '2_300' + $0.charCodeAt().toString(16);
        } else {
          return '2_3' + $0.charCodeAt().toString(16);
        }
      });
    },

    IS_DU: function (str) {
      return str.indexOf('2_3') == 0
    },
    EP: function (json) {
      if (PSMU.isEmtVal(json)) {
        return "";
      }
      var root = protobuf.Root.fromJSON(protobuf.common.get('google/protobuf/struct.proto'));
      var StructMessage = root.lookupType("google.protobuf.Struct");
      var postDataConverted = PSMU.json2Struct(json);
      var message = StructMessage.fromObject(postDataConverted);
      var buf = StructMessage.encode(message).finish();
      // this.bufLen=buf.length;
      return PSMU.base64Encode(buf, 0, buf.length);
    },

    DP: function (str) {
      if (PSMU.isEmtVal(str)) {
        return "";
      }

      if (PSMU.IS_DU(str)) {
        return PSMU.DU(str);
      }

      // var buf = protobuf.util.newBuffer(protobuf.util.base64.length(str));
      // var buf = protobuf.util.newBuffer(this.bufLen);
      var buf = PSMU.base64Decode(str, /*buf,*/ 0);
      var root = protobuf.Root.fromJSON(protobuf.common.get('google/protobuf/struct.proto'));
      var StructMessage = root.lookupType("google.protobuf.Struct");
      var message2 = StructMessage.decode(buf);
      var struct = StructMessage.toObject(message2, protobuf.util.toJSONOptions);
      return PSMU.struct2Json(struct);
    },
    struct2Json: function (structObj) {
      var jsonObj = {};
      var jsonObjTmp = {};
      if (typeof structObj.fields === 'object') {
        jsonObjTmp = structObj.fields;
      }
      Object.keys(jsonObjTmp).forEach(function (field) {
        var fieldObj = jsonObjTmp[field];
        var fieldType = Object.keys(fieldObj)[0];
        switch (fieldType) {
          case 'nullValue':
            // do nothing
            break;
          case 'numberValue':
          case 'stringValue':
          case 'boolValue':
            jsonObj[field] = fieldObj[fieldType];
            break;
          case 'structValue':
            jsonObj[field] = PSMU.struct2Json(fieldObj[fieldType]);
            break;
          case 'listValue':
            jsonObj[field] = PSMU.list2Json(fieldObj[fieldType]);
            break;
        }
      });
      return jsonObj;
    },

    list2Json: function (listObj) {
      var jsonArray = [];
      var jsonArrayTmp = [];
      if (PSMU.isArr(listObj.values)) {
        jsonArrayTmp = listObj.values;
      }

      jsonArrayTmp.forEach(function (itemObj) {
        var fieldType = Object.keys(itemObj)[0];
        switch (fieldType) {
          case 'nullValue':
            // do nothing
            break;
          case 'numberValue':
          case 'stringValue':
          case 'boolValue':
            jsonArray.push(itemObj[fieldType]);
            break;
          case 'structValue':
            jsonArray.push(PSMU.struct2Json(itemObj[fieldType]));
            break;
          case 'listValue':
            jsonArray.push(PSMU.list2Json(itemObj[fieldType]));
            break;
        }
      });
      return jsonArray;
    },

    json2Struct: function (jsonObj) {
      var structObj = {};
      var structObjTmp = {};
      if (typeof jsonObj === 'object') {
        structObjTmp = jsonObj;
      }

      Object.keys(structObjTmp).forEach(function (field) {
        var value = structObjTmp[field];
        switch (typeof (value)) {
          case 'number':
            structObj[field] = {
              numberValue: value
            };
            break;
          case 'string':
            structObj[field] = {
              stringValue: value
            };
            break;
          case 'boolean':
            structObj[field] = {
              boolValue: value
            };
            break;
          case 'object':
            // null
            if (!value) break;
            if (value.constructor === Object) {
              structObj[field] = {
                structValue: PSMU.json2Struct(value)
              };
            } else if (value.constructor === Array) {
              structObj[field] = {
                listValue: PSMU.json2List(value)
              };
            }
            break;
        }
      });
      return {
        fields: structObj
      };
    },

    json2List: function (jsonArray) {
      var listObj = [];
      var listObjTmp = [];

      if (PSMU.isArr(jsonArray)) {
        listObjTmp = jsonArray;
      }

      listObjTmp.forEach(function (jsonObj) {
        switch (typeof (jsonObj)) {
          case 'number':
            listObj.push({
              numberValue: jsonObj
            });
            break;
          case 'string':
            listObj.push({
              stringValue: jsonObj
            });
            break;
          case 'boolean':
            listObj.push({
              boolValue: jsonObj
            });
            break;
          case 'object':
            // null
            if (!jsonObj) break;
            if (jsonObj.constructor === Object) {
              listObj.push({
                structValue: PSMU.json2Struct(jsonObj)
              });
            } else if (jsonObj.constructor === Array) {
              listObj.push({
                listValue: PSMU.json2List(jsonObj)
              });
            }
            break;
        }
      });
      return {
        values: listObj
      };
    },

    escapeText: function (text) {
      return (text) ? window.escape(text) : '';
    },

    unescapeText: function (text) {
      return (text) ? window.unescape(text) : '';
    }
  }
  window.PSMU = PSMU;
}

var psmu = window.PSMU;
export default {
  psmu
}

