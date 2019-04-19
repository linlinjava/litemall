<template>
	<div class="items_group">
		<van-cell-group v-if="setting && !!setting.title">
			<van-cell>
				<slot v-if="$slots.title_right" name="title_right"></slot>
				<template slot="icon">
					<van-icon class="van-cell__left-icon" v-if="setting.icon" :style="{color: setting.title_color}" :name="setting.icon"/>
				</template>
				<template slot="title">
					<span class="group_title" :style="{color: setting.title_color}">{{setting.title}}</span>
					<slot name="title-desc">
						<span class="group_title_desc">{{setting.title_desc}}</span>
					</slot>
				</template>
			</van-cell>
		</van-cell-group>

		<div class="group_banner" v-if="setting && setting.banner">
			<img v-lazy="setting.banner" alt="海报" width="100%">
		</div>

		<div class="item_scroll_box" v-if="setting.style">
			<div class="item_scroll" v-scrollArrow="scrollMore">
				<div class="item_scroll_wrap" :style="{width: scrollWidth}">
					<slot></slot>
				</div>
			</div>

			<transition name="fade">
				<van-icon name="arrow" v-show="leftOver && isShowArrow" class="items_arrow right_arrow" />
			</transition>
			<transition name="fade">
				<van-icon name="arrow-left" v-show="rightOver && isShowArrow" class="items_arrow left_arrow" />
			</transition>
		</div>
		<div v-else>
			<slot></slot>
		</div>

	</div>
</template>
<script>
import ItemCardVert from '../item-card-vert/';
import ItemCardHori from '../item-card-hori/';
import { Cell, CellGroup, Icon } from 'vant';
import scrollArrow from '../_directive/scrollMore';

export default {
  name: 'item-group',
  props: {
    setting: {
      type: Object,
      default: () => ({})
    },
    col: {
      type: Number,
      default: 3
    }
  },
  data() {
    const clientW =
        document.body.clientWidth || document.documentElement.clientWidth,
      col = this.col,
      itemW = Math.floor(clientW / col),
      itemsLen = this.setting.item_len;
    return {
      itemW,
      scrollWidth: `${itemW * itemsLen}px`,
      rightOver: false,
      leftOver: true,
      isShowArrow: itemsLen > col
    };
  },
  methods: {
    scrollMore(obj) {
      this.rightOver = !obj.isLeftOver;
      this.leftOver = !obj.isRightOver;
    }
  },
  directives: {
    scrollArrow
  },
  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Icon.name]: Icon,
    [ItemCardVert.name]: ItemCardVert,
    [ItemCardHori.name]: ItemCardHori
  }
};
</script>

<style lang="scss" scoped>
.items_group {
  background-color: #fff;
}
.group_title {
  font-weight: 700;
}

.group_title_desc {
  font-size: 12px;
  color: $font-color-gray;
}
.group_banner img {
  max-height: 200px;
  display: block;
}

.item_scroll_box {
  position: relative;
  width: 100%;
  padding: 10px 0;
}
.item_scroll {
  width: 100%;
  overflow-y: hidden;
  overflow-x: scroll;
}
.item_scroll_wrap {
  display: flex;
}

.items_arrow {
  position: absolute;
  top: 50%;
  transform: translate(0, -50%);
  font-size: 18px;
}

.left_arrow {
  left: 0;
}

.right_arrow {
  right: 0;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s;
}

.fade-enter-to,
.fade-leave {
  opacity: 1;
}
</style>
