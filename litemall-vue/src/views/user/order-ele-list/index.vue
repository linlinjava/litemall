<template>
  <div class="order_list no-pad-bottom over-hide">
    <van-tabs v-model="activeIndex" :swipe-threshold="5" @click="handleTabClick">
      <van-tab v-for="(tab, tabIndex) in tabsItem" :title="tab.name" :key="tab.type">
        <InfinityScroll
          class="full-page scroll-wrap height-fix42"
          :beforeRequest="beforeRequest"
          :apiUrl="listApi"
          @onLoad="onLoad(tabIndex, $event)"
        >
          <van-panel
            v-for="(el, i) in tab.items"
            class="order_list--panel"
            :key="el.id"
            :title="'订单编号: ' + el.id"
            :status="getStatusText(el.status)"
          >
            <div>
              <van-card
                v-for="(goods, i) in el.serviceItems"
                class="order_list--van-card"
                :key="i"
                :title="goods.item_name"
                :desc="goods.sku_props_str"
                :num="10000"
                :price="(goods.price / 100).toFixed(2)"
                :thumb="goods.pic_url"
              />
              <div class="order_list--total">合计: {{el.total_fee | yuan}}（含运费{{el.post_fee | yuan}}）</div>
            </div>
            <component
              slot="footer"
              :is="'status' + el.status"
              @delete-order="delOrder(i)"
              @pay="toPay(el.id)"
              @cancel="cancelOrder(i)"
            />
          </van-panel>
        </InfinityScroll>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { ELE_COUPON_LIST } from '@/api/order';

import { Tab, Tabs, Panel, Card, List } from 'vant';
import status10 from './handle-status-10';
import status40 from './handle-status-40';
import status60 from './handle-status-60';
import status70 from './handle-status-70';
import status100 from './handle-status-100';
import status110 from './handle-status-110';
import status120 from './handle-status-120';
import _ from 'lodash';

import InfinityScroll from '@/vue/components/infinity-scroll';

const STATUS_TEXT = {
  10: '待付款',
  40: '已完成',
  60: '已关闭',
  70: '已关闭',
  100: '未使用',
  110: '已使用',
  120: '已退款'
};

export default {
  name: 'order-list',

  props: {
    active: {
      type: [String, Number],
      default: 0
    }
  },

  data() {
    const activeIndex = this.active;
    return {
      listApi: ELE_COUPON_LIST,
      shop_id: 1,
      activeIndex,
      tabsItem: [
        {
          name: '全部',
          status: 0,
          items: []
        },
        {
          name: '待付款',
          status: 10,
          items: []
        },
        {
          name: '待使用',
          status: 100,
          items: []
        },
        {
          name: '已使用',
          status: 110,
          items: []
        },
        {
          name: '退款成功',
          status: 120,
          items: []
        }
      ]
    };
  },

  methods: {
    onLoad(i, items) {
      new Array(10).fill(1).forEach(() => {
        items.push(_.cloneDeep(_.last(items)));
      });
      this.tabsItem[i].items.push(...items);
    },
    beforeRequest() {
      const i = this.activeIndex;
      const status = this.tabsItem[i].status;
      const { shop_id } = this;
      return {
        params: {
          status,
          shop_id
        }
      };
    },
    delOrder(i) {
      this.$dialog.confirm({ message: '确定删除订单?' }).then(() => {
        this.items.splice(i, 1);
        this.$toast('已删除');
      });
    },
    async cancelOrder(i) {
      this.$dialog
        .confirm({ message: '确定要取消该订单吗?' })
        .then(() => {
          this.items.splice(i, 1);
          this.$toast('已取消该订单');
        })
        .catch(() => {});
    },
    toPay(id) {
      this.$router.push({ name: 'payment', params: { order_id: id } });
    },
    handleTabClick(index) {
      this.$router.replace({
        name: 'user-order-ele-list',
        params: { status: index }
      });
    },
    getStatusText(status) {
      return STATUS_TEXT[status] || '';
    }
  },
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Panel.name]: Panel,
    [Card.name]: Card,
    [List.name]: List,
    InfinityScroll,
    status10,
    status40,
    status60,
    status70,
    status100,
    status110,
    status120
  }
};
</script>

<style lang="scss" scoped>
.order_list {
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
