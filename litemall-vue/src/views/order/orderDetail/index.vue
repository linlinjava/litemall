<template>
  <div class="place_order_entity">
    <div class="order-goods">
      <van-card
        v-for="item in orderGoods"
        :key="item.id"
        :title="item.goodsName"
        desc="暂无描述"
        :num="item.number"
        :price="item.price +'.00'"
        :thumb="item.picUrl"
      ></van-card>

      <van-cell-group>
        <van-cell title="商品金额">
          <span class="red">{{orderInfo.goodsPrice * 100 | yuan}}</span>
        </van-cell>
        <van-cell title="邮费" :value="orderInfo.freightPrice "></van-cell>
      </van-cell-group>
    </div>

    <van-cell-group style="margin-top: 20px;">
      <van-cell
        icon="dingwei"
        :title="`${orderInfo.consignee}  ${orderInfo.mobile}`"
        :label="orderInfo.address"
      />
    </van-cell-group>

    <van-cell-group style="margin-top: 20px;">
      <van-cell title="下单时间">
        <span>{{orderInfo.addTime }}</span>
      </van-cell>
      <van-cell title="订单编号">
        <span>{{orderInfo.orderSn }}</span>
      </van-cell>
      <van-cell title="订单备注">
        <span>{{orderInfo.remark }}</span>
      </van-cell>

      <van-cell>
        <template slot="title">
          <span class="custom-text">实付款：</span>
          <span class="red">{{orderInfo.actualPrice * 100 | yuan}}</span>
        </template>
        <!-- 订单动作 -->
       <van-button
          size="small"
          v-if="handleOption.cancel"
          @click="cancelOrder"
          style=" float:right"
          round type="danger"
        >取消订单</van-button>
        <van-button
          size="small"
          v-if="handleOption.pay"
          @click="payOrder"
          style=" float:right"
          round type="danger"
        >去支付</van-button>
        <van-button
          size="small"
          v-if="handleOption.delete"
          @click="deleteOrder"
          style=" float:right"
          type="danger"
        >删除订单</van-button>     
        <van-button
          size="small"
          v-if="handleOption.confirm"
          @click="confirmlOrder"
          style=" float:right"
          type="danger"
        >确认收货</van-button>    
        <van-button
          size="small"
          v-if="handleOption.refund"
          @click="refundOrder"
          style=" float:right"
          type="danger"
        >退款</van-button>                    
      </van-cell>
    </van-cell-group>

    <van-cell-group v-if="showExp()" style="margin-top: 20px;">
      <van-cell title="快递公司">
        <span>{{orderInfo.expCode }}</span>
      </van-cell>
      <van-cell title="快递编号">
        <span>{{orderInfo.expNo }}</span>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script>
import { Card, Field, SubmitBar, Button, Cell, CellGroup, Dialog } from 'vant';
import _ from 'lodash';
import { orderDetail } from '@/api/api';

export default {
  data() {
    return {
      isSubmit: false,
      isDisabled: false,
      orderInfo: {},
      orderGoods: [],
      handleOption: {},
      expressInfo: {}
    };
  },
  created() {
    this.init();
  },

  methods: {
    showExp() {
      return _.has(this.orderInfo, 'expNo');
    },
    confirmOrder(){
     Dialog.confirm({
          message: '确定收货？'
        }).then(() => {
        });
    },
    refundOrder(){
     Dialog.confirm({
          message: '确定要取消此订单？'
        }).then(() => {
        });
    },
    deleteOrder(){

    },    
    payOrder(){

    },     
    cancelOrder(){
     Dialog.confirm({
          message: '确定取消此订单？'
        }).then(() => {
          //  this.$router.go(-1);
        });
    },      
    init() {
      let orderId = this.$route.query.orderId;
      orderDetail({ orderId: orderId }).then(res => {
        var data = res.data.data;
        this.orderInfo = data.orderInfo;
        this.orderGoods = data.orderGoods;
        this.handleOption = data.orderInfo.handleOption;
        this.expressInfo = data.expressInfo;
      });
    }
  },

  components: {
    [Dialog.name]: Dialog,
    [CellGroup.name]: CellGroup,
    [Cell.name]: Cell,
    [Button.name]: Button,
    [SubmitBar.name]: SubmitBar,
    [Card.name]: Card,
    [Field.name]: Field
  }
};
</script>


<style lang="scss" scoped>
.place_order_entity {
  padding-bottom: 70px;
}
</style>
