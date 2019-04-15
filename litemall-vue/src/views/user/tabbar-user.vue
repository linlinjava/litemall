<template>
  <div class="tabbar-user">
    <user-header :isLogin="isLogin"/>
    <order-group/>
    <ecoupon-group/>
    <user-module/>
    <van-button size="large" class="tabbar-user__quit" v-if="isLogin" @click="quit">退出当前账户</van-button>
  </div>
</template>

<script>
import userHeader from './tabbar-user-header';
import orderGroup from './tabbar-user-order';
// import ecouponGroup from './tabbar-user-ecoupon';
import userModule from './tabbar-user-module';

import { removeLocalStorage } from 'core/utils/local-storage';

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
    quit() {
      removeLocalStorage(
        'Authorization',
        'user_id',
        'avatar',
        'background_image',
        'nickName'
      );
      this.$router.push({ name: 'login' });
    },
    getLoginStatus() {
      this.isLogin =
        !!localStorage.getItem('Authorization') &&
        !!localStorage.getItem('user_id');
    }
  },

  components: {
    [userHeader.name]: userHeader,
    [orderGroup.name]: orderGroup,
    // [ecouponGroup.name]: ecouponGroup,
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
