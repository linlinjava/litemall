<template>
<div id="app">
  <keep-alive>
    <router-view class="view-router"  v-if="$route.meta.keepAlive"></router-view>
  </keep-alive>
  <router-view class="view-router" v-if="!$route.meta.keepAlive"></router-view>
  <router-view name="tabbar"></router-view>
</div>
</template>

<style lang="scss" src="./assets/scss/global.scss" />
