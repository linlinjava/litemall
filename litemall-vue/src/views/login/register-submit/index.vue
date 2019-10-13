<template>
  <md-field-group class="register_submit">
    <md-field v-model="username" icon="username" disabled="disabled"/>
    <md-field v-model="code" type="number" icon="mobile" placeholder="请输入验证码">
      <div slot="rightIcon" @click="getCode" class="getCode red">
        <countdown v-if="counting" :time="60000" @end="countdownend">
          <template slot-scope="props">{{ +props.seconds || 60 }}秒后获取</template>
        </countdown>
        <span v-else>获取验证码</span>
      </div>
    </md-field>
    <md-field v-model="password" icon="lock" placeholder="请输入密码" type="password"/>
    <!--    <md-field v-model="repeatPassword" icon="lock" placeholder="请再次确认密码"/>-->

    <div class="register_submit_btn">
      <van-button type="danger" size="large" @click="registerSubmit">确定</van-button>
    </div>
  </md-field-group>
</template>

<script>
  import field from '@/components/field/';
  import fieldGroup from '@/components/field-group/';
  import {authRegister} from '@/api/api';

  export default {
    data() {
      return {
        username: this.$route.params.mobile,
        mobile: this.$route.params.mobile,
        counting: true,
        code: '',
        password: '',
        repeatPassword: ''
      };
    },
    mounted() {
      if (!this.mobile) {
        // 如果没数据
        this.$router.push({name: 'registerGetCode'});
      }
    },
    methods: {
      registerSubmit() {
        if (this.code.length !== 6) {
          return this.$toast('验证码必须是6位');
        }
        if (this.password.length < 6) {
          return this.$toast('密码必须大于或者等于6位');
        }
        let params = {
          code: this.code,
          username: this.username,
          mobile: this.mobile,
          password: this.password,
        };
        authRegister(params).then(res => {
          console.log(res);
          this.$router.push({
            name: 'registerStatus',
            params: {status: 'success'}
          });
        })

      },

      getCode() {
        this.counting = true;
      },
      countdownend() {
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
