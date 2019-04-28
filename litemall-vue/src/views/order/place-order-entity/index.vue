<template>
  <div class="place_order_entity">
    <top-user-info :goodsInfo="goodsInfo" style="margin-bottom: 20px;"/>
    <bottom-goods-info :goodsInfo="goodsInfo"/>

    <van-submit-bar
      :price="goodsInfo.orderTotalPrice*100"
      label="总计："
      buttonText="提交订单"
      :loading="isSubmit"
      :disabled="isDisabled"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import topUserInfo from './top-user-info';
import bottomGoodsInfo from './bottom-goods-info';
import { SubmitBar, Dialog } from 'vant';

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
    async onSubmit() {
      this.isSubmit = true;
      let cartId = this.$route.params.cartId;
      if (this.goodsInfo.addressId === 0) {
        Dialog.alert({
          message: '未选择收货地址'
        }).then(() => {
          // on close
        });
      }
      let { data } = await this.$reqPost('/wx/order/submit', {
        addressId: this.goodsInfo.addressId,
        cartId: cartId || 0,
        couponId: 0,
        grouponLinkId: 0,
        grouponRulesId: 0,
        message: ''
      });

      this.$router.push({
        name: 'payment',
        params: { order_id: data.data.orderId }
      });
    },
    async init() {
      let cartId = this.$route.params.cartId;
      let { data } = await this.$reqGet(
        `/wx/cart/checkout?cartId=${cartId ||
          0}&addressId=0&couponId=0&grouponRulesId=0`
      );
      this.goodsInfo = data.data;
    }
  },

  components: {
    [Dialog.name]: Dialog,
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
