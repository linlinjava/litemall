<template>
  <div class="tab_home">
    <div class="tal_class_searchBox">
      <van-search placeholder="点击前往搜索" @click="$router.push({ name: 'search' })"/>
      <div class="tal_class_searchMask"></div>
    </div>
    <van-swipe :autoplay="3000" indicator-color="white">
      <van-swipe-item v-for="(image, index) in brandList" :key="index">
        <img :src="image" style="height:230px">
      </van-swipe-item>
    </van-swipe>

  <div class="goods-channel">
    <div class="item" 
        @click="changeTabbar(iconJson)"  
        v-for="(iconJson, index) in shopInfos.channel"
        :key="index">
      <img :src="iconJson.iconUrl" background-size="cover"/>
      <span>{{iconJson.name}}</span>
    </div>
  </div>

    <van-panel title="优惠券" style=" padding-bottom: 10px;">
      <div
        class="van-coupon-item"
        v-for="(coupon,index) in shopInfos.couponList"
        :key="index"
        @click="getCoupon(coupon.id)"
      >
        <div class="van-coupon-item__content">
          <div class="van-coupon-item__head">
            <h2>
              <span>¥</span>
              {{coupon.discount}} 元
            </h2>
            <p>{{coupon.desc }} - {{coupon.tag}}</p>
          </div>
          <div class="van-coupon-item__body">
            <h2>{{coupon.name}}</h2>
            <p>有效期：{{coupon.days}} 天</p>
        
          </div>
        </div>
         
      </div>
    </van-panel>

    <van-panel title="团购专区">
      <!-- {{shopInfos.grouponList}} -->
      <van-card
        :thumb-link="goDetail(groupGood.goods.id)"
        v-for="(groupGood ,index) in shopInfos.grouponList"
        :key="index"
        :title="groupGood.goods.name"
        :desc="groupGood.goods.brief"
        :num="groupGood.groupon_member"
        :origin-price="groupGood.goods.counterPrice"
        :price="groupGood.goods.retailPrice +'.00'"
        :thumb="groupGood.goods.picUrl"
        @native-click="goDetail(groupGood.goods.id)"
      >
        <!-- <div slot="footer">添加日期 {{item.addTime}}</div> -->
      </van-card>
    </van-panel>

    <van-panel title="新品首发">
      <!-- {{shopInfos.grouponList}} -->
      <van-row gutter>
        <van-col span="12" v-for="(newGood ,index) in shopInfos.newGoodsList" :key="index">
          <router-link :to="{ path: `/items/detail/${newGood.id}`}">
            <img :src="newGood.picUrl" style="width:180px;height:180px;">
          </router-link>
          <span
            style="padding-left: 20px;position: relative;bottom: 10px; color: rgb(123, 116, 116);white-space: nowrap;"
          >{{newGood.name}}</span>
          <span
            style="padding-left: 80px;position: relative;bottom: 10px; color:#ab956d"
          >￥ {{newGood.retailPrice}}</span>
        </van-col>
      </van-row>
    </van-panel>

    <van-panel title="人气推荐">
      <!-- {{shopInfos.grouponList}} -->
      <van-card
        :thumb-link="goDetail(groupGood.id)"
        v-for="(groupGood ,index) in shopInfos.hotGoodsList"
        :key="index"
        :title="groupGood.name"
        :desc="groupGood.brief"
        :origin-price="groupGood.counterPrice"
        :price="groupGood.retailPrice +'.00'"
        :thumb="groupGood.picUrl"
        @native-click="goDetail(groupGood.id)"
      >
        <!-- <div slot="footer">添加日期 {{item.addTime}}</div> -->
      </van-card>
    </van-panel>
    <!-- <van-list
      v-model="loading"
      class="scroll-load"
      :finished="finished"
      :immediate-check="false"
      :offset="100"
      @load="loadMore"
    >
      <item-group
        v-for="( group, key ) in itemGroup"
        v-if="group"
        :key="key"
        class="interval_bot"
        :setting="group.setting"
      >
        <component
          v-for="item in group.items"
          :goods="item"
          :key="item.id"
          :is="getStyle(group.setting.style)"
          @click="toGoods(item)"
        >
          <div slot="mask" v-if="lootAll(item)">
            <img src="../../assets/images/not_enough.png" alt="已抢光">
          </div>
          <div slot="leftTopIcon" v-if="item.as_status < 2">
            <img :src="mxStatus(item.as_status)" alt="秒杀">
          </div>
        </component>
      </item-group>
    </van-list>-->
  </div>
</template>

<script>
import { HOME_module, ALL_GOODS } from '@/api/shop';
import getLocationParam from '@/utils/location-param';

