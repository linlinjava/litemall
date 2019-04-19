<template>
  <div>
    <van-cell-group>
      <van-cell title="联系客服" @click="showKefu = true" isLink></van-cell>
      <van-cell title="意见反馈" isLink></van-cell>
      <van-cell title="常见问题" isLink></van-cell>
    </van-cell-group>
    <van-popup v-model="showKefu">
      <md-kefu mobile="16454193338"/>
    </van-popup>
  </div>
</template>

<script>
import { Popup } from 'vant';
import md_kefu from '@/vue/components/md-kefu/';

export default {
  data() {
    return {
      showKefu: false
    };
  },

  components: {
    [Popup.name]: Popup,
    [md_kefu.name]: md_kefu
  }
};
</script>
