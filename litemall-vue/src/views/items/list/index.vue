<template>
  <div class="item_list over-hide">
    <van-tabs v-model="navActive" @click="handleTabClick">
      <van-tab v-for="(nav, index) in navList" :title="nav.name" :key="index">
        <!-- <InfinityScroll
          :ref="'tabScrolls' + tabIndex"
          class="full-page scroll-wrap fix-height"
          :beforeRequest="beforeRequest"
          :apiUrl="listApi"
          @onLoad="onLoad(tabIndex, $event)"
        > -->
        <div class="h">
          <div class="name">{{currentCategory.name}}</div>
          <div class="desc">{{currentCategory.desc}}</div>
        </div>
          <item-group>
            <item-card-hori
              v-for="(item, i) in goodsList"
              :key="i"
              :goods="item"
              @click="itemClick(item.id)"
            />
          </item-group>
        <!-- </InfinityScroll> -->
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { goodsCategory, goodsList, GoodsList } from '@/api/api';

import ItemGroup from '@/components/item-group';
import ItemCardHori from '@/components/item-card-hori/';
import { Search, Tab, Tabs, Popup } from 'vant';
import InfinityScroll from '@/components/infinity-scroll';

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
      listApi: GoodsList,
      goodsList: [],
      currentCategory: {},
      navList: [],
      navActive: 0
    };
  },

  created() {
    this.init();
  },

  methods: {
    handleTabClick(index) {
      this.categoryId= this.navList[index].id;
      this.$router.replace({
        name: 'list',
        query: { itemClass: this.categoryId }
      });
      this.init();
    },
  init() {
    goodsCategory({id: this.categoryId}).then(res => {
      this.navList = res.data.data.brotherCategory;
      this.currentCategory= res.data.data.currentCategory;

      // 当id是L1分类id时，这里需要重新设置成L1分类的一个子分类的id
      if (res.data.data.parentCategory.id == this.categoryId) {
        this.categoryId = res.data.data.currentCategory.id;
      }

      for (let i = 0; i < this.navList.length; i++) {
        if (this.navList[i].id == this.categoryId) {
          this.navActive = i
          break;
        }
      }
      this.getGoodsList();
    });
  },
  getGoodsList() {
    goodsList({categoryId: this.categoryId}).then(res => {
      this.goodsList= res.data.data.list
    });
  },  
},

  components: {
    InfinityScroll,
    [ItemGroup.name]: ItemGroup,
    [ItemCardHori.name]: ItemCardHori,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Search.name]: Search,
    [Popup.name]: Popup
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

.fix-height {
  padding-bottom: 88px;
}

.over-hide {
  overflow: hidden;
}

.item_list {
  background-color: #fff;
  padding-bottom: 0;
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

.h {
  height: 100px;
  width: 100%;
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