import mx_be_to from '@/assets/images/mx_be_to.png';
import mx_start from '@/assets/images/mx_start.png';

import SignBoard from './tabbar-home-sign-board';
import ShopInfoGroup from './tabbar-home-shop-info';
import ItemGroup from '@/components/item-group/';
import ItemCardVert from '@/components/item-card-vert/';
import ItemCardHori from '@/components/item-card-hori/';

import loadMore from '@/mixin/list-load-more';
import scrollFixed from '@/mixin/scroll-fixed';
import _ from 'lodash';

const coupon = {
  available: 1,
  discount: 0,
  denominations: 150,
  originCondition: 0,
  reason: '',
  value: 150,
  name: '优惠券名称',
  startAt: 1489104000,
  endAt: 1514592000
};

import {
  List,
  Swipe,
  SwipeItem,
  Tabbar,
  TabbarItem,
  Search,
  Panel,
  CouponCell,
  CouponList,
  Toast,
  Card,
  Row,
  Col
} from 'vant';

export default {
  mixins: [loadMore, scrollFixed],

  data() {
    const shop_id = getLocationParam('shop_id');
    return {
      shop_id,
      brandList: [],
      shopInfos: [],
      shopInfo: null,
      coupons: [coupon],
      itemGroup: {
        mx_goods: null,
        activity_seckill: null,
        shop_recommend: null,
        goods: null
      },
      mx_be_to,
      mx_start,
      isLoading: false
    };
  },

  computed: {
    location() {
      const shopInfo = this.shopInfo;
      const local = {
        name: shopInfo.shop_name,
        lat: shopInfo.lat,
        lng: shopInfo.lng
      };
      return local.lat && local.lng ? local : null;
    }
  },

  created() {
    // this.initViews();
    this.initNewViews();
  },

  methods: {
    goDetail(id) {
      return `#/items/detail/${id}`;
    },
    async getCoupon(id) {
      let errmsg = await this.$reqPost('/wx/coupon/receive', {
        couponId: id
      });
      Toast.success('领取成功');
    },
    async changeTabbar(o) {
      let { data } = await this.$reqGet(
        `/wx/goods/category?id=${o.id}`
      );
      let categoryId = data.data.currentCategory.id;
      this.$router.push({
        path: `items/list?keyword=&itemClass=${categoryId}`
      });
    },
    initViews() {
      this.$reqGet(HOME_module, {
        shop_id: this.shop_id,
        'per-page': this.pages.perPage,
        page: 1
      }).then(res => {
        const { shop_info, page } = res.data.data;
        const {
          mx_goods,
          shop_recommend,
          activity_seckill,
          goods
        } = this.decorate(res.data.data);
        this.shopInfo = shop_info;
        this.itemGroup.mx_goods = mx_goods;
        this.itemGroup.shop_recommend = shop_recommend;
        this.itemGroup.activity_seckill = activity_seckill;
        this.itemGroup.goods = goods;
        this.setPages(page);
      });
    },
    initNewViews() {
      this.$reqGet('/wx/home/index').then(res => {
        this.shopInfos = res.data.data;
        this.brandList = [];
        _.each(res.data.data.brandList, v => {
          this.brandList.push(v.picUrl);
        });
      });
    },

    initData() {
      // return this.$reqGet(ALL_GOODS, {
      //   shop_id: this.shop_id,
      //   'per-page': this.pages.perPage,
      //   page: this.pages.currPage
      // }).then(res => {
      //   const { items, page } = res.data.data;
      //   this.itemGroup.goods && this.itemGroup.goods.items.push(...items);
      //   return page;
      // });
    },

    toGoods(item) {
      // 如果是秒杀商品, 并且已经抢光
      if (this.lootAll(item)) {
        this.$dialog.alert({ message: '该秒杀商品已抢光，看看别的吧！' });
        return;
      }
      this.$router.push({ path: `/items/detail/${item.id}` });
    },

    groupIcon(key) {
      const iconGroup = {
        activity_seckill: 'naozhong',
        goods: 'list',
        mx_goods: 'n4',
        shop_recommend: 'good'
      };
      return iconGroup[key] || '';
    },

    getStyle(style) {
      return style ? 'item-card-vert' : 'item-card-hori';
    },

    decorate({ mx_goods, shop_recommend, activity_seckill, goods }) {
      if (mx_goods) {
        mx_goods.setting.icon = 'n4';
        mx_goods.setting.title_desc = '分享得金豆';
        mx_goods.setting.title_color = '#db3d3c';
        mx_goods.setting.item_len = mx_goods.items.length;
      }
      if (shop_recommend) {
        shop_recommend.setting.icon = 'good';
        shop_recommend.setting.item_len = shop_recommend.items.length;
      }
      if (activity_seckill) {
        activity_seckill.setting.icon = 'naozhong';
        activity_seckill.setting.title_color = '#db3d3c';
        activity_seckill.setting.item_len = activity_seckill.items.length;
      }
      if (goods) {
        goods.setting.icon = 'list';
        goods.setting.item_len = goods.items.length;
      }
      return {
        mx_goods,
        shop_recommend,
        activity_seckill,
        goods
      };
    },

    lootAll(item) {
      return (
        typeof item.as_status !== 'undefined' && item.sold_num == item.total
      );
    },

    mxStatus(as_status) {
      return as_status ? this.mx_start : this.mx_be_to;
    }
  },

  components: {
    // Vue.use(Tabbar).use(TabbarItem);,
    [Row.name]: Row,
    [Col.name]: Col,
    [Card.name]: Card,
    [Toast.name]: Toast,
    [CouponCell.name]: CouponCell,
    [CouponList.name]: CouponList,
    [Search.name]: Search,
    [Panel.name]: Panel,
    [List.name]: List,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [Tabbar.name]: Tabbar,
    [TabbarItem.name]: TabbarItem,
    [SignBoard.name]: SignBoard,
    [ShopInfoGroup.name]: ShopInfoGroup,
    [ItemGroup.name]: ItemGroup,
    [ItemCardVert.name]: ItemCardVert,
    [ItemCardHori.name]: ItemCardHori
  }
};
</script>


