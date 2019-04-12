<template>
	<div class="item_cell_group">
		<van-cell-group>
			<van-cell title="有效期" value="2017-8-18~2018-8-19"/>
			<van-cell title="选择规格" isLink :value="skuComb.sku_str" @click.native="skuClick" />
			<van-cell title="文一西路花蒋路交叉口" isLink url="http://m.amap.com/navi/?dest=120.145409,30.238695&key=ab67b14d58d47912a9feb63ba862450c&destName=三潭印月"/>
			<van-cell isLink v-if="mobile">
				<template slot="title">
					<a :href="'tel:' + mobile" class="store_mobile">{{mobile}}</a>
				</template>
			</van-cell>
			<van-cell title="注意事项" @click.native="showPopup = true" isLink />
		</van-cell-group>

		<van-sku
			v-model="showSku"
			:showAddCartBtn="showAddCartBtn"
			:buyText="buyText"
			:sku="sku.sku"
			:goods="sku.goods_info"
			:goodsId="sku.goods_id"
			:disableStepperInput="true"
			@buy-clicked="buyGoods"
		/>

		<van-popup v-model="showPopup"  position="bottom" lockOnScroll>
			<div class="popup_wrap">
				<van-icon name="clear" class="cancel_popup" @click.native="showPopup = false"></van-icon>
				<div class="popup_header">注意事项</div>
				<div class="popup_content">
					<div>这里是注意事项的内容:</div>
					<ol>
						<li>这里是注意意事项的内容事项的内容</li>
						<li>这里是注意事意事项的内容项的内容</li>
						<li>这里是注意意事项的内容事项的内容</li>
						<li>这里是注意意事项的内容事项的内容</li>
						<li>这里是注意意事项的内容事项的内容</li>
						<li>这里是注意事项的内容</li>
						<li>这里是注意意事项的内容事项的内容</li>
						<li>这里是注意意事项的内容事项的内容</li>
						<li>这里是注意意事项的内容事项的内容</li>
					</ol>
				</div>
			</div>
		</van-popup>
	</div>
</template>

<script>
//	import sku from "./sku";
import actionMixin from '../mix';

export default {
  name: 'virtual-group',
  props: {
    mobile: String,
    skuComb: {
      type: Object,
      default: () => ({})
    },
    addressVal: {
      type: Object,
      default: () => ({})
    }
  },
  mixins: [actionMixin],
  data() {
    return {
      //				sku,
    };
  },
  methods: {
    addressClick() {}
  }
};
</script>


<style lang="scss" scoped>
.popup_wrap {
  position: relative;
  padding-bottom: 30px;
  box-sizing: border-box;
}
.popup_header {
  padding: 15px 0 30px 0;
  text-align: center;
}

.popup_content {
  height: 150px;
  box-sizing: border-box;
  overflow-x: hidden;
  overflow-y: scroll;
  padding: 0 10px;
  line-height: 30px;
  &::-webkit-scrollbar {
    background-color: #fff;
    width: 5px;
  }
  &::-webkit-scrollbar-thumb {
    border-radius: 3px;
    background-color: #bebebe;
  }
  ol {
    padding-left: 15px;
    list-style: decimal;
  }
}

.store_mobile {
  display: block;
}

.cancel_popup {
  position: absolute;
  right: 15px;
  top: 15px;
  font-size: 18px;
}
</style>
