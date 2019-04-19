<template>
  <div class="payment">
    <div class="time_down payment_group">
      请在
      <span class="red">半小时内</span>
      完成付款，否则系统自动取消订单
    </div>

    <van-cell-group class="payment_group">
      <van-cell title="订单编号" :value="order_id"/>
      <van-cell title="实付金额">
        <span class="red">{{order_info.orderInfo.actualPrice *100 | yuan}}</span>
      </van-cell>
    </van-cell-group>

    <div class="pay_way_group">
      <div class="pay_way_title">选择支付方式</div>
      <van-radio-group v-model="payWay" @change="payWay">
        <van-cell-group>
          <!-- <van-cell>
            <van-radio @click-native="payWay" name="ali">
              <img src="../../../assets/images/ali_pay.png" alt="支付宝" width="82" height="29">
            </van-radio>
          </van-cell>-->
          <van-cell>
            <van-radio name="wx">
              <img src="../../../assets/images/wx_pay.png" alt="微信支付" width="113" height="23">
            </van-radio>
            <!-- <button @click="pay">wx-pay-test</button> -->
          </van-cell>
        </van-cell-group>
      </van-radio-group>
    </div>

    <van-button class="pay_submit" @click="pay" :loading="isSubmit" type="primary" bottomAction>去支付</van-button>
  </div>
</template>

<script>
import { Radio, RadioGroup } from 'vant';
import _ from 'lodash';
import md5 from 'js-md5';

export default {
  name: 'payment',

  data() {
    return {
      isSubmit: false,
      payWay: 'wx',
      order_info: {},
      order_id: '',
      checkedName: '1'
    };
  },
  created() {
    if (_.has(this.$route.params, 'order_id')) {
      this.order_id = this.$route.params.order_id;
      this.getOrder(this.order_id);
    }
  },
  methods: {
    payWay(name) {
      console.log(name);
    },
    async getOrder(id) {
      let { data } = await this.$reqGet(
        `/wx/order/detail?orderId=${id}`
      );
      this.order_info = data.data;
    },
    async pay() {
      let params = {};
      params.orderId = this.order_info.orderInfo.id;
      let { data } = await this.$reqPost(
        `/wx/order/prepay`,
        params
      );
      var that = this;
      function onBridgeReady() {
        console.log(JSON.stringify(data));
        // var timeStamp = Date.parse(new Date());
        // var packageV = 'prepay_id=' + data.data.prepay_id;
        var params = {
          appId: data.data.appId, //公众号名称，由商户传入
          timeStamp: data.data.timeStamp, //时间戳，自1970年以来的秒数
          nonceStr: data.data.nonceStr, //随机串
          package: data.data.packageValue,
          signType: data.data.signType, //微信签名方式：
          paySign: data.data.paySign //微信签名
        };

        console.log(params);
        WeixinJSBridge.invoke('getBrandWCPayRequest', params, function(res) {
          console.log(JSON.stringify(res));
          if (res.err_msg == 'get_brand_wcpay_request:ok') {
            // 使用以上方式判断前端返回,微信团队郑重提示：
            //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
            that.$router.push({
              name: 'paymentStatus',
              params: {
                status: 'success'
              }
            });
          }
        });
      }
      if (typeof WeixinJSBridge == 'undefined') {
        if (document.addEventListener) {
          document.addEventListener(
            'WeixinJSBridgeReady',
            onBridgeReady,
            false
          );
        } else if (document.attachEvent) {
          document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
          document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
      } else {
        onBridgeReady();
      }
      this.order_info = data.data;
    },
    paySubmit() {
      this.$router.push({
        name: 'paymentStatus',
        params: {
          status: 'success'
        }
      });
    }
  },

  components: {
    [Radio.name]: Radio,
    [RadioGroup.name]: RadioGroup
  }
};
</script>

<style lang="scss" scoped>
.payment_group {
  margin-bottom: 10px;
}

.time_down {
  background-color: #fffeec;
  padding: 10px 15px;
}

.pay_submit {
  position: fixed;
  bottom: 0;
  width: 100%;
}

.pay_way_group img {
  vertical-align: middle;
}

.pay_way_title {
  padding: 15px;
  background-color: #fff;
}
</style>
