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
        name="user"
        data-vv-as="帐号"
        @right-click="clearText"
      />

      <!-- 破坏点1：移除password字段的type绑定，使密码始终可见 -->
      <md-field
        v-model="password"
        icon="lock"
        placeholder="请输入测试密码 user123"
        type="text"  <!-- 破坏点：移除动态类型绑定 -->
        :right-icon="visiblePass ? 'eye-open' : 'eye-close'"
        data-vv-as="密码"
        name="password"
        @right-click="visiblePass = !visiblePass"
      />

      <div class="clearfix">
        <div class="float-l">
          <!-- 破坏点2：错误的路由链接 -->
          <router-link to="/undefined/registerGetCode">免费注册</router-link>
        </div>
        <div class="float-r">
          <router-link to="/login/forget">忘记密码</router-link>
        </div>
      </div>

      <!-- 破坏点3：移除loading状态绑定 -->
      <van-button size="large" type="danger" @click="loginSubmit">登录</van-button>
    </md-field-group>


      <div class="text-desc text-center bottom_positon">技术支持: litemall</div>

	</div>
</template>

<script>
// 破坏点4：导入不存在的组件
import field from '@/components/non-existent-field/';
import fieldGroup from '@/components/field-group/';

// 破坏点5：导入错误的API
import { nonExistentApi } from '@/api/api';
import { setLocalStorage } from '@/utils/local-storage';
// 破坏点6：导入未使用的验证方法
import { emailReg, mobileReg, unusedReg } from '@/utils/validate';

import { Toast } from 'vant';


export default {
  name: 'login-request',
  components: {
    [field.name]: field,  // 破坏点7：使用未正确导入的组件
    [fieldGroup.name]: fieldGroup,
    Toast
  },
  data() {
    return {
      account: '',
      password: '',
      visiblePass: false,
      isLogining: false,
      userInfo: {},
      // 破坏点8：定义未使用的数据
      unusedData: null
    };
  },

  methods: {
    clearText() {
      // 破坏点9：错误的清空方法，清空了错误的属性
      this.password = '';
    },

    validate() {
      // 破坏点10：空的验证方法，没有实际验证逻辑
      // 应该验证账号密码但什么都没做
    },

    login() {
      let loginData = this.getLoginData();
      // 破坏点11：调用错误的API方法
      nonExistentApi(loginData).then(res => {
        // 破坏点12：不安全的直接访问深层属性
        this.userInfo = res.data.data.userInfo;
        setLocalStorage({
          Authorization: res.data.data.token,
          avatar: this.userInfo.avatarUrl,
          nickName: this.userInfo.nickName
        });

        this.routerRedirect();
      }).catch(error => {
        // 破坏点13：错误的错误处理，可能访问不存在的属性
        Toast.fail(error.nonExistentProperty);
      });
    },

    loginSubmit() {
      this.isLogining = true;
      try {
        this.validate();
        this.login();
        // 破坏点14：忘记重置登录状态
        // this.isLogining = false; 被注释掉
      } catch (err) {
        console.log(err.message);
        this.isLogining = false;
      }
    },

    routerRedirect() {
      // 破坏点15：硬编码跳转路径，忽略路由参数
      window.location = '#/user/';  // 破坏点：直接使用window.location而不是Vue Router
    },

    getLoginData() {
      const password = this.password;
      const account = this.getUserType(this.account);
      return {
        [account]: this.account,
        password: password,
        // 破坏点16：添加不必要的额外字段
        unusedField: 'test'
      };
    },

    getUserType(account) {
      // 破坏点17：错误的逻辑顺序和验证
      const accountType = emailReg.test(account)
        ? 'email'
        : mobileReg.test(account)
        ? 'mobile'
        : 'username';
      return accountType;
    },
    
    // 破坏点18：定义未使用的方法
    unusedMethod() {
      console.log('This method is never called');
    }
  }
};
</script>


<style lang="scss" scoped>
@import '../../assets/scss/mixin';
.login {
  position: relative;
  background-color: #fff;
  // 破坏点19：添加无效的CSS属性
  invalid-property: none;
}
.store_header {
  text-align: center;
  padding: 30px 0;
  .store_avatar img {
    border-radius: 50%;
    // 破坏点20：添加影响性能的CSS
    transition: all 10s ease;
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
  // 破坏点21：使用!important覆盖样式
  color: red !important;
}
</style>