<style lang="scss" scoped>
.interval_bot {
  margin-bottom: 10px;
}

.goods-channel {
  background: #fff;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding-bottom: 0px;
  padding-top: 10px;
}

.goods-channel .item {
  width: 50px;
  height: 50px;
  margin-left: 10px;
}

.goods-channel img {
  display: block;
  width: 30px;
  height: 30px;
  margin: 0 auto;
}

.goods-channel span {
  display: block;
  font-size: 15px;
  text-align: center;
  margin: 0 auto;
  line-height: 1;
  color: #333;
}
van-tabbar-item
.van-coupon-cell--selected {
  color: #323233;
}
.van-coupon-list {
  height: 100%;
  position: relative;
  background-color: #f8f8f8;
}
.van-coupon-list__field {
  padding: 7px 15px;
}
.van-coupon-list__exchange {
  height: 32px;
  line-height: 30px;
}
.van-coupon-list__list {
  overflow-y: auto;
  padding: 15px 0;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-overflow-scrolling: touch;
}
.van-coupon-list__close {
  left: 0;
  bottom: 0;
  position: absolute;
  font-weight: 500;
}
.van-coupon-list__empty {
  padding-top: 100px;
  text-align: center;
}
.van-coupon-list__empty p {
  color: #969799;
  margin: 15px 0;
  font-size: 14px;
  line-height: 20px;
}
.van-coupon-list__empty img {
  width: 80px;
  height: 84px;
}
.van-coupon-item {
  overflow: hidden;
  border-radius: 4px;
  margin: 0 15px 15px;
  background-color: #fff;
  -webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
}
.van-coupon-item:active {
  background-color: #e8e8e8;
}
.van-coupon-item__content {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  height: 100px;
  padding: 24px 0 0 15px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  border:1px solid red;

}
.van-coupon-item h2,
.van-coupon-item p {
  margin: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.van-coupon-item h2 {
  height: 34px;
  font-weight: 500;
  line-height: 34px;
}
.van-coupon-item p {
  font-size: 12px;
  line-height: 16px;
  color: #969799;
}
.van-coupon-item__head {
  min-width: 90px;
}
.van-coupon-item__head h2 {
  color: #f44;
  font-size: 24px;
}
.van-coupon-item__head h2 span {
  font-size: 50%;
}
.van-coupon-item__body {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;
  position: relative;
  border-radius: 0 4px 4px 0;
}
.van-coupon-item__body h2 {
  font-size: 16px;
}
.van-coupon-item__corner {
  top: 16px;
  right: 15px;
  position: absolute;
}
.van-coupon-item__corner .van-icon {
  border-color: #f44;
  background-color: #f44;
}
.van-coupon-item__reason {
  padding: 7px 15px;
  border-top: 1px dashed #ebedf0;
  background-color: #fafafa;
}
.van-coupon-item--disabled:active {
  background-color: #fff;
}
.van-coupon-item--disabled .van-coupon-item__content {
  height: 90px;
}
.van-coupon-item--disabled h2,
.van-coupon-item--disabled p,
.van-coupon-item--disabled span {
  color: #969799;
}
</style>
