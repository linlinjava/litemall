<template>
  <div class="user_collect">
    <van-list
      v-model="loading"
      :finished="finished"
      :immediate-check="false"
      :offset="100"
      @load="loadMore"
    >
      <item-group>
        <item-card-hori
          v-for="(item, i) in items"
          :style="{backgroundColor: !item.goods_status && '#fcfcfc'}"
          :key="i"
          :goods="item"
          @click="itemClick(i,item)"
        >
          <van-icon
            name="lajitong"
            slot="footer"
            @click.stop="cancelCollect($event, i,item)"
            style="float: right;"
          />
        </item-card-hori>
      </item-group>
    </van-list>

    <is-empty v-if="items.length === 0">没有商品收藏</is-empty>

  </div>
</template>

<script>
import { collectList, collectAddOrDelete } from '@/api/api';

import ItemGroup from '@/components/item-group/';
import ItemCardHori from '@/components/item-card-hori/';
import IsEmpty from '@/components/is-empty/';
import { Search, List } from 'vant';

import loadMore from '@/mixin/list-load-more';
import scrollFixed from '@/mixin/scroll-fixed';

export default {
  mixins: [loadMore, scrollFixed],

  data() {
    return {
      page: 1,
      limit: 10,
      total: 0,
      items: []
    };
  },

  created() {
    this.init();
  },

  methods: {
    init() {
      collectList({type:0, page:this.page, limit:this.limit}).then(res => {
        this.page = res.data.data.page;
        this.limit = res.data.data.limit;
        this.total = res.data.data.total;
        this.items.push(...res.data.data.list);
      });
    },
    cancelCollect(event, i, item) {
      this.$dialog.confirm({ message: '是否取消收藏该商品' }).then(() => {
        collectAddOrDelete({ valueId: item.valueId, type: 0 }).then(res => {
          this.items.splice(i, 1);
        });
      });
    },
    itemClick(i, item) {
      this.$router.push(`/items/detail/${item.valueId}`);
    }
  },

  components: {
    [ItemGroup.name]: ItemGroup,
    [ItemCardHori.name]: ItemCardHori,
    [Search.name]: Search,
    [IsEmpty.name]: IsEmpty,
    [List.name]: List
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
