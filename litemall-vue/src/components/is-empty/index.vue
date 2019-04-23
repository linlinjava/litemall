<template>
	<div class="is_empty">
		<div>
			<img src="../../assets/images/is_empty.png" alt="无商品" width="20%">
		</div>
		<div>
			<slot></slot>
		</div>
	</div>
</template>

<script>
export default {
  name: 'is-empty'
};
</script>


<style lang="scss" scoped>
.is_empty {
  text-align: center;
  color: $font-color-gray;
  padding-top: 100px;
  > div {
    margin-bottom: 20px;
  }
}
</style>
