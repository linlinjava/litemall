<template>
  <div class="tab_class">
    <div class="tal_class_searchBox">
      <van-search placeholder="点击前往搜索"/>
      <div class="tal_class_searchMask" @click="$router.push({ name: 'search' })"></div>
    </div>
    <class-tree
      ref="classTree"
      class="height-fix42"
      @nav-click="changeCatalog"
      @class-click="toItemList"
      @all-click="toItemList"
      :list="list"
    ></class-tree>

    <is-empty v-if="isEmpty">抱歉,店主还未上架商品</is-empty>
  </div>
</template>

<script>
import { catalogList } from '@/api/api';

import getLocationParam from '@/utils/location-param';
import { Search } from 'vant';
import classTree from './tabbar-class-tree';
import IsEmpty from '@/components/is-empty';
import _ from 'lodash';
import { async } from 'q';

function getIndex(arr, keyWord) {
  let index = 0;
  _.each(arr, (v, k) => {
    if (v.id === keyWord) {
      index = k;
      return false;
    }
  });
  return index;
}
export default {
  data() {
    return {
      list: [],
      subCategory: [],
      isEmpty: false
    };
  },

  created() {
    this.initData();
  },

  methods: {
    initData() {
      catalogList().then(res => {
        this.list = res.data.data.categoryList;
        this.$refs.classTree.changeList(res.data.data);
        this.subCategory = res.data.data.currentSubCategory;
        if (this.subCategory.length === 0) this.isEmpty = true;
      });
    },
    removeNoChild(data) {
      return data.filter(item => item.children && item.children.length);
    },
    changeCatalog(id) {
      catalogList({ id: id}).then(res => {
        let index = getIndex(this.list, res.data.data.currentCategory.id);
        this.$refs.classTree.changeList(res.data.data);
        this.subCategory = res.data.data.currentSubCategory;
        if (this.subCategory.length === 0) this.isEmpty = true;
      });
    },
    toItemList(id) {
      this.$router.push({
        name: 'list',
        query: { keyword: '', itemClass: id }
      });
    }
  },
  components: {
    [Search.name]: Search,
    [classTree.name]: classTree,
    [IsEmpty.name]: IsEmpty
  }
};
</script>


<style scoped>
.tab_class {
  overflow: hidden;
  background-color: #fff;
}

.height-fix {
  padding-bottom: 42px;
}

.tal_class_searchBox {
  position: relative;
}

.tal_class_searchMask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9;
}
</style>
