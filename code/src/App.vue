<!--code/src/App.vue-->
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>

import { api } from '@/api/index.js'
window.api = api

</script>

<style>
/* 全局背景与字体 */
body, html, #app {
  height: 100%;
  margin: 0;
  font-family: "微软雅黑", "PingFang SC", "Helvetica Neue", Arial;
  background: #f9f1e5;
  color: #222;
}
</style>
