<template>
  <div class="popup_wrap">
    <van-icon name="clear" class="cancel_popup" @click.native="$parent.value = false"></van-icon>
    <div class="popup_header">商品属性</div>
    <div class="popup_content">
      <van-cell-group>
        <van-cell v-for="(str, i) in propsStr" :key="i">
          <van-row>
            <van-col span="8">{{str[0]}}</van-col>
            <van-col span="16">{{str[1]}}</van-col>
          </van-row>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import { Row, Col } from 'vant';

export default {
  name: 'popup-props',

  props: {
    propsStr: {
      type: Array,
      default: () => []
    }
  },

  components: {
    [Col.name]: Col,
    [Row.name]: Row
  }
};
</script>


<style lang="scss" scoped>
.popup_wrap {
  position: relative;
  padding-bottom: 30px;
  box-sizing: border-box;
  .popup_header {
    padding: 15px 0 30px 0;
    text-align: center;
  }

  .popup_content {
    min-height: 150px;
    max-height: 400px;
    box-sizing: border-box;
    overflow-x: hidden;
    overflow-y: scroll;
    padding: 0 10px;
    line-height: 30px;
    &::-webkit-scrollbar {
      background-color: #fff;
      width: 5px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 3px;
      background-color: #bebebe;
    }
    ol {
      padding-left: 15px;
      list-style: decimal;
    }
  }

  .cancel_popup {
    position: absolute;
    right: 15px;
    top: 15px;
    z-index: 9;
    font-size: 18px;
  }
}
</style>
