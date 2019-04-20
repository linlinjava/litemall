<template>
  <div>
    <van-cell-group>
      <van-cell title="联系客服" @click="showKefu = true" isLink></van-cell>
      <van-cell title="意见反馈" isLink></van-cell>
      <van-cell title="常见问题" isLink></van-cell>
    </van-cell-group>
    <van-popup v-model="showKefu">
      电话 XXX
    </van-popup>
  </div>
</template>

<script>
import { Popup } from 'vant';

export default {
  data() {
    return {
      showKefu: false
    };
  },

  components: {
    [Popup.name]: Popup
  }
};
</script>
