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

    <van-card v-for="(item, i) in brand.list"
              :key="i"
              :desc="item.brief"
              :title="item.name"
              :thumb="item.picUrl"
              :price="item.retailPrice"
              :origin-price="item.counterPrice"
              @click="itemClick(item.id)">
    </van-card>

  </div>
</template>

<script>
import { brandDetail } from '@/api/api';
import { Card } from 'vant';

export default {
  props: {
    brandId: [String, Number]
  },
  data() {
    return {
      brand: {}
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
    },
    itemClick(id) {
      this.$router.push(`/items/detail/${id}`);
    }
  },

  components: {
    [Card.name]: Card
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