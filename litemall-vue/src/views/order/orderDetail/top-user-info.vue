<template>
  <van-cell-group>
    <van-cell
      icon="dingwei"
      isLink
      :title="`${goodsInfo.orderInfo.consignee}  ${goodsInfo.orderInfo.mobile}`"
      :label="goodsInfo.orderInfo.address"
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
