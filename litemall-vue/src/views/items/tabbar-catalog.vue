<template>
  <div class="tab_class">
    <div class="tal_class_searchBox">
      <van-search placeholder="点击前往搜索"/>
      <div class="tal_class_searchMask" @click="$router.push({ name: 'search' })"></div>
    </div>

    <div class="class_tree clearfix">
    <ul class="class_tree_nav">
      <li
        v-for="(item, index) in categoryList"
        :key="index"
        :class="{active_nav: currentCategory.id == item.id}"
        @click="changeCatalog(item.id)"
      >{{item.name}}</li>
    </ul>
    <div class="class_tree_content">
      <div class="class_tree_all">
        <img style="width:250px" v-lazy="currentCategory.picUrl">
      </div>
      <div class="box">
        <span>{{currentCategory.desc}}</span>
      </div>
      <div class="class_tree_items_wrap clearfix">
        <div @click="toItemList(item.id)" :key="i" v-for="(item, i) in currentSubCategoryList">
          <div class="class_tree_item_img">
            <img :src="item.picUrl" :alt="item.name">
          </div>
          <div class="class_tree_item_name">{{item.name}}</div>
        </div>
      </div>
    </div>
  </div>

  </div>
</template>

<script>
import { catalogList, catalogCurrent } from '@/api/api';

import { Search } from 'vant';

export default {
  data() {
    return {
      categoryList: [],
      currentCategory: {},
      currentSubCategoryList: []
    };
  },

  created() {
    this.initData();
  },

  methods: {
    initData() {
      catalogList().then(res => {
        let data = res.data.data;
        this.categoryList = data.categoryList;
        this.currentCategory = res.data.data.currentCategory;
        this.currentSubCategoryList = data.currentSubCategory;
      });
    },
    changeCatalog(id) {
      catalogCurrent({ id: id}).then(res => {
        let data = res.data.data;
        this.currentCategory = data.currentCategory;        
        this.currentSubCategoryList = data.currentSubCategory;
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
    [Search.name]: Search
  }
};
</script>


<style lang="scss" scoped>
@import '../../assets/scss/mixin';

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


.box {
  width: 250px;
  height: 20px;
  text-align: center;
  font-family: PingFangSC-Light, helvetica, 'Heiti SC';
  font-size: 13px;
  position: absolute;
  top: 95px;
}
.box span {
  line-height: 20px;
}
.class_tree {
  position: relative;
  background-color: #fff;
  overflow-x: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  height: 100%;
  box-sizing: border-box;
}
.class_tree_nav {
  float: left;
  width: 100px;
  height: 100%;
  background-color: #fff;
  overflow: scroll;
  > li {
    @include one-border;
    height: 40px;
    line-height: 40px;
    text-align: center;
    border-left: 2px solid $bg-color;
  }
  > li.active_nav {
    background-color: #fff;
    border-left: 2px solid $red;
    color: $red;
  }  
}
.class_tree_content {
  margin-left: 100px;
  height: 100%;
  overflow-x: hidden;
  overflow-y: scroll;
  .class_tree_all {
    text-align: right;
    padding-right: 10px;
    height: 40px;
    line-height: 40px;
    color: $font-color-gray;
    font-size: $font-size-small;
  }
  .van-icon-arrow {
    font-size: $font-size-small;
  }
  .class_tree_items_wrap {
    padding: 10px 20px;
    margin-right: -3%;
    margin-top: 70px;
    text-align: center;
    > div {
      float: left;
      padding-right: 3%;
      box-sizing: border-box;
      width: 33.333%;
      margin-bottom: 20px;
    }
    img {
      max-width: 100%;
    }

    .class_tree_item_img {
      display: inline-block;
      max-width: 100%;
      width: 70px;
      height: 70px;
    }
    .class_tree_item_name {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
