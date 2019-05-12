<template>
  <div class="order_list over-hide">
    <van-tabs v-model="activeIndex" :swipe-threshold="5" @click="handleTabClick">
      <van-tab v-for="(tabTitle, index) in tabTitles" :title="tabTitle" :key="index">
        <!-- <InfinityScroll
          class="full-page scroll-wrap height-fix42"
          :beforeRequest="beforeRequest"
          :apiUrl="listApi"
          @onLoad="onLoad(tabIndex, $event)"
        > -->
          <van-panel
            v-for="(el, i) in orderList"
            class="order_list--panel"
            :key="i"
            :title="'订单编号: ' + el.orderSn"
            :status="el.orderStatusText"
          >
            <div>
              <van-card
                v-for="(goods, goodsI) in el.goodsList"
                class="order_list--van-card"
                :key="goodsI"
                :title="goods.goodsName"
                :num="goods.number"
                :thumb="goods.picUrl"
                @click.native="toOrderDetail(el.id)"
              >
              <div slot="desc">
                <div class="van-card__desc">
                  <van-tag plain style="margin-right:6px;" v-for="(spec, index) in goods.specifications" :key="index">
                  {{spec}}
                  </van-tag>
                </div>
              </div>
            </van-card>              
              <div
                class="order_list--total"
              >合计: {{el.actualPrice * 100 | yuan}}（含运费{{el.post_fee | yuan}}）</div>
            </div>

          	<div slot="footer" class="order_list--footer_btn">
              <van-button size="small" v-if="el.handleOption.cencel" @click="cancelOrder(el.id)">取消订单</van-button>
	            <van-button size="small" v-if="el.handleOption.pay" type="danger" @click="toPay(el.id)">去支付</van-button>
		          <van-button size="small" v-if="el.handleOption.confirm" type="danger" @click="confirmOrder(el.id)">确认收货</van-button>
              <van-button size="small" v-if="el.handleOption.delete" @click="delOrder(el.id)">删除订单</van-button>
              <van-button size="small" v-if="el.handleOption.comment" @click="commentOrder(el.id)">去评价</van-button>
	          </div>

          </van-panel>

        <!-- </InfinityScroll> -->

      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { OrderList, orderList } from '@/api/api';

import { Tab, Tabs, Panel, Card, List, Tag } from 'vant';
import InfinityScroll from '@/components/infinity-scroll';

export default {
  name: 'order-list',

  props: {
    active: {
      type: [String, Number],
      default: 0
    }
  },
  created() {
    this.init();
  },
  data() {
    return {      
      listApi: OrderList,
      activeIndex: this.active,
      tabTitles: [
        '全部',
        '待付款',
        '待发货',
        '待收货',
        '待评价'
      ],
      orderList: []
    };
  },

  methods: {
    init(i) {
      let showType = i || this.activeIndex;
      orderList({showType: showType}).then(res => {
        this.orderList = res.data.data.list;
      });
    },
    delOrder(i) {
      this.$dialog.confirm({ message: '确定要删除该订单吗?' });
      this.items.splice(i, 1);
      this.$toast('已删除该订单');
    },
    cancelOrder(id) {
      this.$dialog.confirm({ message: '确定要取消该订单吗?' });
      this.$toast('已取消该订单');
    },
    confirmOrder(id) {
      this.$dialog.confirm({
        message: '请确认收到货物, 确认收货后无法撤销!'
      });
      this.$toast('已确认收货');
    },
    commentOrder(id) {
    },
    toPay(id) {
      this.$router.push({ name: 'payment', params: { orderId: id } });
    },
    handleTabClick(index) {
      this.init(index);
    },
    toOrderDetail(id) {
      this.$router.push({
        name: 'orderDetail',
        query: { orderId: id }
      });
    }
  },
  components: {
    InfinityScroll,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Panel.name]: Panel,
    [Card.name]: Card,
    [List.name]: List,
    [Tag.name]: Tag
  }
};
</script>

<style lang="scss" scoped>
.order_list {
  padding-bottom: 0;
  overflow-y: hidden;
  &--footer_btn {
    text-align: right;
  }
  &--panel {
    margin-bottom: 10px;
  }

  &--van-card {
    background-color: #fafafa;
  }

  &--total {
    text-align: right;
    padding: 10px;
  }
}
</style>
