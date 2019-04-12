<template>
  <div class="place_order_entity">
    <top-user-info :goodsInfo="goodsInfo" style="margin-bottom: 20px;"/>

    <bottom-goods-info :goodsInfo="goodsInfo"/>

    <van-cell-group style="margin-top: 20px;">
      <van-cell title="下单时间">
        <span>{{goodsInfo.orderInfo.addTime }}</span>
      </van-cell>
      <van-cell title="订单编号">
        <span>{{goodsInfo.orderInfo.orderSn }}</span>
      </van-cell>

      <van-cell>
        <template slot="title">
          <span class="custom-text">实付款：</span>
          <span class="red">{{goodsInfo.orderInfo.actualPrice * 100 | yuan}}</span>
        </template>
        <!-- 订单动作 -->
        <van-button
          v-if="getStatus() !== ''"
          size="small"
          @click="orderAction"
          style=" float:right"
          type="danger"
        >{{getCurrentButtonText()}}</van-button>
        <!-- 未付款的时候价格取消的动作 -->
        <van-button
          size="small"
          v-if="getStatus() === 'pay'"
          @click="cancelOrder"
          style=" float:right"
          type="danger"
        >取消订单</van-button>
      </van-cell>
    </van-cell-group>

    <van-cell-group v-if="showExp" style="margin-top: 20px;">
      <van-cell title="快递公司">
        <span>{{goodsInfo.orderInfo.expCode }}</span>
      </van-cell>
      <van-cell title="快递编号">
        <span>{{goodsInfo.orderInfo.expNo }}</span>
      </van-cell>
    </van-cell-group>
    <!-- <van-button @click="cancelOrder" style=" position: absolute;bottom: 4px;z-index: 1000;" type="danger">取消订单</van-button> -->
    <!-- <van-submit-bar
      v-if="showSubmit()"
      :price="goodsInfo.orderInfo.actualPrice*100"
      label="总计："
      buttonText="支付"
      :loading="isSubmit"
      :disabled="isDisabled"
      @submit="onSubmit"
    ></van-submit-bar>-->
  </div>
</template>

<script>
import topUserInfo from './top-user-info';
import bottomGoodsInfo from './bottom-goods-info';
import { SubmitBar, Button, Cell, CellGroup, Dialog } from 'vant';
import _ from 'lodash';

export default {
  data() {
    return {
      isSubmit: false,
      isDisabled: false,
      goodsInfo: {}
    };
  },
  created() {
    this.init();
  },

  methods: {
    getStatus() {
      let options = this.goodsInfo.orderInfo.handleOption;
      let current_allow_option = '';
      _.each(options, (v, k) => {
        if (v) current_allow_option = k;
      });
      return current_allow_option;
    },
    /**\
     * cancel: false
      comment: false
      confirm: false
      delete: true
      pay: false
      rebuy: false
      refund: false
     */
    showExp() {
      return _.has(goodsInfo.orderInfo, 'expNo');
    },
    getCurrentButtonText() {
      let option = this.getStatus();
      if (option === 'cancel') return '取消订单';
      if (option === 'delete') return '删除订单';
      if (option === 'confirm') return '确认收货';
      if (option === 'pay') return '支付订单';
      if (option === 'rebuy') return '删除订单';
      if (option === 'refund') return '申请退款';
      if (option === 'comment') return '评价';
    },
    showSubmit() {
      if (this.getStatus() === 'refund') {
        return false;
      }
    },
    async orderAction() {
      let option = this.getStatus();
      if (option === 'cancel') {
        this.cancelOrder();
      }
      if (option === 'confirm') {
        await Dialog.confirm({
          message: '确定收货？'
        }).then(() => {
          this.doAction(option);
        });
      }
      if (option === 'refund') {
        await Dialog.confirm({
          message: '确定要取消此订单？'
        }).then(() => {
          this.doAction(option);
        });
      }
      if (option === 'rebuy') {
        await Dialog.confirm({
          message: '确定要删除此订单？'
        }).then(() => {
          this.doAction('delete');
        });
      }
      if (option === 'pay') {
        this.doAction('prepay', false);
      }
      // if (option === 'pay')
      // if (option === 'rebuy')
      // if (option === 'refund')
      // if (option === 'comment')
    },
    async doAction(status, skip) {
      let { data } = await this.$reqPost(`/wx/order/${status}`, {
        orderId: this.$route.query.orderId
      });
      if (skip != false) {
        if (data.errno == 0) this.$router.go(-1);
      }
      if (status == 'prepay') {
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
      }
    },
    async cancelOrder() {
      await Dialog.confirm({
        message: '确定取消订单？'
      }).then(() => {
        let { data } = this.$reqPost('/wx/order/cancel', {
          orderId: this.$route.query.orderId
        });
        // if (data.errno == 0)
        this.$router.go(-1);
      });
    },
    async delOrder() {
      let { data } = await this.$reqPost('/wx/order/delete', {
        orderId: this.$route.query.orderId
      });
      if (data.errno == 0) this.$router.go(-1);
    },
    async onSubmit() {
      this.isSubmit = true;
      let cartId = this.$route.params.cartId;
      let { data } = await this.$reqPost('/wx/order/submit', {
        addressId: this.goodsInfo.addressId,
        cartId: cartId || 0,
        couponId: 0,
        grouponLinkId: 0,
        grouponRulesId: 0,
        message: ''
      });
      this.$router.replace({ name: 'payment' });
    },
    async init() {
      // let cartId = this.$route.params.cartId;
      let orderId = this.$route.query.orderId;
      let { data } = await this.$reqGet(
        `/wx/order/detail?orderId=${orderId}`
      );
      this.goodsInfo = data.data;
    }
  },

  components: {
    [Dialog.name]: Dialog,
    [CellGroup.name]: CellGroup,
    [Cell.name]: Cell,
    [Button.name]: Button,
    [SubmitBar.name]: SubmitBar,
    [topUserInfo.name]: topUserInfo,
    [bottomGoodsInfo.name]: bottomGoodsInfo
  }
};
</script>


<style lang="scss" scoped>
.place_order_entity {
  padding-bottom: 70px;
}
</style>
