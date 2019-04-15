<template>
	<div slot="footer" class="order_list--footer_btn">
		<van-button size="small" @click="$emit('cancel')">取消订单</van-button>
		<van-button size="small" type="danger" @click="$emit('pay')">去支付</van-button>
	</div>
</template>
