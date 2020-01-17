<template>
  <div class="goods_hot">
    <div class="banner">
      <div class="title">大家都在买</div>
    </div>

    <van-list v-model="loading"
              :finished="finished"
              :immediate-check="false"
              finished-text="没有更多了"
              @load="getHotList">
      <van-card v-for="(item, i) in list"
                :key="i"
                :desc="item.brief"
                :title="item.name"
                :thumb="item.picUrl"
                :price="item.retailPrice"
                :origin-price="item.counterPrice"
                @click="itemClick(item.id)">
      </van-card>
    </van-list>

  </div>
</template>

<script>
import { goodsList } from '@/api/api';
import { Card, List } from 'vant';
import scrollFixed from '@/mixin/scroll-fixed';

export default {
  mixins: [scrollFixed],

  data() {
    return {
      list: [],
      page: 0,
      limit: 10,
      loading: false,
      finished: false
    };
  },

  created() {
    this.init();
  },

  methods: {
    init() {
      this.page = 0;
      this.list = [];
      this.getHotList();
    },
    getHotList() {
      this.page++;
      goodsList({
        isHot: true,
        page: this.page,
        limit: this.limit
      }).then(res => {
        this.list.push(...res.data.data.list);
        this.loading = false;
        this.finished = res.data.data.page >= res.data.data.pages;
      });
    },
    itemClick(id) {
      this.$router.push(`/items/detail/${id}`);
    }
  },

  components: {
    [List.name]: List,
    [Card.name]: Card
  }
};
</script>

<style lang="scss" scoped>
.goods_hot {
  padding: 20px;
  .banner {
    height: 250px;
    background-image: url('http://yanxuan.nosdn.127.net/8976116db321744084774643a933c5ce.png');
    background-size: cover;
    margin-bottom: 20px;
    .title {
      text-align: center;
      line-height: 200px;
      color: #ffffff;
      font-size: 40px;
    }
  }
}
</style>