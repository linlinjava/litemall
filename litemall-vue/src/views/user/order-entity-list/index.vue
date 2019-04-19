<template>
  <div class="order_list over-hide">
    <van-tabs v-model="activeIndex" :swipe-threshold="5" @click="handleTabClick">
      <van-tab
        v-for="(tab, tabIndex) in tabsItem"
        :title="tab.name"
        @click="changeTab"
        :key="tab.type"
      >
        <InfinityScroll
          class="full-page scroll-wrap height-fix42"
          :beforeRequest="beforeRequest"
          :apiUrl="listApi"
          @onLoad="onLoad(tabIndex, $event)"
        >
          <van-panel
            v-for="(el, i) in orderArr"
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
                :desc="goods.goodsName"
                :num="goods.number"
                :thumb="goods.picUrl"
                @click.native="toOrderDetail(el.id)"
              />
              <div
                class="order_list--total"
              >合计: {{el.actualPrice * 100 | yuan}}（含运费{{el.post_fee | yuan}}）</div>
            </div>
            <component
              slot="footer"
              :is="'status' + el.status"
              :reminder="el.is_can_reminder"
              @handle="actionHandle($event, i)"
            />
          </van-panel>
        </InfinityScroll>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { ORDER_LIST } from '@/api/order';

import { Tab, Tabs, Panel, Card, List } from 'vant';
import status10 from './handle-status-10';
import status20 from './handle-status-20';
import status25 from './handle-status-25';
import status30 from './handle-status-30';
import status40 from './handle-status-40';
import status50 from './handle-status-50';
import status60 from './handle-status-60';
import status70 from './handle-status-70';

import InfinityScroll from '@/vue/components/infinity-scroll';

const STATUS_TEXT = {
  10: '待付款',
  20: '待发货',
  25: '部分发货',
  30: '待收货',
  40: '已完成',
  50: '退款成功',
  60: '交易关闭',
  70: '交易关闭'
};

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
    const activeIndex = this.active;
    return {
      listApi: ORDER_LIST,
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
          name: '待发货',
          status: 20,
          items: []
        },
        {
          name: '待收货',
          status: 30,
          items: []
        },
        {
          name: '已完成',
          status: 40,
          items: []
        }
      ],
      orderArr: []
    };
  },

  methods: {
    async init(i) {
      let { data } = await this.$reqGet(
        `/wx/order/list?showType=${i || this.active || '0'}`
      );
      this.orderArr = data.data.data;
    },
    onLoad(i, items) {
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
    async delOrder(i) {
      await this.$dialog.confirm({ message: '确定要删除该订单吗?' });
      this.items.splice(i, 1);
      this.$toast('已删除该订单');
    },
    async cancelOrder(i) {
      await this.$dialog.confirm({ message: '确定要取消该订单吗?' });
      if (this.activeIndex == 0) {
        this.items[i].status = 60;
      } else {
        this.items.splice(i, 1);
      }
      this.$toast('已取消该订单');
    },
    async receiptOrder(i) {
      await this.$dialog.confirm({
        message: '请确认收到货物, 确认收货后无法撤销!'
      });
      this.items[i].status = 40;
      this.$toast('已确认收货');
    },
    reminderOrder(i) {
      this.items[i].is_can_reminder = false;
      this.$toast('已提醒卖家发货, 请耐心等待哦~');
    },
    toPay(i) {
      const id = this.items[i].id;
      this.$router.push({ name: 'payment', params: { order_id: id } });
    },
    handleTabClick(index) {
      console.log(6666666);
      // console.log(index);
      // await this.init(i);
      this.$router.replace({
        name: 'user-order-list',
        params: { active: index }
      });
    },
    getStatusText(status) {
      return STATUS_TEXT[status] || '';
    },
    toOrderDetail(id) {
      this.$router.push({
        name: `orderDetail`,
        query: { cartId: 0, orderId: id }
      });
    },
    actionHandle(handle, i) {
      this[handle] && this[handle](i);
    }
  },
  watch: {
    active(val) {
      this.init(val);
      val && !this.address_list.length && this.getAddress();
    }
  },
  components: {
    InfinityScroll,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Panel.name]: Panel,
    [Card.name]: Card,
    [List.name]: List,
    status10,
    status20,
    status25,
    status30,
    status40,
    status50,
    status60,
    status70
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
