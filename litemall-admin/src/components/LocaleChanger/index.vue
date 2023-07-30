<!-- https://kazupon.github.io/vue-i18n/guide/locale.html -->
<template>
  <el-select v-model="$i18n.locale" size="mini" placeholder="lang" class="select">
    <el-option v-for="(lang, i) in langs" :key="`Lang${i}`" :label="lang.label" :value="lang.value" />
  </el-select>
</template>

<script>
export default {
  name: 'LocaleChanger',
  data() {
    return {
      langs: Object.entries(this.$i18n.messages)
        .map(([key, messages]) => ({
          label: messages.lang ?? key,
          value: key
        }))
    }
  }
}
</script>

<style>
.select {
  width: 180px;
}
</style>
