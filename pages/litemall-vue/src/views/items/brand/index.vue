<template>
  <div class="goods_brand">
    <div class="brand-info">
      <div class="name">
        <img class="img"
             :src="brand.picUrl"
             background-size="cover" />
        <div class="info-box">
          <div class="txt">{{brand.name}}</div>
          <div class="line"></div>
        </div>
      </div>
      <div class="desc">
        {{brand.desc}}
      </div>
    </div>

    <van-row gutter>
      <van-col span="12"
               v-for="(goods ,index) in brandGoods"
               :key="index">
        <router-link :to="{ path: `/items/detail/${goods.id}`}">
          <img :src="goods.picUrl"
               style="width:150px;height:150px;">
        </router-link>
        <div style="margin-left: 20px; rgb(123, 116, 116);">{{goods.name}}</div>
        <div style="margin-left: 20px; color:#ab956d">￥ {{goods.retailPrice}}</div>
      </van-col>
    </van-row>

  </div>
</template>

<script>
import { brandDetail, goodsList } from '@/api/api';
import { Card, Row, Col } from 'vant';

export default {
  props: {
    brandId: [String, Number]
  },
  data() {
    return {
      brand: {},
      brandGoods: []
    };
  },

  created() {
    this.init();
  },

  methods: {
    init() {
      brandDetail({
        id: this.brandId
      }).then(res => {
        this.brand = res.data.data;
      });

      goodsList({
        brandId: this.brandId
      }).then(res => {
        this.brandGoods = res.data.data.list;
      });
    },
    itemClick(id) {
      this.$router.push(`/items/detail/${id}`);
    }
  },

  components: {
    [Card.name]: Card,
    [Row.name]: Row,
    [Col.name]: Col
  }
};
</script>

<style lang="scss" scoped>
.goods_brand {
  .brand-info {
    .name {
      width: 100%;
      height: 180px;
      position: relative;

      .img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 180px;
      }

      .info-box {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 180px;
        text-align: center;
        display: flex;
        justify-content: center;
        align-items: center;
        display: block;

        .txt {
          display: block;
          margin-top: 60px;
          height: 35px;
          font-size: 35px;
          color: #fff;
        }

        .line {
          margin: 0 auto;
          margin-top: 16px;
          display: block;
          height: 2px;
          width: 145px;
          background: #fff;
        }
      }
    }
    .desc {
      background: #fff;
      width: 100%;
      height: auto;
      overflow: hidden;
      padding: 25px 20px;
      font-size: 20px;
      color: #666;
      line-height: 20px;
      text-align: center;
    }
  }
}
</style>