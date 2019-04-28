<template>
  <div class="order-goods">
    <van-card
      v-for="item in goodsInfo.orderGoods"
      :key="item.id"
      :title="item.goodsName"
      desc="暂无描述"
      :num="item.number"
      :price="item.price +'.00'"
      :thumb="item.picUrl"
    ></van-card>

    <van-cell-group>
      <!-- <van-field v-model="remark" placeholder="请输入备注" label="订单备注"> -->
      <!-- <template slot="icon">{{remark.length}}/50</template> -->
      <!-- </van-field> -->
      <van-cell title="商品金额">
        <span class="red">{{goodsInfo.orderInfo.goodsPrice * 100 | yuan}}</span>
      </van-cell>
      <van-cell title="邮费" :value="goodsInfo.orderInfo.freightPrice "></van-cell>
      <!-- <van-cell title="税费" value="¥8.96"></van-cell> -->
      <!-- <van-cell title="优惠券">
        <span class="red">-{{ goodsInfo.orderInfo.xxx * 100 || 0 | yuan}}</span>
      </van-cell>-->
    </van-cell-group>
  </div>
</template>

<script>
import { Card, Field } from 'vant';

export default {
  name: 'bottom-goods-info',
  props: {
    goodsInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      remark: ''
      // goodsInfo: {}
    };
  },
  created() {},
  methods: {
    // async init() {
    //   let { data } = await this.$reqGet(
    //     '/wx/cart/checkout?cartId=0&addressId=0&couponId=0&grouponRulesId=0'
    //   );
    //   debugger;
    //   this.goodsInfo = data.data;
    // }
  },
  components: {
    [Card.name]: Card,
    [Field.name]: Field
  }
};
</script>
<style lang="scss" scoped>
.order-goods {
  background-color: #fff;
}
</style>
