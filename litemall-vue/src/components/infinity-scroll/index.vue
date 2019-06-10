<template>
  <van-list
    v-model="loading"
    :finished="finished"
    :offset="100"
    @load="loadMore"
    v-bind="$attrs"
    v-on="$listeners"
    :immediate-check="false"
  >
    <is-empty v-if="isEmpty">{{ emptyText }}</is-empty>
    <slot v-else></slot>
    <div v-if="finished" class="text-center nomore">{{ onMoreText }}</div>
  </van-list>
</template>

<script>
import { List } from 'vant';
import { get } from 'lodash';
import IsEmpty from '@/components/is-empty';
import loadMore from '@/mixin/load-more';
import { getList } from '@/api/api';

const DEFAULT_CONFIG = {
  params: {},
  headers: {}
};

export default {
  name: 'infinity-scroll',

  mixins: [loadMore],

  props: {
    apiUrl: {
      type: String,
      required: true
    },
    resKey: {
      type: String,
      default: 'data.goodsList'
    },
    pageKey: {
      type: String,
      default: 'data.page'
    },
    emptyText: {
      type: String,
      default: '抱歉,找不到结果~'
    },
    onMoreText: {
      type: String,
      default: '没有更多了~'
    },
    perPage: Number,
    beforeRequest: Function
  },

  created() {
    this.resetInit();
  },

  methods: {
    beforeInitData() {
      return this.beforeRequest ? this.beforeRequest() : DEFAULT_CONFIG;
    },
    initData() {
      const { params = {}, headers = {} } = this.beforeInitData();
      const prePage = this.perPage || this.pages.perPage;
      console.log(params);
      console.log(headers);
      getList(this.apiUrl, {
          page: this.pages.currPage,
          limit: prePage,
          ...params
        },
        headers
      ).then(res => {
        const items = get(res.data.data, this.resKey, []);
        const page = get(res.data.data, this.pageKey, null);
        this.$emit('onLoad', items);
        return page;
      });
    },
    sleep(time) {
      return new Promise(resolve => {
        setTimeout(resolve, time);
      });
    }
  },

  components: {
    IsEmpty,
    [List.name]: List
  }
};
</script>

<style lang="scss" scoped>
.nomore {
  padding: 10px 0;
  color: $font-color-gray;
}
</style>
