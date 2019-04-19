<template>
	<span class="search_tag" @click="OnClick">
		<slot></slot>
	</span>
</template>

<script>
export default {
  name: 'word-tag',
  methods: {
    OnClick() {
      this.$emit('click');
    }
  }
};
</script>

<style lang="scss" scoped>
.search_tag {
  display: inline-block;
  font-size: 12px;
  background-color: #f4f4f4;
  padding: 3px 10px;
  border-radius: 11px;
  min-width: 20px;
  text-align: center;
}
</style>
