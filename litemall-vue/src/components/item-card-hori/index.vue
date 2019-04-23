<template>
  <div class="item_card_H_wrap one_border" @click="OnClick">
    <div class="clearfix">
      <div class="item_card_image float-l">
        <div v-if="$slots.leftTopIcon" class="leftTopIcon">
          <slot name="leftTopIcon"></slot>
        </div>

        <div v-if="$slots.mask" class="item_img_mask">
          <slot name="mask"></slot>
        </div>
        <img v-lazy="goods.picUrl">
        <div class="item_image_desc">{{goodsStatusToMe}}</div>
      </div>
      <!-- {{goods}} -->
      <div class="item_card_info">
        <div class="item_card_name">
          <van-tag plain type="danger" v-if="goods.is_haitao">海淘</van-tag>
          <span v-if="$slots.icon" class="item_card_icon">
            <slot name="icon"></slot>
          </span>
          {{goods.name}}
        </div>

        <div class="item_card_info_desc">{{goods.brief}}</div>

        <div class="item_card_footer">
          <div class="footer_price">
            <span>{{goods.retailPrice * 100 | yuan}}</span>
            <span class="marketPrice" v-if="goods.counterPrice">{{goods.counterPrice * 100 | yuan}}</span>
          </div>

          <div class="footer_desc" v-if="$slots.footer">
            <slot name="footer"></slot>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import item_mix from '@/mixin/item-card';

export default {
  name: 'item-card-hori',
  mixins: [item_mix]
};
</script>

<style lang="scss" scoped>
.item_card_H_wrap {
  padding: 15px 10px;
}

.item_card_image {
  position: relative;
  padding-top: 5px;
  width: 90px;
  height: 90px;
  text-align: center;
  img {
    display: inline-block;
    max-height: 100%;
    max-width: 100%;
  }
  .leftTopIcon {
    position: absolute;
    left: 0;
    top: 0;
    max-width: 50%;
    text-align: left;
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

.item_card_info {
  position: relative;
  margin-left: 110px;
  height: 100px;
  .item_card_name {
    font-size: 12px;
    margin-bottom: 10px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .item_card_name .item_card_icon {
    width: 25px;
    height: 14px;
    vertical-align: middle;
    display: inline-block;
    background-repeat: no-repeat;
  }

  .isHaiTao {
    background-image: url(http://mamaqunaer.oss-cn-shanghai.aliyuncs.com/20171121/xMACDPN2Bz.png);
  }

  .item_card_info_desc {
    font-size: 12px;
    color: $font-color-gray;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
  }

  .item_card_icon img {
    max-height: 100%;
    max-width: 100%;
  }

  .item_card_footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;

    .footer_price {
      color: $red;
      margin-right: 5px;
    }

    .footer_price .marketPrice {
      color: $font-color-gray;
      font-size: 12px;
      text-decoration: line-through;
      margin-left: 5px;
    }

    .footer_desc {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
