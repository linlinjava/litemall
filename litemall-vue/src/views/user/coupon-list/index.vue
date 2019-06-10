<template>
  <div class="order_list no-pad-bottom over-hide">
    <van-tabs v-model="activeIndex" :swipe-threshold="5"  @click="handleTabClick">
      <van-tab v-for="(tabTitle, tabIndex) in tabTitles" :title="tabTitle" :key="tabIndex">
        <!-- <InfinityScroll
          class="full-page scroll-wrap height-fix42"
          :beforeRequest="beforeRequest"
          :apiUrl="listApi"
          @onLoad="onLoad(tabIndex, $event)"
        > -->
    
       <van-panel style=" padding-bottom: 10px;">
      <div class="van-coupon-item" v-for="(coupon,index) in couponList" :key="index">
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
            <p>有效期: 至 {{coupon.endTime}}</p>
        
          </div>
        </div>
         
      </div>
    </van-panel>
        <!-- </InfinityScroll> -->
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { CouponMyList, couponMyList } from '@/api/api';

import { Tab, Tabs, Panel, Card, List, CouponCell, CouponList } from 'vant';
import _ from 'lodash';

import InfinityScroll from '@/components/infinity-scroll';


export default {
  name: 'coupon-list',

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
      listApi: CouponMyList,
      activeIndex: this.active,
      tabTitles: [
        '未使用',
        '已使用',
        '已过期'
      ],
      status: 0,
      couponList: []
    };
  },

  methods: {
    init(i) {
      let status = i || this.activeIndex;
      couponMyList({status: status}).then(res => {
        this.couponList = res.data.data.list;
      });
    },
    handleTabClick(index) {
      this.init(index);
    },    
  },
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Panel.name]: Panel,
    [Card.name]: Card,
    [List.name]: List,
    InfinityScroll,
    CouponCell,
    CouponList,
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
  margin-left:20px;
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
