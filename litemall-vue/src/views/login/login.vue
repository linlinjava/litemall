<template>
	<div class="login">
    	<div class="store_header">
		<div class="store_avatar">
			<img src="../../assets/images/avatar_default.png" alt="头像" width="55" height="55">
		</div>
		<div class="store_name">litemall-vue</div>
	</div>

    <md-field-group>
      <md-field
        v-model="account"
        icon="username"
        placeholder="请输入测试账号 user123"
        right-icon="clear-full"
        v-validate="'required'"
        name="user"
        data-vv-as="帐号"
        @right-click="clearText"
      />

      <md-field
        v-model="password"
        icon="lock"
        placeholder="请输入测试密码 user123"
        :type="visiblePass ? 'text' : 'password'"
        :right-icon="visiblePass ? 'eye-open' : 'eye-close'"
        v-validate="'required'"
        data-vv-as="密码"
        name="password"
        @right-click="visiblePass = !visiblePass"
      />

      <div class="clearfix">
        <div class="float-l">
          <router-link to="/login/registerGetCode">免费注册</router-link>
        </div>
        <div class="float-r">
          <router-link to="/login/forget">忘记密码</router-link>
        </div>
      </div>

      <van-button size="large" type="danger" :loading="isLogining" @click="loginSubmit">登录</van-button>
    </md-field-group>


      <div class="text-desc text-center bottom_positon">技术支持: litemall</div>

	</div>
</template>

<script>
import field from '@/components/field/';
import fieldGroup from '@/components/field-group/';

import { loginByUsername, USER_LOGIN, USER_PROFILE } from '@/api/user';
import { setLocalStorage } from '@/utils/local-storage';
import { emailReg, mobileReg } from '@/utils/validate';

import { Toast } from 'vant';


export default {
  name: 'login-request',
  components: {
    [field.name]: field,
    [fieldGroup.name]: fieldGroup,
    Toast
  },
  data() {
    return {
      account: '',
      password: '',
      visiblePass: false,
      isLogining: false,
      userInfo: {}
    };
  },

  methods: {
    clearText() {
      this.account = '';
    },

    async validate() {
      const result = await this.$validator.validate();
      if (!result) {
        const errMsg = this.errors.items[0].msg;
        Toast(errMsg);
        throw new Error(`表单验证: ${errMsg}`);
      }
    },

    async login() {
      let loginData = this.getLoginData();
      loginByUsername(loginData)
      let { data } = await this.$reqPost(USER_LOGIN, loginData);
      this.userInfo = data.data.userInfo;
      console.log(this.userInfo);
      setLocalStorage({
        Authorization: data.data.token
      });
      this.getUserProfile();
    },

    async loginSubmit() {
      this.isLogining = true;
      try {
        await this.validate();
        await this.login();
        this.isLogining = false;
      } catch (err) {
        console.log(err.message);
        this.isLogining = false;
      }
    },

    getUserProfile() {
      // const {
      //   data: { data }
      // } = await this.$reqGet(USER_PROFILE);
      setLocalStorage({
        avatar: this.userInfo.avatarUrl,
        // user_id: data.user_id,
        // background_image: data.background_image,
        nickName: this.userInfo.nickName
      });

      this.routerRedirect();
    },

    routerRedirect() {
      // const { query } = this.$route;
      // this.$router.replace({
      //   name: query.redirect || 'home',
      //   query: query
      // });
      window.location = '#/user/';
    },

    getLoginData() {
      const password = this.password;
      const account = this.getUserType(this.account);
      return {
        [account]: this.account,
        password: password
      };
    },

    getUserType(account) {
      const accountType = mobileReg.test(account)
        ? 'mobile'
        : emailReg.test(account)
        ? 'email'
        : 'username';
      return accountType;
    }
  }
};
</script>


<style lang="scss" scoped>
@import '../../assets/scss/mixin';
.login {
  position: relative;
  background-color: #fff;
}
.store_header {
  text-align: center;
  padding: 30px 0;
  .store_avatar img {
    border-radius: 50%;
  }
  .store_name {
    padding-top: 5px;
    font-size: 16px;
  }
}
.register {
  padding-top: 40px;
  color: $font-color-gray;
  a {
    color: $font-color-gray;
  }
  > div {
    width: 50%;
    box-sizing: border-box;
    padding: 0 20px;
  }
  .connect {
    @include one-border(right);
    text-align: right;
  }
}
.bottom_positon {
  position: absolute;
  bottom: 30px;
  width: 100%;
}
</style>
