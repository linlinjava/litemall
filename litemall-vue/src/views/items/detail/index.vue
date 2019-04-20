<template>
  <div class="item_detail">
    <van-swipe :autoplay="3000">
      <van-swipe-item v-for="(image, index) in itemImgs" :key="index">
        <!-- <img v-lazy="image" width="100%"> -->
        <img :src="image" width="100%">
      </van-swipe-item>
    </van-swipe>
    <van-cell-group class="item_cell_group" v-if="goods">
      <van-cell class="item_info">
        <div>
          <span class="item_price">{{ goods.retailPrice*100 | yuan }}</span>
          <span class="item_market_price">{{goods.counterPrice*100 | yuan}}</span>
        </div>
        <div class="item-title">
          <!-- <van-tag plain type="danger" v-if="goods.is_haitao">海淘</van-tag> -->
          {{ goods.name }}
        </div>
        <!-- <div class="item_intro">{{goods.sell_point}}</div> ???-->
        <!-- <div class="item_dispatch">发货地: {{}}</div> -->
      </van-cell>
    </van-cell-group>

    <component
      v-if="goods"
      ref="goodAction"
      v-bind:is="'entity-group'"
      :selectSku.sync="selectSku"
      :addressVal.sync="addressVal"
      :mobile="mobile"
      :goods-info="goods"
      @skuBuy="doBuyNow"
      @cart-count="cartEvent"
    />

    <div class="item_desc" v-if="goods">
      <div class="item_desc_title">商品详情</div>
      <div class="item_desc_wrap" v-if="goods.detail.length === 0" style="padding-left: 170px;">
        <p>无详情</p>
      </div>
      <div class="item_desc_wrap" v-html="goods.detail"></div>
    </div>

    <van-goods-action>
      <van-goods-action-mini-btn @click="toCart" icon="cart" :info="cartInfo"/>
      <van-goods-action-mini-btn
        :style="collectAdd ? 'color: #f7b444;':''"
        @click="addCollect"
        icon="shoucang"
      />
      <van-goods-action-big-btn @click="openSku('cart')" text="加入购物车"/>
      <van-goods-action-big-btn primary @click="openSku('buy')" text="立即购买"/>
    </van-goods-action>

  </div>
</template>

<script>
import { GOODS_DETAIL } from '@/api/goods';

import {
  Swipe,
  SwipeItem,
  GoodsAction,
  GoodsActionBigBtn,
  GoodsActionMiniBtn,
  Popup
} from 'vant';

export default {
  props: {
    itemId: [String, Number]
  },

  data() {
    const isLogin = !!localStorage.getItem('Authorization');
    return {
      isLogin,
      itemImgs: [],
      collectAdd: false,
      cartInfo: '0',
      selectSku: {
        selectedNum: 1,
        selectedSkuComb: {}
      },
      addressVal: {
        id: null,
        area_name: '',
        district: '',
        city: '',
        province: ''
      },
      goods: null,
      productList: []
    };
  },

  computed: {
    // itemImgs() {
    //   debugger;
    //   return this.goods.info.gallery;
    // }
  },

  created() {
    this.initData();
  },

  methods: {
    async initData() {
      // let a = this.$route.params.itemId;
      this.$reqGet(`/wx/goods/detail?id=${this.itemId}`).then(
        res => {
          this.goods = res.data.data.info;
          this.goods.attribute = res.data.data.attribute;
          this.goods.specificationList = res.data.data.specificationList;
          this.goods.productList = res.data.data.productList;
          this.productList = res.data.data.productList;
          this.itemImgs = res.data.data.info.gallery || [];
          this.collectAdd = res.data.data.userHasCollect === 1;
        }
      );

      let { data } = await this.$reqGet('/wx/cart/goodscount');
      this.cartInfo = data.data;

      // this.$reqGet(GOODS_DETAIL).then(res => {
      //   this.goods = res.data.data;
      // });
    },
    openSku(status) {
      const goodAction = this.$refs.goodAction;
      goodAction.skuClick(status);
    },
    cartEvent(count) {
      this.cartInfo = ~~this.cartInfo + ~~count + '';
    },
    doBuyNow() {
      // if (
      //   (this.goods.has_sku && this.selectSku.sku_id) ||
      //   !this.goods.has_sku
      // ) {
      //   this.$router.push({ name: 'placeOrderEntity' });
      // } else {
      //   const goodAction = this.$refs.goodAction;
      //   goodAction.showSku = true;
      //   goodAction.isSkuBuy = true;
      // }
    },
    addCart() {
      // debugger;
      // if (this.goods.has_sku && this.selectSku.sku_id) {
      // this.$reqPost('/wx/cart/add', {
      //   goodsId: this.itemId,
      //   number: 1
      // }).then(() => {
      //   this.$toast({
      //     message: '已添加至购物车',
      //     duration: 1500
      //   });
      //   this.cartInfo = String(parseInt(this.cartInfo) + 1);
      // });
      // }
    },
    toCart() {
      this.$router.push({
        name: 'cart'
      });
    },
    async addCollect() {
      let { data } = await this.$reqPost(
        '/wx/collect/addordelete',
        {
          valueId: this.itemId,
          type: 0
        }
      );
      let type = data.data.type;
      this.collectAdd = type === 'add' ? true : false;
      this.$toast({
        message: this.collectAdd ? '添加成功' : '取消成功',
        duration: 1500
      });
    }
  },

  components: {
    [Popup.name]: Popup,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [GoodsAction.name]: GoodsAction,
    [GoodsActionBigBtn.name]: GoodsActionBigBtn,
    [GoodsActionMiniBtn.name]: GoodsActionMiniBtn,
    'entity-group': () =>
      import(/* webpackChunkName: "EntityGroup" */ './EntityGroup/index'),
    'virtual-group': () =>
      import(/* webpackChunkName: "VirtualGroup" */ './VirtualGroup/index.vue')
  }
};
</script>

<style lang="scss" scoped>
.item_detail {
  img {
    max-width: 100%;
  }
}

.item_cell_group {
  margin-bottom: 15px;
}

.item_price {
  font-size: 20px;
  color: $red;
  margin-right: 10px;
}

.item_market_price {
  color: $font-color-gray;
  text-decoration: line-through;
  font-size: $font-size-small;
}

.item-title {
  line-height: 1.4;
}

.item_dispatch {
  font-size: $font-size-small;
  color: $font-color-gray;
}

.item_intro {
  line-height: 18px;
  margin: 5px 0;
  font-size: $font-size-small;
  color: $font-color-gray;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.item_desc {
  background-color: #fff;
  p {
    padding: 0 10px;
  }
  /deep/ img {
    max-width: 100%;
    display: block;
  }
}

.item_desc_title {
  @include one-border;
  padding: 10px 0;
  text-align: center;
}
</style>
