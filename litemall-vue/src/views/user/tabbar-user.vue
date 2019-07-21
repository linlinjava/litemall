<template>
  <div class="tabbar-user">
    <user-header :isLogin="isLogin"/>
    <order-group/>
    <coupon-group/>
    <user-module/>
  </div>
</template>

<script>
import userHeader from './tabbar-user-header';
import orderGroup from './tabbar-user-order';
import couponGroup from './tabbar-user-coupon';
import userModule from './tabbar-user-module';

export default {
  data() {
    return {
      isLogin: false
    };
  },

  activated() {
    this.getLoginStatus();
  },

  methods: {
    getLoginStatus() {
      this.isLogin =
        !!localStorage.getItem('Authorization');
    }
  },

  components: {
    [userHeader.name]: userHeader,
    [orderGroup.name]: orderGroup,
    [couponGroup.name]: couponGroup,
    [userModule.name]: userModule
  }
};
</script>


<style scoped lang="scss">
.tabbar-user {
  > div {
    margin-bottom: 10px;
  }
  &__quit {
    border: 0;
    border-radius: 0;
  }
}
</style>
