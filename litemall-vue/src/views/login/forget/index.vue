<template>
  <md-field-group class="foget_view">
    <md-field
            v-model="mobile"
            icon="mobile"
            placeholder="请输入手机号"/>

    <md-field
            v-model="code"
            icon="lock"
            placeholder="请输入短信验证码"
    >
      <div slot="rightIcon" @click="getCode" class="getCode red">
        <countdown v-if="counting" :time="60000" @end="countdownend">
          <template slot-scope="props">{{ +props.seconds || 60 }}秒后获取</template>
        </countdown>
        <span v-else>获取验证码</span>
      </div>
    </md-field>
    <md-field
            v-model="password"
            icon="lock"
            type="password"
            placeholder="请输入新密码"/>

    <div class="foget_submit">
      <van-button size="large" type="danger" @click="submitCode">修改密码</van-button>
    </div>
  </md-field-group>
</template>

<script>
  import field from '@/components/field/';
  import fieldGroup from '@/components/field-group/';
  import {authCaptcha, authForget} from '@/api/api';

  export default {
    data() {
      return {
        counting: false,
        mobile: '',
        code: '',
        password: '',
      };
    },

    methods: {
      submitCode() {
        // this.$router.push({name: 'forgetReset'});
        authForget({
          code: this.code,
          password: this.password,
          mobile: this.mobile,
        }).then(res => {
          this.$dialog.alert({message: '修改成功'});
          this.$router.push({name: 'login'})
        })
      },
      getCode() {
        if (!this.mobile) return;
        authCaptcha({mobile: this.mobile, type: 0}).then(res => {
          console.log(res);
          this.counting = true;
        });
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

  div.foget_view {
    background-color: #fff;
    padding-top: 30px;
  }

  div.foget_submit {
    padding-top: 30px;
    padding-bottom: 20px;
  }

  .getCode {
    @include one-border(left);
    text-align: center;
  }

  .time_down {
    color: $red;
  }
</style>
