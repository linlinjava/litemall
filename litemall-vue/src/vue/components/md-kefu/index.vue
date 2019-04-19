<template>
	<div class="contact_popup">
		<div class="contact_box contact_top">
			<div>微信长按识别店主二维码</div>
			<div><img src="../../../assets/images/qc_code.png" alt="店主二维码"></div>
		</div>
		<div class="contact_box">
			<div><van-icon name="phone" /> {{mobile}}</div>
			<div class="contact_btn"><a :href="'tel:' + mobile">联系店家</a></div>
		</div>
	</div>
</template>

<script>
export default {
  name: 'md-kefu',
  props: {
    qcCode: String,
    mobile: String
  }
};
</script>


<style lang="scss" scoped>
.contact_popup {
  white-space: nowrap;
  background-color: $bg-color;
  text-align: center;
  border-radius: 5px;
  .contact_box {
    padding: 20px 30px;
    > div:first-child {
      margin-bottom: 20px;
    }
  }
  .contact_top {
    @include one-border;
    &::after {
      border-bottom-color: #999;
    }
  }
  .contact_btn {
    border: 1px solid $red;
    width: 80%;
    margin: 0 auto;
    color: #fff;
    background-color: $red;
    padding: 5px 0;
    border-radius: 3px;
    a {
      color: #fff;
      display: block;
    }
  }
  img {
    max-width: 100%;
    max-height: 100%;
  }
}
</style>
