<template>
  <div class="user_collect">
    <form action="/search" class="fixedTop">
      <van-search placeholder="请输入商品名称" v-model="searchVal"/>
    </form>

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

    <is-empty v-if="isEmpty">没有商品收藏</is-empty>

    <!-- <div class="clear_invalid" v-if="items.length" @click="clearInvalid">
      <van-icon name="lajitong"/>清除失效商品
    </div>-->
  </div>
</template>

<script>
import { GOODS_COLLECT_LIST } from '@/api/user';

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
      shop_id: 1,
      items: [],
      searchVal: ''
    };
  },

  created() {
    this.resetInit();
  },

  methods: {
    initData() {
      return this.$reqGet(
        '/wx/collect/list?type=0&page=1&size=100',
        {},
        {
          hideLoading: true
        }
      ).then(res => {
        // debugger;
        const { collectList, page } = res.data.data;
        this.items.push(...collectList);
        return page;
      });
    },
    cancelCollect(event, i, item) {
      this.$dialog.confirm({ message: '是否取消收藏该商品' }).then(() => {
        this.$reqPost(
          '/wx/collect/addordelete',
          { valueId: item.valueId, type: 0 },
          {
            hideLoading: true
          }
        ).then(res => {
          this.items.splice(i, 1);
        });
      });
    },
    clearInvalid() {
      this.$dialog.confirm({ message: '确定清除所有失效商品吗?' });
    },
    itemClick(i, item) {
      // const item_id = this.items[i].item_id;
      // const status = this.items[i].goods_status;
      // status &&
      this.$router.push(`/items/detail/${item.valueId}`);
      // !status && this.$toast('该商品已失效');
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
