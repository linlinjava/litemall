<template>
  <div class="item_card_V_wrap" @click="OnClick">
    <div class="item_card_image">
      <div v-if="$slots.leftTopIcon" class="leftTopIcon">
        <slot name="leftTopIcon"></slot>
      </div>

      <div v-if="$slots.mask" class="item_img_mask">
        <slot name="mask"></slot>
      </div>
      <img v-lazy="goods.pic_url">
      <div class="item_image_desc">{{goodsStatusToMe}}</div>
    </div>
    <div class="item_card_name">
      <van-tag plain type="danger" v-if="goods.is_haitao">海淘</van-tag>
      <span v-if="$slots.icon" class="item_card_icon">
        <slot name="icon"></slot>
      </span>
      {{goods.name}}
    </div>

    <div class="item_card_price">{{goods.retailPrice | yuan}}</div>
  </div>
</template>


<script>
import item_mix from '@/mixin/item-card';

export default {
  name: 'item-card-vert',
  mixins: [item_mix]
};
</script>

<style lang="scss" scoped>
.item_card_V_wrap {
  display: inline-block;
  flex: 1;
  width: 90px;
  margin: 0 10px;
}

.item_card_image {
  position: relative;
  width: 100%;
  height: 90px;
  margin-bottom: 5px;
  text-align: center;
  .leftTopIcon {
    position: absolute;
    left: 0;
    top: 0;
    z-index: 3;
    max-width: 50%;
    text-align: left;
  }
  img {
    display: inline-block;
    max-height: 100%;
    max-width: 100%;
  }
  .item_image_desc {
    position: absolute;
    bottom: 0;
    background-color: rgba(244, 133, 145, 0.8);
    width: 100%;
    color: #fff;
    font-size: 12px;
  }
  .item_img_mask {
    position: absolute;
    top: 50%;
    left: 50%;
    z-index: 2;
    transform: translate(-50%, -50%);
    width: 70px;
    height: 70px;
    overflow: hidden;
  }
}

.item_card_name {
  line-height: 16px;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  text-align: center;
}

.item_card_icon {
  width: 30px;
  height: 14px;
  vertical-align: middle;
  display: inline-block;
  background-repeat: no-repeat;
  background-size: 100% 100%;
}

.item_card_icon img {
  max-height: 100%;
  max-width: 100%;
}

.isHaiTao {
  background-image: url(http://www-dev.mamaqunaer.com/images/common/icon_menu_bdt.png);
}

.item_card_price {
  text-align: center;
  color: $red;
}
</style>
