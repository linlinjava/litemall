<template>
  <div class="set_nickname">
    <van-cell-group>
      <van-field v-model="nickName" label="昵称" />
    </van-cell-group>

    <div class="bottom_btn">
      <van-button size="large" type="danger" @click="saveNick">保存</van-button>
    </div>
  </div>
</template>

<script>
import { authProfile } from '@/api/api';
import { Field } from 'vant';

export default {
  data() {
    return {
      nickName: ''
    };
  },

  created() {
    this.getNick();
  },

  methods: {
    getNick() {
      this.nickName = localStorage.getItem('nickName') || '';
    },
    saveNick() {
      authProfile({ nickname: this.nickName }).then(res => {
        localStorage.setItem('nickName', this.nickName);
        this.$dialog.alert({ message: '保存成功' }).then(() => {
          this.$router.go(-1);
        });
      });
    }
  },

  components: {
    [Field.name]: Field
  }
};
</script>

<style scoped>
.bottom_btn {
  padding: 30px 15px 0 15px;
}
</style>
