<template>
  <div class="user_collect">
    <van-list v-model="loading"
              :finished="finished"
              :immediate-check="false"
              finished-text="没有更多了"
              @load="getCollectList">
      <van-card v-for="(item, i) in list"
                :key="i"
                :desc="item.brief"
                :title="item.name"
                :thumb="item.picUrl"
                :price="item.retailPrice"
                :origin-price="item.counterPrice"
                @click="itemClick(item.valueId)">
        <div slot="footer">
          <van-button size="mini"
                      icon="lajitong"
                      @click.stop="cancelCollect($event, i,item)">删除</van-button>
        </div>
      </van-card>
    </van-list>

    <is-empty v-if="list.length === 0">没有商品收藏</is-empty>

  </div>
</template>

<script>
import { collectList, collectAddOrDelete } from '@/api/api';
import IsEmpty from '@/components/is-empty/';
import { Card, Search, List } from 'vant';
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
      this.getCollectList()
    },
    getCollectList() {
      this.page++;
      collectList({ type: 0, page: this.page, limit: this.limit }).then(res => {
        this.list.push(...res.data.data.list);
        this.loading = false;
        this.finished = res.data.data.page >= res.data.data.pages;
      });
    },
    cancelCollect(event, i, item) {
      this.$dialog.confirm({ message: '是否取消收藏该商品' }).then(() => {
        collectAddOrDelete({ valueId: item.valueId, type: 0 }).then(res => {
          this.list.splice(i, 1);
        });
      });
    },
    itemClick(id) {
      this.$router.push(`/items/detail/${id}`);
    }
  },

  components: {
    [Search.name]: Search,
    [IsEmpty.name]: IsEmpty,
    [List.name]: List,
    [Card.name]: Card
  }
};
</script>

<style lang="scss" scoped>
.clear_invalid {
  width: 120px;
  color: $font-color-gray;
  border: 1px solid $font-color-gray;
  margin: 0 auto;
  text-align: center;
  padding: 5px 3px;
  margin-top: 20px;
  border-radius: 3px;
}
</style>
