<template>
  <div>
    <i class="el-icon-full-screen" @click="click" />
  </div>
</template>

<script>
import screenfull from 'screenfull'

export default {
  name: 'Screenfull',
  props: {
    width: {
      type: Number,
      default: 22
    },
    height: {
      type: Number,
      default: 22
    },
    fill: {
      type: String,
      default: '#48576a'
    }
  },
  data() {
    return {
      isFullscreen: false
    }
  },
  methods: {
    click() {
      if (!screenfull.enabled) {
        this.$message({
          message: '浏览器不支持全屏',
          type: 'warning'
        })
        return false
      }
      screenfull.toggle()
    }
  }
}
</script>

<style scoped>
.el-icon-full-screen {
  display: inline-block;
  cursor: pointer;
  fill: #5a5e66;;
  width: 20px;
  height: 20px;
  font-size: 20px;
}
</style>
