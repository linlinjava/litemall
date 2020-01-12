<template>
	<md-field-group class="register_submit">
		<md-field v-model="code" icon="mobile" placeholder="请输入验证码">
			<div slot="rightIcon" @click="getCode" class="getCode red">
				<countdown v-if="counting" :time="60000" @end="countDownEnd">
				  <template slot-scope="props">{{ +props.seconds || 60 }}秒后获取</template>
				</countdown>
				<span v-else>获取验证码</span>
			</div>
		</md-field>
		<md-field v-model="username" icon="username" placeholder="请输入用户名"/>
		<md-field v-model="password" icon="lock" placeholder="请输入密码"/>
		<md-field v-model="repeatPassword" icon="lock" placeholder="请再次确认密码"/>

		<div class="register_submit_btn">
			<van-button type="danger" size="large" @click="registerSubmit">确定</van-button>
		</div>
	</md-field-group>
</template>

<script>
import field from '@/components/field/';
import fieldGroup from '@/components/field-group/';
import { authRegisterCaptcha } from '@/api/api';
import { authRegister } from '@/api/api';
import {Toast} from "vant";

export default {
	props: {
		phone: String
	},
	data() {
		return {
		  counting: true,
		  code: '',
		  username: '',
		  password: '',
		  repeatPassword: ''
		};
	},
	mounted:function(){
		this.getCode();
	},

  methods: {
    registerSubmit() {
      if(this.username === '' || this.code === ''){
        return
      }
      if(this.password === '' || this.repeatPassword === ''){
        return
      }
      if(this.password !== this.repeatPassword){
        this.password = ''
        this.repeatPassword = ''
        return
      }
      let data = this.getRegisterData();
      authRegister(data).then(res => {
        this.$router.push({
          name: 'registerStatus',
          params: { status: 'success' }
        });
      }).catch (error => {
        Toast.fail(error.data.errmsg);
        if (error.data.errno == 705) {
          window.location = '#/login/';
        }
      });
    },

    getCode() {
      this.counting = true;
      let data = {
        mobile: this.phone
      };
      authRegisterCaptcha(data).then(res => {
        this.counting = true;
      }).catch(error => {
        alert(error.data.errmsg);
        this.counting = true;
      });
    },

    getRegisterData() {
      const password = this.password;
      const code = this.code;
      const repeatPassword = this.repeatPassword;
      const mobile = this.phone;
      const username = this.username;
      return {
        code: code,
        username: username,
        password: password,
        repeatPassword: repeatPassword,
        mobile: mobile
      };
    },

    countDownEnd() {
      this.counting = false;
    }
  },

  components: {
    [field.name]: field,
    [fieldGroup.name]: fieldGroup
  }
};
</script>


<style lang="scss" scoped>
@import '../../../assets/scss/mixin';

.register_submit {
  padding-top: 40px;
  background-color: #fff;
}

.register_submit_btn {
  padding-top: 30px;
}

.getCode {
  @include one-border(left);
  text-align: center;
}

.time_down {
  color: $red;
}
</style>
