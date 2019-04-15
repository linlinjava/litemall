<template>
	<div class="id_card_upload">
		<div>身份证照片</div>
		<van-row class="upload_hint text-desc">
			<van-icon name="hint"/>
			温馨提示: 请上传原始比例的身份证正反面, 请勿剪裁涂改,保证身份证信息清晰显示, 否则无法通过审核
		</van-row>
		<van-row gutter="20" class="id_card_row upload_box">
			<van-col span=12>
				<div class="upload_wrap">
					<div v-if="frontUrl">
						<img :src="frontUrl" alt="反面">
					</div>
					<div class="add_btn" v-else>
						<van-icon name="add" />
						<div>上传照片</div>
					</div>
				</div>
			</van-col>
			<van-col span=12>
				<div class="upload_wrap">
					<div v-if="reverseUrl">
						<img :src="reverseUrl" alt="反面">
					</div>
					<div class="add_btn" v-else>
						<van-icon name="add" />
						<div>上传照片</div>
					</div>
				</div>
			</van-col>
		</van-row>
		<van-row gutter="10" class="id_card_row">
			<van-col span=12>
				<div class="text-desc">正面示例</div>
				<img src="../../../assets/images/id_card_front.png" alt="正面" width="50%">
			</van-col>
			<van-col span=12>
				<div class="text-desc">反面示例</div>
				<img src="../../../assets/images/id_card_reverse.png" alt="反面" width="50%">
			</van-col>
		</van-row>
	</div>
</template>

<script>
import { Row, Col } from 'vant';

export default {
  name: 'id-card-upload',

  props: {
    frontUrl: String,
    reverseUrl: String
  },

  components: {
    [Row.name]: Row,
    [Col.name]: Col
  }
};
</script>


<style lang="scss" scoped>
.id_card_upload {
  margin: 10px 0 30px 0;
  background-color: #fff;
  padding: 15px 10px;
  > div {
    margin-bottom: 15px;
  }
}
.upload_hint {
  position: relative;
  padding-left: 20px;
  line-height: 1.6;
  i {
    position: absolute;
    top: 2px;
    left: 0;
  }
}

.id_card_row {
  > div {
    text-align: center;
  }
  .text-desc {
    margin-bottom: 8px;
  }
}

.upload_box .upload_wrap {
  position: relative;
  border: 1px dashed $gray;
  min-height: 100px;
  box-sizing: border-box;
  padding: 5px;
  img {
    max-width: 100%;
    display: block;
  }
  .add_btn {
    position: absolute;
    top: 50%;
    left: 50%;
    font-size: 20px;
    color: $gray;
    transform: translate(-50%, -50%);
    i {
      font-size: 24px;
    }
  }
}
</style>
