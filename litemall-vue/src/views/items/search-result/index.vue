<template>
  <div class="item_list">
    <form action="/search" class="fixedTop">
      <van-search placeholder="请输入商品名称" v-model="searchVal" @search="resetInit" showAction/>
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
          v-for="(item) in items"
          :key="item.id"
          :goods="item"
          @click="itemClick(item.id)"
        />
      </item-group>
    </van-list>

    <is-empty v-if="items.length === 0">抱歉,没有找到符合条件商品</is-empty>

    <transition name="fade">
      <van-icon name="arrowupcircle" class="backTop" @click.native="backTop" v-show="showArrow"></van-icon>
    </transition>
  </div>
</template>

<script>
import { GOODS_SEARCH } from '@/api/goods';

import ItemGroup from '@/vue/components/item-group/';
import IsEmpty from '@/vue/components/is-empty/';
import ItemCardHori from '@/vue/components/item-card-hori/';
import { Search, List } from 'vant';
import _ from 'lodash';

import loadMore from '@/vue/mixin/list-load-more';
import scrollFixed from '@/vue/mixin/scroll-fixed';

export default {
  name: 'Item-list',
  props: {
    keyword: {
      type: String,
      default: ''
    }
  },

  mixins: [loadMore, scrollFixed],

  data() {
    return {
      isEmpty: false,
      shop_id: '',
      searchVal: '',
      showArrow: false
    };
  },

  activated() {
    this.searchVal = this.keyword;
    this.resetInit();
    this.eventListen();
  },

  deactivated() {
    document
      .getElementById('app')
      .removeEventListener('scroll', this.scrollShowArrow);
  },

  created() {
    this.initData();
    this.scrollShowArrow = _.throttle(this.scrollShowArrow, 100);
  },

  methods: {
    initData() {
      debugger;
      this.items = [];

      return this.$reqGet(
        `/wx/goods/list`,
        {
          keyword: this.searchVal,
          page: 1,
          size: 100,
          sort: 'name',
          order: 'desc',
          categoryId: 0
        },
        {
          hideLoading: true
        }
      ).then(res => {
        const { goodsList, filterCategoryList } = res.data.data;
        _.each(goodsList, v => {
          v.pic_url = v.picUrl;
        });
        this.items.push(...goodsList);
        // return page;
      });
    },
    eventListen() {
      document
        .getElementById('app')
        .addEventListener('scroll', this.scrollShowArrow);
    },
    scrollShowArrow() {
      this.showArrow = document.getElementById('app').scrollTop > 120;
    },
    backTop() {
      document.getElementById('app').scrollTop = 0;
    },
    itemClick(i) {
      this.$router.push({ name: 'detail', params: { itemId: i } });
    }
  },

  components: {
    [ItemGroup.name]: ItemGroup,
    [ItemCardHori.name]: ItemCardHori,
    [Search.name]: Search,
    [List.name]: List,
    [IsEmpty.name]: IsEmpty
  }
};
</script>

<style lang="scss" scoped>
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s;
}

.item_list {
  background-color: #fff;
  padding-top: 50px;
  box-sizing: border-box;
}
.fixedTop {
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 999;
}

.items_loading {
  margin: 0 auto;
}
.backTop {
  position: fixed;
  right: 20px;
  bottom: 20px;
  font-size: 24px;
}
</style>
