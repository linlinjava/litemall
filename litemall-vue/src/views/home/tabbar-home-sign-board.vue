<template>
	<div class="signboard">
		<img :src="boardUrl" :height="signboardHeight" width="100%">
		<div class="store_opacity clearfix">
			<div class="float-l">{{storeName}}</div>
			<div class="float-r store_collect isCollect" @click="showCollect = true">
				<van-icon name="shoucang-full" />
				<span>收藏</span>
			</div>
		</div>

		<van-popup v-model="showCollect" position="top" style="background-color: transparent">
			<img :src="showCollect && collectImg" @click="showCollect = false" width="100%" alt="右上角收藏">
		</van-popup>
	</div>
</template>

<script>
import { Popup } from 'vant';
import collectImg from '@/assets/images/index_collect.png';

export default {
  name: 'sign-board',
  props: {
    boardUrl: {
      type: String,
      required: true
    },
    storeName: {
      type: String,
      required: true
    }
  },
  data() {
    const clientW =
      document.body.clientWidth || document.documentElement.clientWidth;
    const signboardHeight = clientW ? (clientW * 2) / 3 : 250;
    return {
      signboardHeight,
      showCollect: false,
      collectImg
    };
  },

  components: {
    [Popup.name]: Popup
  }
};
</script>

<style lang="scss" scoped>
.signboard {
  position: relative;
  min-height: 250px;
}
.store_opacity {
  position: absolute;
  bottom: 0;
  color: #fff;
  width: 100%;
  background-image: linear-gradient(rgba(0, 0, 0, 0), rgba(0, 0, 0, 1));
  padding: 15px 10px;
  box-sizing: border-box;
}

.isCollect i {
  color: $red;
}
</style>
