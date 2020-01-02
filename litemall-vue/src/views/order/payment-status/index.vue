<template>
  <div class="payment_status">
    <div class="status_top">
      <van-icon :name="statusIcon" :class="statusClass"/>
      <div>{{statusText}}</div>
    </div>

    <div class="status_text" v-if="isSuccess">
      <span class="red">3秒</span>跳转订单
    </div>
    <div class="status_text" v-else>系统繁忙, 支付遇到问题, 请您稍后再试!</div>

    <div class="status_goLink">
      <router-link class="red" :to="{name: 'user'}">查看订单
        <van-icon name="arrow"/>
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'payment-status',

  props: {
    status: String
  },
  created() {
    setTimeout(() => {
      this.$router.push({ path: '/user/order/list/0' });
    }, 3000);
  },
  data() {
    return {
      isSuccess: true
    };
  },

  computed: {
    statusText() {
      return this.isSuccess ? '支付成功' : '支付失败';
    },
    statusIcon() {
      return this.isSuccess ? 'checked' : 'fail';
    },
    statusClass() {
      return this.isSuccess ? 'success_icon' : 'fail_icon';
    }
  },

  activated() {
    this.isSuccess = this.status === 'success';
  }
};
</script>


<style lang="scss" scopd>
.payment_status {
  padding-top: 30px;
  box-sizing: border-box;
  background-color: #fff;
  text-align: center;
}

.status_top {
  margin-bottom: 15px;
  i {
    margin-bottom: 5px;
  }
  > div {
    font-size: 18px;
  }
}

.status_text {
  color: $font-color-gray;
  margin-bottom: 50px;
}

.status_icon {
  font-size: 80px;
}

i.success_icon {
  @extend .status_icon;
  color: #06bf04;
}

i.fail_icon {
  @extend .status_icon;
  color: #f44;
}
</style>
