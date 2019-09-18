<template>
  <div>
    <van-cell-group>
      <van-cell title="联系客服" @click="showKefu = true" isLink></van-cell>
      <van-cell title="意见反馈" to="/user/feedback" isLink></van-cell>
      <van-cell title="常见问题" to="/user/help" isLink/>
    </van-cell-group>
    <van-popup v-model="showKefu">
      <van-cell-group>
        <van-cell title="项目名称" value="litemall" />
        <van-cell title="项目地址" value="Github"  url="https://github.com/linlinjava/litemall"/>
        <van-cell title="项目地址" value="Gitee"  url="https://gitee.com/linlinjava/litemall"/>
        <van-cell title="联系电话" value="021-xxxx-xxxx" />
        <van-cell title="联系QQ" value="738696120" />
        <van-cell title="当前版本" value="V1.0" />
        <van-cell title="开源协议" value="MIT" />
      </van-cell-group>
    </van-popup>
  </div>
</template>

<script>
import { Popup, Cell, CellGroup } from 'vant';

export default {
  data() {
    return {
      showKefu: false
    };
  },

  components: {
    [Popup.name]: Popup,
    [Cell.name]: Cell, 
    [CellGroup.name]: CellGroup    
  }
};
</script>
<style scoped lang="scss">
  .van-popup {
    width: 80%;
    padding: 20px;
    box-sizing: border-box;

  }
</style>