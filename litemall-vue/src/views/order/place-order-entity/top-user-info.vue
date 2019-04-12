<template>
  <van-cell-group>
    <van-cell
      v-if="goodsInfo.checkedAddress.id !== 0"
      icon="dingwei"
      isLink
      @click="goAddressList(goodsInfo)"
      :title="`${goodsInfo.checkedAddress.name}  ${goodsInfo.checkedAddress.mobile}`"
      :label="goodsInfo.checkedAddress.address"
    />
    <van-cell
      v-else
      icon="dingwei"
      isLink
      @click="goAddressList(goodsInfo)"
      title="您暂时没有配送地址，请输入配送地址"
      :label="goodsInfo.checkedAddress.address"
    />

    <!-- <van-cell class="daodian" title="到店自提" label="浙江省 杭州市 西湖区 创新创业园">
      <van-checkbox v-model="isDaoDian" slot="icon"></van-checkbox>
    </van-cell>-->
    <!-- <van-cell icon="id-card" title="张三" label="330327********1574" isLink/> -->
  </van-cell-group>
</template>

<script>
import { Checkbox } from 'vant';

export default {
  name: 'top-user-info',
  props: {
    goodsInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      isDaoDian: false
    };
  },
  methods: {
    goAddressList(goodInfo) {
      this.$router.push({
        path: '/user/address'
      });
    }
  },
  components: {
    [Checkbox.name]: Checkbox
  }
};
</script>


<style lang="scss">
.daodian {
  .van-checkbox .van-icon-success {
    height: 16px;
    width: 16px;
    font-size: $font-size-small;
    &::before {
      line-height: 16px;
    }
  }
  .van-checkbox__input {
    height: 16px;
  }
  .van-checkbox__label {
    margin-left: 0;
  }
  .shop_address {
    padding-left: 25px;
    box-sizing: border-box;
  }
}
</style>
