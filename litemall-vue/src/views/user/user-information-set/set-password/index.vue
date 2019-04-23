<template>
  <div>
    <van-cell-group>
      <van-field
        label="原密码"
        v-model="password"
        type="password"
        placeholder="请输入原密码"
        :error="!!$vuelidation.error('password')"
      />

      <van-field
        label="新密码"
        v-model="new_password"
        type="password"
        placeholder="请输入新密码"
        :error="!!$vuelidation.error('new_password')"
      />

      <van-field
        label="确认密码"
        v-model="repeat_password"
        type="password"
        placeholder="请再次输入密码"
        :error="!!$vuelidation.error('repeat_password')"
      />
    </van-cell-group>

    <div class="bottom_btn">
      <van-button size="large" type="danger" @click="modifypassword">保存</van-button>
    </div>
  </div>
</template>


<script>
import { USER_MODIFY_PASSWORD, USER_LOGOUT } from '@/api/user';
import { removeLocalStorage } from '@/utils/local-storage';

import { Field } from 'vant';

export default {
  data: () => ({
    password: '',
    new_password: '',
    repeat_password: ''
  }),

  methods: {
    modifypassword() {
      if (this.passwordValid()) {
        this.$reqPut(USER_MODIFY_PASSWORD, {
          old_password: this.password,
          new_password: this.new_password
        })
          .then(() => this.$dialog.alert({ message: '保存成功, 请重新登录.' }))
          .then(() => this.$reqGet(USER_LOGOUT))
          .then(() => {
            removeLocalStorage(
              'Authorization',
              'avatar',
              'background_image',
              'nickName'
            );
            this.$router.replace({ name: 'login' });
          });
      }
    },
    passwordValid() {
      if (this.new_password != this.repeat_password) {
        this.$toast('密码不一致, 请再次确认密码');
        return false;
      }
      return true;
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
