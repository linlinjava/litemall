<template>
  <van-cell-group>
    <van-cell>
      <van-notice-bar
        :text="notice"
        background="white"
        :leftIcon="trumpet"
        style="padding-left: 0"
      />
    </van-cell>
    <van-cell :title="address" icon="dingwei" isLink :url="mapSrc"></van-cell>
    <van-cell icon="phone" isLink>
      <template slot="title">
        <a :href="'tel:' + mobile" class="store_mobile">{{mobile}}</a>
      </template>
    </van-cell>
  </van-cell-group>
</template>

<script>
import { NoticeBar } from 'vant';
import trumpet from '@/assets/images/trumpet.png';

export default {
  name: 'shop-info-group',
  props: {
    address: {
      type: String,
      required: true
    },
    mobile: {
      type: String,
      required: true
    },
    notice: {
      type: String,
      default: ''
    },
    location: Object
  },

  data() {
    const { location } = this;
    const MAP_PATH = `http://m.amap.com/navi/?dest=${location.lat},${
      location.lng
    }&key=ab67b14d58d47912a9feb63ba862450c&destName=${location.name}`;
    return {
      trumpet,
      mapSrc: location ? MAP_PATH : '#'
    };
  },

  created() {},

  components: {
    [NoticeBar.name]: NoticeBar
  }
};
</script>


<style lang="scss" scoped>
.store_mobile {
  color: #0000a0;
  text-decoration: underline;
}
</style>
