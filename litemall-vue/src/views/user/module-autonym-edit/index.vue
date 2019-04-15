<template>
	<div>
		<van-cell-group>
			<van-field
				v-model="field.username"
				label="姓名"
				placeholder="请输入姓名"
				required
				:error="nameErr"
				@blur="checkUserName"
				@focus="nameErr = false"/>

			<van-field
				v-model="field.idCard"
				label="身份证号"
				placeholder="请输入身份证号码"
				required
				:error="idCardErr"
				@blur="checkIdCard"
				@focus="idCardErr = false"/>
		</van-cell-group>

		<id-card-upload
			:front.sync="field.frontUrl"
			:reverse.sync="field.reverseUrl"/>

		<div class="save_div">
			<van-button type="danger" size="large" @click="save">保存</van-button>
		</div>

		<ul class="text-desc">
			<li>根据海关规定: 购买跨境商品需要办理相关手续.</li>
			<li>根据海关规定: 购买跨境商品需要办理相关手续.</li>
		</ul>
	</div>
</template>

<script>
import idCardUpload from './id-card-upload';
import { idCard } from 'core/regexp';

export default {
  data() {
    return {
      field: {
        username: '',
        idCard: '',
        frontUrl: '',
        reverseUrl: ''
      },
      nameErr: true,
      idCardErr: true
    };
  },

  methods: {
    save() {
      const keys = Object.keys(this.field);
      const isValid = keys.every(key => {
        const message = this.getErrorMessageByKey(key);
        if (message) {
          this.$toast.fail({ message, duration: 1000 });
        }
        return !message;
      });
      if (isValid) {
        console.log('保存');
      }
    },
    checkUserName() {
      this.nameErr =
        this.field.username == '' || this.field.username.length > 5;
    },
    checkIdCard() {
      this.idCardErr = !idCard.test(this.field.idCard);
    },

    getErrorMessageByKey(key) {
      const val = this.field[key];
      switch (key) {
        case 'username':
          return val ? (val.length < 5 ? '' : '名字太长了') : '请输入名字';
        case 'idCard':
          return val ? (idCard(val) ? '' : '请输入正确身份证') : '请输入身份证';
        case 'frontUrl':
          return val ? '' : '请上传正面照片';
        case 'reverseUrl':
          return val ? '' : '请上传背面照片';
      }
    }
  },

  components: {
    [idCardUpload.name]: idCardUpload
  }
};
</script>


<style lang="scss" scoped>
.save_div {
  padding: 0 20px;
}

ul.text-desc {
  padding: 20px;
  list-style: inside;
  li {
    margin-bottom: 15px;
  }
}
</style>
