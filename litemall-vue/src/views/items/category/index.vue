<template>
  <div class="item_list">
    <van-tabs v-model="navActive"
              @click="handleTabClick">
      <van-tab v-for="(nav, index) in navList"
               :title="nav.name"
               :key="index">
        <van-list v-model="loading"
                  :finished="finished"
                  :immediate-check="false"
                  finished-text="没有更多了"
                  @load="getGoodsList">
          <div class="h">
            <div class="name">{{currentCategory.name}}</div>
            <div class="desc">{{currentCategory.desc}}</div>
          </div>
          <van-card v-for="(item, i) in goodsList"
                    :key="i"
                    :desc="item.brief"
                    :title="item.name"
                    :thumb="item.picUrl"
                    :price="item.retailPrice"
                    :origin-price="item.counterPrice"
                    @click="itemClick(item.id)" />
        </van-list>

      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { goodsCategory, goodsList } from '@/api/api';
import { Card, List, Tab, Tabs } from 'vant';

export default {
  name: 'Item-list',
  props: {
    itemClass: {
      type: [String, Number],
      default: ''
    }
  },

  data() {
    return {
      categoryId: this.itemClass,
      goodsList: [],
      page: 0,
      limit: 10,
      currentCategory: {},
      navList: [],
      navActive: 0,
      loading: false,
      finished: false
    };
  },

  created() {
    this.init();
  },

  methods: {
    handleTabClick(index) {
      this.categoryId = this.navList[index].id;
      this.$router.replace({
        name: 'category',
        query: { itemClass: this.categoryId }
      });
      this.init();
    },
    init() {
      goodsCategory({ id: this.categoryId }).then(res => {
        this.navList = res.data.data.brotherCategory;
        this.currentCategory = res.data.data.currentCategory;

        // 当id是L1分类id时，这里需要重新设置成L1分类的一个子分类的id
        if (res.data.data.parentCategory.id == this.categoryId) {
          this.categoryId = res.data.data.currentCategory.id;
        }

        for (let i = 0; i < this.navList.length; i++) {
          if (this.navList[i].id == this.categoryId) {
            this.navActive = i;
            break;
          }
        }
        this.page = 0;
        this.goodsList = [];
        this.getGoodsList();
      });
    },
    getGoodsList() {
      this.page++;
      goodsList({
        categoryId: this.categoryId,
        page: this.page,
        limit: this.limit
      }).then(res => {
        this.goodsList.push(...res.data.data.list);
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
    [Card.name]: Card,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs
  }
};
</script>

<style lang="scss" scoped>
.item_list {
  background-color: #fff;
}

.h {
  height: 100px;
  width: 100%;
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.h .name {
  display: block;
  height: 30px;
  margin-bottom: 10px;
  font-size: 20px;
  color: #333;
}

.h .desc {
  display: block;
  height: 24px;
  font-size: 16px;
  color: #999;
}
</style>
