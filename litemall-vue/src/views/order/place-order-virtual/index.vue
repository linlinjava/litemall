<template>
  <div class="place_order_virtual">
    <van-panel>
      <van-card
        slot="header"
        :title="goods.title"
        :desc="goods.desc"
        num="2"
        price="2.00"
        :thumb="goods.thumb"
      />

      <div class="panel_content">
        <van-icon :name="showNotice ? 'arrow-up' : 'arrow-down'" class="panel_notice"/>
        <div @click="showNotice = !showNotice">注意事项</div>
        <ol v-if="showNotice">
          <li>注意事项1</li>
          <li>注意事项2</li>
          <li>注意事项3</li>
          <li>注意事项4</li>
        </ol>
      </div>

      <div slot="footer" class="clearfix">
        <div class="float-l">商品金额</div>
        <div class="float-r red">{{7200 | yuan}}</div>
      </div>
    </van-panel>

    <van-submit-bar
      :price="3050"
      label="总计："
      buttonText="提交订单"
      :loading="isSubmit"
      :disabled="isDisabled"
      @submit="onSubmit"
    />
  </div>
</template>

<script>
import { Panel, SubmitBar, Card } from 'vant';

export default {
  data() {
    return {
      showNotice: false,
      isSubmit: false,
      isDisabled: false,
      goods: {
        id: '2',
        title: '陕西蜜梨',
        desc: '约600g',
        price: 690,
        status: 1,
        num: 3,
        thumb:
          'https://img.yzcdn.cn/public_files/2017/10/24/f6aabd6ac5521195e01e8e89ee9fc63f.jpeg'
      }
    };
  },

  methods: {
    async onSubmit() {
      let { data } = await this.$reqPost('/wx/order/submit', {
        addressId: 2,
        cartId: 0,
        couponId: 0,
        grouponLinkId: 0,
        grouponRulesId: 0,
        message: ''
      });
      this.isSubmit = true;

      console.log('提交订单');
    }
  },

  components: {
    [Panel.name]: Panel,
    [Card.name]: Card,
    [SubmitBar.name]: SubmitBar
  }
};
</script>

<style scoped lang="scss">
.panel_content {
  position: relative;
  padding: 10px 15px;
  .panel_notice {
    position: absolute;
    top: 15px;
    right: 15px;
  }

  ol {
    padding-left: 12px;
    padding-top: 10px;
    list-style: decimal;
    color: $font-color-gray;
    overflow: hidden;
    li {
      margin-bottom: 10px;
      font-size: $font-size-small;
      &:last-child {
        margin: 0;
      }
    }
  }
}

/*
	.slide-enter,.slide-leave-to{
		height: 0;
	}

	.slide-enter-active,.slide-leave-active{
		transition: all 1s;
	}
*/
</style>
