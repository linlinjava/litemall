<template>
	<div>
		<van-cell-group>
			<van-field
				label="登录密码"
				v-model="password"
				type="password"
				placeholder="请输入登录密码"
				:error="!!$vuelidation.error('password')" />

			<van-field
				label="新手机号"
				v-model="new_mobile"
				placeholder="请输入新手机号"
				:error="!!$vuelidation.error('new_mobile')" />

			<van-field
				label="验证码"
				v-model="code"
				@click-icon="getCode"
				placeholder="请输入验证码">

				<span slot="icon"
					class="verifi_code red"
					:class="{verifi_code_counting: counting}"
					@click="getCode">
					<countdown v-if="counting" :time="60000" @countdownend="countdownend">
					  <template slot-scope="props">{{ +props.seconds || 60 }}秒后获取</template>
					</countdown>
					<span v-else>获取验证码</span>
				</span>
			</van-field>
		</van-cell-group>

		<div class="bottom_btn">
			<van-button size="large" type="danger" @click="saveMobile">保存</van-button>
		</div>
	</div>
</template>


<script>
import { USER_SENDCODE } from '@/api/user';

import { Field } from 'vant';

export default {
  data: () => ({
    password: '',
    new_mobile: '',
    code: '',
    counting: false
  }),

  vuelidation: {
    data: {
      password: {
        required: true
      },
      new_mobile: {
        required: true,
        mobile: true
      }
    }
  },

  methods: {
    getCode() {
      if (!this.counting && this.vuelidat()) {
        this.$reqPost(USER_SENDCODE, {
          mobile: this.new_mobile,
          operation: 'changeMobile'
        }).then(() => {
          this.$toast.success('发送成功');
          this.counting = true;
        });
      }
    },
    countdownend() {
      this.counting = false;
    },
    vuelidat() {
      this.$vuelidation.valid();
      if (this.$vuelidation.error('new_mobile')) {
        const msg = this.$vuelidation.error('new_mobile');
        this.$toast(msg == 'Required' ? '请输入手机号' : msg);
        return false;
      }
      return true;
    },
    saveMobile() {
      console.log('保存手机号');
    }
  },

  components: {
    [Field.name]: Field
  }
};
</script>

<style lang="scss" scoped>
@import '../../../../assets/scss/var';
@import '../../../../assets/scss/mixin';
.bottom_btn {
  padding: 30px 15px 0 15px;
}

.verifi_code {
  @include one-border;
  padding-left: 10px;
  &::after {
    border-bottom: 0;
    border-left: 1px solid $border-color;
  }

  &_counting {
    color: $font-color-gray;
  }
}
</style>
