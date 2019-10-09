<template>
  <div class="order_list">
    <van-tabs v-model="activeIndex"
              :swipe-threshold="5"
              @click="handleTabClick">
      <van-tab v-for="(tabTitle, index) in tabTitles"
               :title="tabTitle"
               :key="index">
        <van-list v-model="loading"
                  :finished="finished"
                  :immediate-check="false"
                  finished-text="没有更多了"
                  @load="getOrderList">
          <van-panel v-for="(el, i) in orderList"
                     :key="i"
                     :title="'订单编号: ' + el.orderSn"
                     :status="el.orderStatusText"
                     @click.native="toOrderDetail(el.id)">
            <van-card v-for="(goods, goodsI) in el.goodsList"
                      :key="goodsI"
                      :title="goods.goodsName"
                      :num="goods.number"
                      :thumb="goods.picUrl">
              <div slot="desc">
                <div class="desc">
                  <van-tag plain
                           style="margin-right:6px;"
                           v-for="(spec, index) in goods.specifications"
                           :key="index">
                    {{spec}}
                  </van-tag>
                </div>
              </div>
            </van-card>
            <div class="total">合计: {{el.actualPrice * 100 | yuan}}（含运费{{el.post_fee | yuan}}）</div>

            <div slot="footer"
                 class="footer_btn">
              <van-button size="small"
                          v-if="el.handleOption.cancel"
                          @click.stop="cancelOrder(el.id)">取消订单</van-button>
              <van-button size="small"
                          v-if="el.handleOption.pay"
                          type="danger"
                          @click.stop="toPay(el.id)">去支付</van-button>
              <van-button size="small"
                          v-if="el.handleOption.refund"
                          type="danger"
                          @click.stop="refundOrder(el.id)">退款</van-button>
              <van-button size="small"
                          v-if="el.handleOption.confirm"
                          type="danger"
                          @click.stop="confirmOrder(el.id)">确认收货</van-button>
              <van-button size="small"
                          v-if="el.handleOption.delete"
                          @click.stop="delOrder(el.id)">删除订单</van-button>
              <van-button size="small"
                          v-if="el.handleOption.comment"
                          @click.stop="commentOrder(el.id)">去评价</van-button>
            </div>

          </van-panel>

        </van-list>

      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { orderList, orderDelete, orderConfirm, orderCancel, orderRefund } from '@/api/api';
import _ from 'lodash';
import { Tab, Tabs, Panel, Card, List, Tag } from 'vant';

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
      activeIndex: Number(this.active),
      tabTitles: ['全部', '待付款', '待发货', '待收货', '待评价'],
      orderList: [],
      page: 0,
      limit: 10,
      loading: false,
      finished: false
    };
  },

  methods: {
    init() {
      this.page = 0;
      this.orderList = [];
      this.getOrderList();
    },
    getOrderList() {
      this.page++;
      orderList({
        showType: this.activeIndex,
        page: this.page,
        limit: this.limit
      }).then(res => {
        this.orderList.push(...res.data.data.list);
        this.loading = false;
        this.finished = res.data.data.page >= res.data.data.pages;
      });
    },
    delOrder(id) {
      let that = this;
      this.$dialog
        .confirm({ message: '确定要删除该订单吗?' })
        .then(() => {
          orderDelete({ orderId: id }).then(() => {
            this.init();
            this.$toast('已删除订单');
          });
        })
        .catch(() => {});
    },
    cancelOrder(id) {
      this.$dialog
        .confirm({ message: '确定要取消该订单吗?' })
        .then(() => {
          orderCancel({ orderId: id }).then(() => {
            this.init();
            this.$toast('已取消该订单');
          });
        })
        .catch(() => {});
    },
    refundOrder(id) {
      this.$dialog
        .confirm({ message: '确定要申请退款吗?' })
        .then(() => {
          orderRefund({ orderId: id }).then(() => {
            this.init();
            this.$toast('已申请订单退款');
          });
        })
        .catch(() => {});
    },    
    confirmOrder(id) {
      this.$dialog
        .confirm({
          message: '请确认收到货物, 确认收货后无法撤销!'
        })
        .then(() => {
          orderConfirm({ orderId: id }).then(() => {
            this.init();
            this.$toast('已确认收货');
          });
        })
        .catch(() => {});
    },
    commentOrder(id) {},
    toPay(id) {
      this.$router.push({ name: 'payment', params: { orderId: id } });
    },
    handleTabClick() {
      this.page = 0;
      this.orderList = [];
      this.getOrderList();
    },
    toOrderDetail(id) {
      this.$router.push({
        path: '/order/order-detail',
        query: { orderId: id }
      });
    }
  },
  components: {
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
  .van-panel {
    margin-top: 20px;
  }

  .van-card {
    background-color: #fff;
  }

  .total {
    text-align: right;
    padding: 10px;
  }

  .footer_btn {
    text-align: right;
    .van-button {
      margin-left: 10px;
    }
  }
}
</style>
