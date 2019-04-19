<template>
	<div slot="footer" class="order_list--footer_btn">
		<van-button size="small">查看物流</van-button>
		<van-button size="small" type="danger" @click="$emit('handle', 'receiptOrder')">确认收货</van-button>
	</div>
</template>

<script>
</script>
