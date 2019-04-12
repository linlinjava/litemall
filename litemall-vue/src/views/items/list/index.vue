<template>
  <div class="item_list over-hide">
    <form action="/search">
      <van-search
        placeholder="请输入商品名称"
        v-model="searchVal"
        @click="$router.push({ name: 'search' })"
        showAction
      />
    </form>

    <van-tabs v-model="tabActive" @disabled="toggleFilterModal(true)">
      <van-tab
        v-for="(tab, tabIndex)  in tabsItem"
        :title="tab.name"
        :key="tab.type"
        :disabled="tab.sort === false"
      >
        <InfinityScroll
          :ref="'tabScrolls' + tabIndex"
          class="full-page scroll-wrap fix-height"
          :beforeRequest="beforeRequest"
          :apiUrl="listApi"
          @onLoad="onLoad(tabIndex, $event)"
        >
          <item-group>
            <item-card-hori
              v-for="(item, i) in tab.items"
              :key="i"
              :goods="item"
              @click="itemClick(item.id)"
            />
          </item-group>
        </InfinityScroll>
      </van-tab>
    </van-tabs>

    <van-popup class="filterItem" v-model="filterItemShow" position="right">
      <ul>
        <li
          v-for="(li, i) in filterItem"
          :key="i"
          @click="filterMethod(i)"
          :class="{filter_active: li.isActive}"
        >
          {{li.name}}
          <van-icon name="success" v-show="li.isActive" class="float-r"/>
        </li>
      </ul>
    </van-popup>

    <!-- <transition name="fade">
			<van-icon
				name="arrowupcircle"
				class="backTop"
				@click.native="backTop"
				v-show="showArrow"
			/>
    </transition>-->
  </div>
</template>

<script>
import { GOODS_SEARCH } from '@/api/goods';

import ItemGroup from '@/vue/components/item-group';
import ItemCardHori from '@/vue/components/item-card-hori/';
import { Search, Tab, Tabs, Popup } from 'vant';
// import { throttle } from 'lodash';
import InfinityScroll from '@/vue/components/infinity-scroll';

export default {
  name: 'Item-list',
  props: {
    keyword: {
      type: String,
      default: ''
    },
    itemClass: {
      type: [String, Number],
      default: ''
    }
  },

  data() {
    return {
      listApi: GOODS_SEARCH,
      shop_id: 1,
      tabActive: 0,
      tabsItem: [
        { name: '默认', sort: '', items: [] },
        { name: '销量', sort: 'sold_quantity', items: [] },
        { name: '最新', sort: 'created_at', items: [] }
        // { name: '筛选', sort: false, items: [] }
      ],
      is_haitao: 0,
      filterItem: [
        {
          name: '全部',
          filterType: 2,
          isActive: true
        },
        {
          name: '店铺商品',
          filterType: 0,
          isActive: false
        },
        {
          name: '海淘商品',
          filterType: 1,
          isActive: false
        }
      ],
      isHaitao: 2,
      searchVal: '',
      filterItemShow: false
      // showArrow: false
    };
  },

  computed: {
    sortVal() {
      const { tabActive: i } = this;
      return this.tabsItem[i].sort;
    }
  },

  created() {
    // this.scrollShowArrow = throttle(this.scrollShowArrow, 100);
  },

  methods: {
    onLoad(i, items) {
      this.tabsItem[i].items.push(...items);
    },
    beforeRequest() {
      return {
        params: {
          q: this.searchVal,
          shop_id: this.shop_id,
          cid: this.itemClass,
          sort: this.sortVal,
          is_haitao: this.isHaitao
        }
      };
    },
    // 滚动容器改变, 导致滚动监听失效, 暂时先注释了
    // eventListen(isBind = true) {
    //   if (isBind) {
    //     this.$el.addEventListener('scroll', this.scrollShowArrow);
    //   } else {
    //     this.$el.removeEventListener('scroll', this.scrollShowArrow);
    //   }
    // },
    // scrollShowArrow() {
    //   this.showArrow = this.$el.scrollTop > 120;
    // },
    activeFilter(i) {
      this.filterItem.forEach((item, index) => {
        item.isActive = i === index;
      });
    },
    resetActiveTab() {
      const { tabActive: i } = this;
      this.tabsItem[i].items = [];
      const targetScroll = this.$refs[`tabScrolls${i}`][0];
      debugger;
      targetScroll && targetScroll.resetInit();
    },
    toggleFilterModal(status) {
      this.filterItemShow = status;
    },
    filterMethod(i) {
      const filterType = this.filterItem[i].filterType;
      if (filterType === this.isHaitao) return;

      this.isHaitao = filterType;
      this.activeFilter(i);
      this.toggleFilterModal(false);
      this.resetActiveTab();
    },
    // backTop() {
    //   this.$el.scrollTop = 0;
    // },
    itemClick(id) {
      this.$router.push({ name: 'detail', params: { itemId: id } });
    }
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
.filterItem {
  width: 40%;
  height: 100%;
  li {
    padding: 10px;
    &.filter_active {
      color: $red;
    }
  }
}
.backTop {
  position: fixed;
  right: 20px;
  bottom: 20px;
  font-size: 24px;
}
</style>
